package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.util.json.DecoratedJsonArray;
import it.polimi.ingsw.util.json.DecoratedJsonObject;
import it.polimi.ingsw.util.json.JsonObjectGenerator;
import it.polimi.ingsw.util.json.JsonPathGenerator;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.network.server.VirtualClient;
import it.polimi.ingsw.network.server.lobby.Lobby;
import it.polimi.ingsw.view.remote.cli.ColorPrinter;
import it.polimi.ingsw.view.virtual.VirtualView;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.util.Table;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.virtual.cli.CliCommon;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class handles at a high level everything concerning the game logic and interfaces with the {@link VirtualView}
 * to update {@link VirtualClient}s on its status, which is in turn modified by the {@link Controller}.
 */
public class Game {
    /**
     * The minimum amount of players for an online match.
     */
    public static final int MINIMUM_PLAYER_COUNT = 3;

    /**
     * Allows to play a game without depending on the client-server architecture. Enabling this flag
     * allows integration testing of the game only, without indirectly testing external aspects as well.
     */
    public static boolean offlineMode = false;

    /**
     * Intercepts requests to the player and instantly provides the awaiting method with a random value
     * between the legal choices. Enabling this flag allows to automatically run, and therefore test very
     * quickly, complete games in less than a second each.
     */
    public static boolean autoPilot = false;

    /**
     * Disables all output. Enabling this flag significantly reduces execution times in some of the beefier
     * tests, since removing calls to the {@code System.out} saves a few hundred clock cycles per removed call.
     */
    public static boolean silent = false;

    /**
     * Whether or not the standard game should be followed by an additional round in Final Frenzy mode.
     */
    private boolean finalFrenzy;

    /**
     * The number of kills left before the game ends or enters the Final Frenzy. This also represents
     * the number of skulls on the killshot track.
     */
    private int roundsLeft;

    /**
     * Whether or not the game has already started. This allows the {@link Lobby} to know that a game has
     * started, so as not to modify the list of {@link Game#participants}.
     */
    private boolean started;

    /**
     * Whether or not the game has ended. This occurs after {@link Game#roundsLeft} drops to {@code 0} in Sudden Death
     * mode, or after a full round is played in Final Frenzy mode.
     */
    private boolean gameOver;

    /**
     * The {@link Player}s involved in the game.
     */
    private List<Player> participants;

    /**
     * The id of the player whose turn it is, inside the {@link Game#participants} list.
     */
    private int currentTurnPlayer;

    /**
     * The id of the {@link Board} schematic the game is being played on. This is never used during a game
     * but needs to be saved in order to reload the game, should the {@link Server} go down.
     */
    private int boardType;

    /**
     * The board the game is being played on. This defines all possible movements.
     */
    private Board board;

    /**
     * A reference to the game's {@link VirtualView} through which the model sends requests to {@link VirtualClient}s.
     */
    private VirtualView virtualView;

