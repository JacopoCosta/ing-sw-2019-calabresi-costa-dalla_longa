package it.polimi.ingsw.model;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.utilities.DecoratedJsonArray;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;
import it.polimi.ingsw.model.utilities.JsonPathGenerator;
import it.polimi.ingsw.view.virtual.VirtualView;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Game {
    private boolean finalFrenzy;
    private int roundsLeft;
    private boolean gameOver;

    private List<Player> participants;
    private int currentTurnPlayer;

    private int boardType;
    private Board board;
    private VirtualView virtualView;

    private Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.gameOver = false;
        this.participants = participants;
        this.currentTurnPlayer = 0;
        this.boardType = boardType;
        this.board = Board.generate(this, boardType);
        this.virtualView = new VirtualView(this);
    }

    public static Game create(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        Game game = new Game(finalFrenzy, roundsToPlay, boardType, participants);
        participants.forEach(p -> p.setGame(game));
        return game;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public Board getBoard() {
        return this.board;
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    private void playTurn() {
        board.spreadAmmo();
        board.spreadWeapons();

        Player subject = participants.get(currentTurnPlayer); // TODO check still connected

        if (subject.getPosition() == null) {
            List<PowerUp> powerUps = new ArrayList<>();
            for (int i = 0; i <= 1; i++)
                board.getPowerUpDeck().smartDraw(true).ifPresent(powerUps::add);
            virtualView.spawn(subject, powerUps);
        }

        subject.beginTurn();
        subject.savePosition();
        virtualView.usePowerUp(subject);
        while (subject.getRemainingExecutions() > 0) { // a turn is made by several executions
            List<Execution> options = Execution.getOptionsForPlayer(subject);
            Execution choice = virtualView.chooseExecution(subject, options);

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
                        virtualView.move(subject, validDestinations);
                        break;

                    case GRAB:
                        Cell position = subject.getPosition();
                        if (position.isSpawnPoint()) {
                            virtualView.grabWeapon(subject);
                        } else
                            virtualView.grabAmmo(subject);
                        break;

                    case SHOOT:
                        virtualView.shoot(subject);
                        break;

                    case RELOAD:
                        virtualView.reload(subject);
                        break;

                    default:
                        break;
                }
            }
            subject.endExecution();
            virtualView.usePowerUp(subject);
        }

        participants.stream() // score all dead players
                .filter(Player::isKilled)
                .forEach(p -> {
                    p.scoreUponDeath();
                    p.die();
                    if (roundsLeft > 0)
                        roundsLeft--;
                    else if (finalFrenzy)
                        p.activateFrenzy();
                });

        if (roundsLeft == 0) { // when the last skull has been removed from the killshot track
            if (finalFrenzy && !subject.causedFrenzy())
                subject.causeFrenzy();
            else {
                gameOver = true;
                invalidateSaveState();
            }
        }

        this.currentTurnPlayer = (this.currentTurnPlayer + 1) % this.participants.size(); // pass the turn on to the next player
        save(); // save the game state at the end of each turn
    }

    public void play() {
        while (!gameOver)
            this.playTurn();
        board.scoreUponGameOver();

        // now to declare the winner
        Player winner = participants.stream()
                .max(Comparator.comparingInt(Player::getScore))
                .orElse(null); // this should never happen

        // TODO do something with the winner, like a broadcast or something
    }

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
        tl.writeToFile(JsonPathGenerator.getPath("saved.json"));
    }

    public void invalidateSaveState() {
        DecoratedJsonObject jSaved = new DecoratedJsonObject();

        jSaved.putValue("valid", false);

        DecoratedJsonObject tl = new DecoratedJsonObject();
        tl.putObject("saved", jSaved);
        tl.writeToFile(JsonPathGenerator.getPath("saved.json"));
    }

    public static Game load(List<Player> participants) throws InvalidSaveStateException, UnmatchedSavedParticipantsException {
        DecoratedJsonObject tl = DecoratedJsonObject.getFromFile(JsonPathGenerator.getPath("saved.json"));
        DecoratedJsonObject jSaved;
        try {
            jSaved = tl.getObject("saved");
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
                    .asList()
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

        List<String> playerNames = participants.stream()
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
            jKillers = jSaved.getArray("killers").asList();
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
            jDoubleKillers = jSaved.getArray("doubleKillers").asList();
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
            jCells = jSaved.getArray("cells").asList();
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
                                    .asList()
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
                        if(!jAmmoTile.isEmpty()) {
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
                        }
                        else
                            ((AmmoCell) cell).setAmmoTile(null);
                    }
                }
        );

        List<DecoratedJsonObject> jPlayers;
        try {
            jPlayers = jSaved.getArray("participants").asList();
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
                        throw new JsonException("Savestate participant deathCount nout found.");
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
                                .asList()
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
                                .asList()
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
                                .asList()
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
                                .asList()
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
                        throw new JsonException("Savetstate participant ammoCubes blue not found.");
                    }
                    AmmoCubes ammoCubes = new AmmoCubes(red, yellow, blue);
                    player.giveAmmoCubes(ammoCubes);

                    int positionId = 0;
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

    @Override
    public String toString() {
        final int edge = 160;
        StringBuilder s = new StringBuilder("\n\n\n");
        for (int i = 0; i < edge; i++)
            s.append("~");
        s.append("\n\nGame status:");
        s.append(" finalFrenzy: ").append(finalFrenzy);
        s.append(", roundsLeft: ").append(roundsLeft);
        s.append(", currentPlayer: ").append(participants.get(currentTurnPlayer).getName());
        s.append(", boardType: ").append(boardType);
        s.append("\nKillers: ").append(Table.list(board.getKillers()));
        s.append("\nDoubleKillers: ").append(Table.list(board.getDoubleKillers()));
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
        for (int i = 0; i < edge; i++)
            s.append("~");
        s.append("\n");
        return s.toString();
    }
}