    /**
     * This is the only constructor.
     *
     * @param finalFrenzy  whether to enable {@link Game#finalFrenzy}.
     * @param roundsToPlay the number of rounds to play, also the initial value {@link Game#roundsLeft}.
     * @param boardType    the id of the type of {@link Board} to play the game on.
     * @param participants the list of {@link Game#participants}.
     */
    private Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.gameOver = false;
        this.participants = participants;
        this.currentTurnPlayer = 0;
        this.boardType = boardType;
        this.board = Board.generate(this, boardType);
        this.virtualView = new VirtualView(this);
        this.started = false;
    }

    /**
     * This factory method creates a new {@code Game} with the settings specified by the arguments. It then
     * proceeds to bind each of the {@code participants} to the {@code Game}.
     *
     * @param finalFrenzy  whether to enable {@link Game#finalFrenzy}.
     * @param roundsToPlay the number of rounds to play, also the initial value {@link Game#roundsLeft}.
     * @param boardType    the id of the type of {@link Board} to play the game on.
     * @param participants the list of {@link Game#participants}.
     * @return the {@code Game}.
     */
    public static Game create(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        Game game = new Game(finalFrenzy, roundsToPlay, boardType, participants);
        participants.forEach(p -> p.setGame(game));
        return game;
    }

    /**
     * Gets the list containing the {@link Game#participants} of the {@code Game}.
     *
     * @return the list.
     */
    public List<Player> getParticipants() {
        return participants;
    }

    /**
     * Gets the {@link Game#board} the {@code Game} is played on.
     *
     * @return the {@link Board}.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Gets the number of {@link #roundsLeft}.
     *
     * @return the number of rounds left to play.
     */
    public int getRoundsLeft() {
        return roundsLeft;
    }

    /**
     * Gets the {@link Game#virtualView} bound to the {@code Game}.
     *
     * @return the {@link VirtualView}.
     */
    public VirtualView getVirtualView() {
        return virtualView;
    }

    /**
     * Tells whether the game has {@link Game#started}.
     *
     * @return {@code true} if it has.
     */
    public boolean isStarted() {
        return this.started;
    }

    /**
     * Executes a sequence of internal methods that form one complete turn.
     */
    private void playTurn() {
        board.spreadAmmo();
        board.spreadWeapons();

        Player subject = participants.get(currentTurnPlayer);

        // verify at least a certain amount of players are still connected
        virtualView.announceTurn(subject); // this should update all connection flags on virtual clients

        int onlinePlayerCount = (int) participants.stream()
                .filter(Player::isConnected)
                .count();

        boolean enoughPlayers = onlinePlayerCount >= MINIMUM_PLAYER_COUNT;

        if (!enoughPlayers && !offlineMode) { // end game if there are not enough participants
            gameOver = true;
            return;
        }

        subject.beginTurn();

        if (subject.getPosition() == null) { // player is dead, start respawn routine
            board.getPowerUpDeck().smartDraw(true).ifPresent(c -> {
                try {
                    subject.givePowerUp(c);
                } catch (FullHandException ignored) {
                } // discarding will be part of the respawn mechanic
            });

            if (subject.getDeathCount() == 0) // if subject hasn't died yet -- it's the entry spawn, draw twice
                board.getPowerUpDeck().smartDraw(true).ifPresent(c -> {
                    try {
                        subject.givePowerUp(c);
                    } catch (FullHandException ignored) {
                    } // discarding will be part of the respawn mechanic
                });

            try {
                virtualView.spawn(subject);
            } catch (AbortedTurnException ignored) { // in case of aborted turn, return to the caller
                virtualView.announceDisconnect(subject);
                return;
            }
        }

        subject.savePosition();
        while (subject.getRemainingExecutions() > 0) { // a turn is made by several executions
            try {
                virtualView.usePowerUp(subject);
            } catch (AbortedTurnException e) {
                virtualView.announceDisconnect(subject);
                return;
            }
            List<Execution> options = Execution.getOptionsForPlayer(subject);
            Execution choice;
            try {
                choice = virtualView.chooseExecution(subject, options);
            } catch (AbortedTurnException e) {
                virtualView.announceDisconnect(subject);
                return;
            }

            for (Activity activity : choice.getActivities()) { // each execution consists of some activities
                switch (activity.getType()) {
                    case MOVE:
                        int maxDistance = ((Move) activity).getMaxDistance();
                        List<Cell> validDestinations = board.getCells()
                                .stream()
                                .filter(c -> {
                                    try {
                                        return c.distance(subject.getPosition()) <= maxDistance;
                                    } catch (NullCellOperationException e) {
                                        return false;
                                    }
                                })
                                .collect(Collectors.toList());
                        try {
                            virtualView.move(subject, validDestinations);
                        } catch (AbortedTurnException e) {
                            virtualView.announceDisconnect(subject);
                            return;
                        }
                        break;

                    case GRAB:
                        Cell position = subject.getPosition();
                        if (position.isSpawnPoint()) {
                            try {
                                virtualView.grabWeapon(subject);
                            } catch (AbortedTurnException e) {
                                virtualView.announceDisconnect(subject);
                                return;
                            }
                        } else {
                            try {
                                virtualView.grabAmmo(subject);
                            } catch (AbortedTurnException e) {
                                virtualView.announceDisconnect(subject);
                                return;
                            }
                        }
                        break;

                    case SHOOT:
                        try {
                            virtualView.shoot(subject);
                        } catch (AbortedTurnException e) {
                            virtualView.announceDisconnect(subject);
                            return;
                        }
                        break;

                    case RELOAD:
                        try {
                            virtualView.reload(subject);
                        } catch (AbortedTurnException e) {
                            virtualView.announceDisconnect(subject);
                            return;
                        }
                        break;

                    default:
                        break;
                }
            }
            subject.endExecution();
        }

        participants.stream() // score all dead players
                .filter(Player::isKilled)
                .forEach(p -> {
                    p.scoreDamageTrack();
                    p.die();
                    if (roundsLeft > 0)
                        roundsLeft--;
                    if (finalFrenzy && roundsLeft == 0) {
                        p.activateFrenzy();
                        p.setDeathCount(0);
                    }
                    board.getPowerUpDeck().smartDraw(true).ifPresent(c -> {
                        try {
                            p.givePowerUp(c);
                        } catch (FullHandException ignored) {
                        } // discarding will be part of the respawn process
                    });
                    try {
                        virtualView.spawn(p);
                    } catch (AbortedTurnException ignored) {
                    } // in case of lost connection, don't spawn
                });

        board.promoteDoubleKillers();

        if (roundsLeft == 0) { // when the last skull has been removed from the killshot track
            if (finalFrenzy && !subject.causedFrenzy()) {
                if (participants.stream().noneMatch(Player::causedFrenzy)) {
                    virtualView.announceFrenzy(subject);
                    subject.causeFrenzy();
                }
            } else {
                gameOver = true;
                invalidateSaveState();
            }
        }

        this.currentTurnPlayer = (this.currentTurnPlayer + 1) % this.participants.size(); // pass the turn on to the next player
        save(); // save the game state at the end of each turn
    }

    /**
     * Starts the game.
     */
    public void play() {
        this.started = true;

        while (!gameOver) {
            this.playTurn();
        }
        board.scoreUponGameOver();

        this.started = false;

        // now to declare the winner
        List<Player> ranking = participants.stream()
                .sorted((p1, p2) -> p2.getScore() - p1.getScore())
                .collect(Collectors.toList());

        virtualView.announceWinner(ranking);
    }

    /**
     * Saves the game status to a {@code .json} file.
     */
    public void save() {
        DecoratedJsonObject jSaved = new DecoratedJsonObject();

        jSaved.putValue("valid", true);
        jSaved.putValue("finalFrenzy", finalFrenzy);
        jSaved.putValue("roundsLeft", roundsLeft);
        jSaved.putValue("currentTurnPlayer", currentTurnPlayer);
        jSaved.putValue("boardType", boardType);

        DecoratedJsonArray jKillers = new DecoratedJsonArray(
                board.getKillers().stream().map(p -> {
                    if (p == null)
                        return new DecoratedJsonObject("id", -1);
                    return new DecoratedJsonObject("id", p.getId());
                }).collect(Collectors.toList()),
                null
        );
        jSaved.putArray("killers", jKillers);

        DecoratedJsonArray jDoubleKillers = new DecoratedJsonArray(
                board.getDoubleKillers().stream().map(p -> {
                    if (p == null)
                        return new DecoratedJsonObject("id", -1);
                    return new DecoratedJsonObject("id", p.getId());
                }).collect(Collectors.toList()),
                null
        );
        jSaved.putArray("doubleKillers", jDoubleKillers);

        DecoratedJsonArray jCells = new DecoratedJsonArray(
                board.getCells().stream().map(c -> {
                    DecoratedJsonObject jCell = new DecoratedJsonObject();
                    jCell.putValue("id", c.getId());
                    if (c.isSpawnPoint()) {
                        DecoratedJsonArray jWeaponShop = new DecoratedJsonArray(
                                ((SpawnCell) c).getWeaponShop()
                                        .stream()
                                        .map(w -> new DecoratedJsonObject("name", w.getName()))
                                        .collect(Collectors.toList()),
                                null
                        );
                        jCell.putArray("weaponShop", jWeaponShop);
                    } else {
                        AmmoTile ammoTile = ((AmmoCell) c).getAmmoTile();
                        DecoratedJsonObject jAmmoTile = new DecoratedJsonObject();
                        if (ammoTile != null) {
                            jAmmoTile.putValue("red", ammoTile.getAmmoCubes().getRed());
                            jAmmoTile.putValue("yellow", ammoTile.getAmmoCubes().getYellow());
                            jAmmoTile.putValue("blue", ammoTile.getAmmoCubes().getBlue());
                            jAmmoTile.putValue("includesPowerUp", ammoTile.includesPowerUp());
                            jCell.putObject("ammoTile", jAmmoTile);
                        } else {
                            jCell.putValue("ammoTile", null);
                        }
                    }
                    return jCell;
                }).collect(Collectors.toList()),
                null
        );
        jSaved.putArray("cells", jCells);

        DecoratedJsonArray jParticipants = new DecoratedJsonArray(
                participants.stream().map(p -> {
                    DecoratedJsonObject jPlayer = new DecoratedJsonObject();
                    jPlayer.putValue("id", p.getId());
                    jPlayer.putValue("name", p.getName());
                    jPlayer.putValue("score", p.getScore());
                    jPlayer.putValue("deathCount", p.getDeathCount());
                    jPlayer.putValue("onFrenzy", p.isOnFrenzy());
                    jPlayer.putValue("causedFrenzy", p.causedFrenzy());
                    jPlayer.putArray("damage", new DecoratedJsonArray(
                            p.getDamageAsList()
                                    .stream()
                                    .map(Player::getId)
                                    .map(i -> new DecoratedJsonObject("id", i))
                                    .collect(Collectors.toList()),
                            null
                    ));
                    jPlayer.putArray("markings", new DecoratedJsonArray(
                            p.getMarkingsAsList()
                                    .stream()
                                    .map(Player::getId)
                                    .map(i -> new DecoratedJsonObject("id", i))
                                    .collect(Collectors.toList()),
                            null
                    ));
                    jPlayer.putArray("weapons", new DecoratedJsonArray(
                            p.getWeapons()
                                    .stream()
                                    .map(Weapon::getName)
                                    .map(s -> new DecoratedJsonObject("name", s))
                                    .collect(Collectors.toList()),
                            null
                    ));
                    jPlayer.putArray("powerUps", new DecoratedJsonArray(
                            p.getPowerUps()
                                    .stream()
                                    .map(pow -> {
                                        DecoratedJsonObject jPowerUp = new DecoratedJsonObject();
                                        jPowerUp.putValue("type", pow.getType().toString().toLowerCase());
                                        jPowerUp.putValue("color", pow.getAmmoCubes().toStringAsColor());
                                        return jPowerUp;
                                    })
                                    .collect(Collectors.toList()),
                            null
                    ));
                    DecoratedJsonObject jAmmoCubes = new DecoratedJsonObject();
                    jAmmoCubes.putValue("red", p.getAmmoCubes().getRed());
                    jAmmoCubes.putValue("yellow", p.getAmmoCubes().getYellow());
                    jAmmoCubes.putValue("blue", p.getAmmoCubes().getBlue());
                    jPlayer.putObject("ammoCubes", jAmmoCubes);

                    jPlayer.putValue("position", p.getPosition() == null ? -1 : p.getPosition().getId());

                    return jPlayer;
                }).collect(Collectors.toList()),
                null
        );
        jSaved.putArray("participants", jParticipants);

        DecoratedJsonObject tl = new DecoratedJsonObject();
        tl.putObject("saved", jSaved);
        tl.writeToFile(JsonPathGenerator.getFile("saved.json"));
    }

    /**
     * Taints the current save state for this {@code Game} by applying an invalidity tag. This is done
     * to prevent the option of replaying the last round of a {@code Game} that has already come to a conclusion.
     */
    public void invalidateSaveState() {
        DecoratedJsonObject jSaved = new DecoratedJsonObject();

        jSaved.putValue("valid", false);

        DecoratedJsonObject tl = new DecoratedJsonObject();
        tl.putObject("saved", jSaved);
        tl.writeToFile(JsonPathGenerator.getFile("saved.json"));
    }

    /**
     * This factory method creates a new {@code Game} based on a save state retrieved from a {@code .json} file.
     *
     * @param shuffledParticipants The list of {@link Player}s attempting to join the reloaded {@code Game}.
     * @return The {@code Game}.
     * @throws InvalidSaveStateException           when attempting to load from an invalidated save state.
     * @throws UnmatchedSavedParticipantsException when the list of joining {@link Player}s is not a permutation
     *                                             of the list of {@link Player}s found in the save state.
     */
    public static Game load(List<Player> shuffledParticipants) throws InvalidSaveStateException, UnmatchedSavedParticipantsException {
        DecoratedJsonObject jSaveState = JsonObjectGenerator.getSavedGameBuilder();

        if (shuffledParticipants == null)
            throw new NullPointerException("Tried to load a game with participants set to null.");
        DecoratedJsonObject jSaved;
        try {
            jSaved = jSaveState.getObject("saved");
        } catch (JullPointerException e) {
            throw new JsonException("Savestate JSON does not include \"saved\" as top-level object.");
        }

        boolean valid;
        try {
            valid = jSaved.getBoolean("valid");
        } catch (JullPointerException e) {
            throw new JsonException("Savestate valid flag not found.");
        }

        if (!valid)
            throw new InvalidSaveStateException("Attempted to load a save state marked as invalid.");

        List<String> savedPlayerNames;
        try {
            savedPlayerNames = jSaved.getArray("participants")
                    .toList()
                    .stream()
                    .map(djo -> {
                        try {
                            return djo.getString("name");
                        } catch (JullPointerException e) {
                            throw new JsonException("DecoratedJsonObject in \"participants\": name not found.");
                        }
                    })
                    .collect(Collectors.toList());
        } catch (JullPointerException e) {
            throw new JsonException("Savestate participants not found.");
        }

        List<String> playerNames = shuffledParticipants.stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        if (savedPlayerNames.size() != playerNames.size())
            throw new UnmatchedSavedParticipantsException("The saved game expected " + savedPlayerNames.size() + " players, but found " + playerNames.size() + " instead.");

        Optional<String> missingPlayer = savedPlayerNames.stream().filter(s -> !playerNames.contains(s)).findFirst();
        if (missingPlayer.isPresent())
            throw new UnmatchedSavedParticipantsException("The saved game expected player \"" + missingPlayer + "\" who was not found in the participants.");

        Optional<String> intrusivePlayer = playerNames.stream().filter(s -> !savedPlayerNames.contains(s)).findFirst();
        if (intrusivePlayer.isPresent())
            throw new UnmatchedSavedParticipantsException("The saved game did not expect a player named \"" + intrusivePlayer + "\".");

        final List<Player> participants = shuffledParticipants.stream() // match the saved order
                .sorted(Comparator.comparingInt(p -> savedPlayerNames.indexOf(p.getName())))
                .collect(Collectors.toList());

        boolean finalFrenzy;
        try {
            finalFrenzy = jSaved.getBoolean("finalFrenzy");
        } catch (JullPointerException e) {
            throw new JsonException("Savestate finalFrenzy not found.");
        }
        int roundsLeft;
        try {
            roundsLeft = jSaved.getInt("roundsLeft");
        } catch (JullPointerException e) {
            throw new JsonException("Savestate roundsLeft not found.");
        }
        int currentTurnPlayer;
        try {
            currentTurnPlayer = jSaved.getInt("currentTurnPlayer");
        } catch (JullPointerException e) {
            throw new JsonException("Savestate currentTurnPlayer not found.");
        }
        int boardType;
        try {
            boardType = jSaved.getInt("boardType");
        } catch (JullPointerException e) {
            throw new JsonException("Savestate boardType not found.");
        }

        Game game = Game.create(finalFrenzy, roundsLeft, boardType, participants);
        game.currentTurnPlayer = currentTurnPlayer;

        List<DecoratedJsonObject> jKillers;
        try {
            jKillers = jSaved.getArray("killers").toList();
        } catch (JullPointerException e) {
            throw new JsonException("Savestate killers not found.");
        }
        List<Player> killers = jKillers.stream()
                .map(djo -> {
                    try {
                        return djo.getInt("id") - 1;
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate killer id not found.");
                    }
                })
                .map(participants::get)
                .collect(Collectors.toList());
        game.getBoard().setKillers(killers);


        List<DecoratedJsonObject> jDoubleKillers;
        try {
            jDoubleKillers = jSaved.getArray("doubleKillers").toList();
        } catch (JullPointerException e) {
            throw new JsonException("Savestate doubleKillers not found.");
        }
        List<Player> doubleKillers = jDoubleKillers.stream()
                .map(djo -> {
                    try {
                        return djo.getInt("id") - 1;
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate doubleKiller id not found.");
                    }
                })
                .map(participants::get)
                .collect(Collectors.toList());
        game.getBoard().setDoubleKillers(doubleKillers);

        List<DecoratedJsonObject> jCells;
        try {
            jCells = jSaved.getArray("cells").toList();
        } catch (JullPointerException e) {
            throw new JsonException("Savestate cells not found.");
        }
        jCells.forEach(
                jc -> {
                    int id;
                    try {
                        id = jc.getInt("id");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate cell id not found.");
                    }
                    Cell cell = game.getBoard().getCells().get(id - 1);
                    if (cell.isSpawnPoint()) {
                        try {
                            jc.getArray("weaponShop")
                                    .toList()
                                    .stream()
                                    .map(djo -> {
                                        try {
                                            return djo.getString("name");
                                        } catch (JullPointerException e) {
                                            throw new JsonException("Savestate cell weaponShop weapon name not found.");
                                        }
                                    })
                                    .map(s -> game.getBoard().fetchWeapon(s))
                                    .forEach(
                                            opt -> opt.ifPresent(((SpawnCell) cell)::addToWeaponShop)
                                    );
                        } catch (JullPointerException e) {
                            throw new JsonException("Savestate cell weaponShop not found.");
                        }
                    } else {
                        DecoratedJsonObject jAmmoTile;
                        try {
                            jAmmoTile = jc.getObject("ammoTile");
                        } catch (JullPointerException e) {
                            throw new JsonException("Savestate cell ammoTile not found.");
                        }
                        if (!jAmmoTile.isEmpty()) {
                            int red;
                            try {
                                red = jAmmoTile.getInt("red");
                            } catch (JullPointerException e) {
                                throw new JsonException("Savestate cell ammoTile red not found.");
                            }
                            int yellow;
                            try {
                                yellow = jAmmoTile.getInt("yellow");
                            } catch (JullPointerException e) {
                                throw new JsonException("Savestate cell ammoTile yellow not found.");
                            }
                            int blue;
                            try {
                                blue = jAmmoTile.getInt("blue");
                            } catch (JullPointerException e) {
                                throw new JsonException("Savestate cell ammoTile blue not found.");
                            }
                            boolean includesPowerUp;
                            try {
                                includesPowerUp = jAmmoTile.getBoolean("includesPowerUp");
                            } catch (JullPointerException e) {
                                throw new JsonException("Savestate cell ammoTile includesPowerUp not found.");
                            }
                            game.getBoard()
                                    .fetchAmmoTile(red, yellow, blue, includesPowerUp)
                                    .ifPresent(((AmmoCell) cell)::setAmmoTile);
                        } else
                            ((AmmoCell) cell).setAmmoTile(null);
                    }
                }
        );

        List<DecoratedJsonObject> jPlayers;
        try {
            jPlayers = jSaved.getArray("participants").toList();
        } catch (JullPointerException e) {
            throw new JsonException("Savestate participants not found.");
        }
        jPlayers.forEach(
                jp -> {
                    int id;
                    try {
                        id = jp.getInt("id");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant id not found.");
                    }
                    Player player = participants.get(id - 1);

                    int score;
                    try {
                        score = jp.getInt("score");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant score not found.");
                    }
                    player.giveScore(score);

                    int deathCount;
                    try {
                        deathCount = jp.getInt("deathCount");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant deathCount not found.");
                    }
                    player.setDeathCount(deathCount);

                    boolean onFrenzy;
                    try {
                        onFrenzy = jp.getBoolean("onFrenzy");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant onFrenzy not found.");
                    }
                    if (onFrenzy)
                        player.activateFrenzy();

                    boolean causedFrenzy;
                    try {
                        causedFrenzy = jp.getBoolean("causedFrenzy");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant causedFrenzy not found.");
                    }
                    if (causedFrenzy)
                        player.causeFrenzy();

                    try {
                        jp.getArray("damage")
                                .toList()
                                .stream()
                                .map(djo -> {
                                    try {
                                        return djo.getInt("id");
                                    } catch (JullPointerException e) {
                                        throw new JsonException("Savestate participant damage id not found.");
                                    }
                                })
                                .map(pid -> pid - 1)
                                .map(participants::get)
                                .forEach(player::applyDamage);
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant damage not found.");
                    }

                    try {
                        jp.getArray("markings")
                                .toList()
                                .stream()
                                .map(djo -> {
                                    try {
                                        return djo.getInt("id");
                                    } catch (JullPointerException e) {
                                        throw new JsonException("Savestate participant marking id not found.");
                                    }
                                })
                                .map(pid -> pid - 1)
                                .map(participants::get)
                                .forEach(player::applyMarking);
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant markings not found.");
                    }

                    try {
                        jp.getArray("weapons")
                                .toList()
                                .stream()
                                .map(djo -> {
                                    try {
                                        return djo.getString("name");
                                    } catch (JullPointerException e) {
                                        throw new JsonException("Savestate participant weapon name not found.");
                                    }
                                })
                                .map(game.getBoard()::fetchWeapon)
                                .forEach(
                                        opt -> opt.ifPresent(w -> {
                                            try {
                                                player.giveWeapon(w);
                                            } catch (FullHandException ignored) {
                                            }
                                        })
                                );
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant weapons not found.");
                    }

                    try {
                        jp.getArray("powerUps")
                                .toList()
                                .forEach(
                                        djo -> {
                                            String type;
                                            try {
                                                type = djo.getString("type");
                                            } catch (JullPointerException e) {
                                                throw new JsonException("Savestate participant powerUp type not found.");
                                            }
                                            String color;
                                            try {
                                                color = djo.getString("color");
                                            } catch (JullPointerException e) {
                                                throw new JsonException("Savestate participant powerUp color not found.");
                                            }
                                            game.getBoard().fetchPowerUp(type, color).ifPresent(
                                                    pu -> {
                                                        try {
                                                            player.givePowerUp(pu);
                                                        } catch (FullHandException ignored) {
                                                        }
                                                    }
                                            );
                                        }
                                );
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant powerUps not found.");
                    }

                    DecoratedJsonObject jAmmoCubes;
                    try {
                        jAmmoCubes = jp.getObject("ammoCubes");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant ammoCubes not found.");
                    }
                    int red;
                    try {
                        red = jAmmoCubes.getInt("red");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant ammoCubes red not found.");
                    }
                    int yellow;
                    try {
                        yellow = jAmmoCubes.getInt("yellow");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant ammoCubes yellow not found");
                    }
                    int blue;
                    try {
                        blue = jAmmoCubes.getInt("blue");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant ammoCubes blue not found.");
                    }
                    AmmoCubes ammoCubes = new AmmoCubes(red, yellow, blue);
                    player.giveAmmoCubes(ammoCubes);

                    int positionId;
                    try {
                        positionId = jp.getInt("position");
                    } catch (JullPointerException e) {
                        throw new JsonException("Savestate participant position not found.");
                    }
                    Cell position = positionId == -1 ? null : game.getBoard().getCells().get(positionId - 1);
                    player.setPosition(position);

                }
        );
        return game;
    }

    /**
     * Generates a string containing a summary of the game status. The intended use is for
     * synoptic inspection and should not be presented to the end user.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        final int edge = 160;
        StringBuilder s = new StringBuilder("\n\n\n");
        s.append("~".repeat(edge));
        s.append("\n\nGame status:");
        s.append(" finalFrenzy: ").append(finalFrenzy);
        s.append(", roundsLeft: ").append(roundsLeft);
        s.append(", currentPlayer: ").append(participants.get(currentTurnPlayer).getName());
        s.append(", boardType: ").append(boardType);
        s.append("\nKillers: ").append(Table.list(board.getKillers()));
        s.append("\nDoubleKillers: ").append(Table.list(board.getDoubleKillers()));
        s.append("\n\nDecks:");
        s.append("\nWeapons > ").append(board.getWeaponDeck().toString());
        s.append("\nPowerUps > ").append(board.getPowerUpDeck().toString());
        s.append("\nAmmoTiles > ").append(board.getAmmoTileDeck().toString());
        s.append("\n\nCells:\n");
        s.append(Table.create(
                board.getCells().stream().map(Cell::toString).collect(Collectors.toList()),
                board.getCells().stream().map(Cell::getRoom).map(Room::toString).map(str -> "in " + str).collect(Collectors.toList()),
                board.getCells().stream().map(Cell::getAdjacentCells).map(
                        l -> l.stream()
                                .map(Cell::getId)
                                .collect(Collectors.toList())
                ).map(Table::list).map(str -> "adjacent to: " + str).collect(Collectors.toList()),
                board.getCells().stream().map(c -> {
                    if (c.isSpawnPoint())
                        return Table.list(((SpawnCell) c).getWeaponShop()) + " ~~ (this is the " + ((SpawnCell) c).getAmmoCubeColor().toStringAsColor() + " spawnpoint)";
                    else if (((AmmoCell) c).getAmmoTile() != null)
                        return ((AmmoCell) c).getAmmoTile().toString();
                    else
                        return "null";
                }).map(str -> "contains: " + str).collect(Collectors.toList())
        ));
        s.append("\n\nPlayers:\n");
        s.append(Table.create(
                participants.stream().map(p -> p.getId() + ". " + p.getName()).collect(Collectors.toList()),
                participants.stream().map(p -> "@" + p.getPosition()).collect(Collectors.toList()),
                participants.stream().map(p -> "| Damage" +
                        p.getDamageAsList().stream().map(Player::getId).collect(Collectors.toList())
                ).collect(Collectors.toList()),
                participants.stream().map(p -> "Marks" +
                        p.getMarkingsAsList().stream().map(Player::getId).collect(Collectors.toList())
                ).collect(Collectors.toList()),
                participants.stream().map(p -> "| Weapons" +
                        p.getWeapons().stream().map(Weapon::toString).collect(Collectors.toList())
                ).collect(Collectors.toList()),
                participants.stream().map(p -> "PowerUps" +
                        p.getPowerUps().stream().map(PowerUp::toString).collect(Collectors.toList())
                ).collect(Collectors.toList()),
                participants.stream().map(p -> "Ammo[" + p.getAmmoCubes().toString() + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "| " + p.getScore() + " points").collect(Collectors.toList()),
                participants.stream().map(p -> "died " + p.getDeathCount() + " times").collect(Collectors.toList()),
                participants.stream().map(p -> "| " + (p.isOnFrenzy() ? "onFrenzy" : "standard") + (p.causedFrenzy() ? "(C)" : "--")).collect(Collectors.toList())
        ));
        s.append("\n");
        s.append("~".repeat(edge));
        s.append("\n");
        return s.toString();
    }
}
