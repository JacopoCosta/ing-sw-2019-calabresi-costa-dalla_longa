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
                    if(p == null)
                        return new DecoratedJsonObject("id", -1);
                    return new DecoratedJsonObject("id", p.getId());
                }).collect(Collectors.toList()),
                null
        );
        jSaved.putArray("killers", jKillers);

        DecoratedJsonArray jDoubleKillers = new DecoratedJsonArray(
                board.getDoubleKillers().stream().map(p -> {
                    if(p == null)
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
                        if(ammoTile != null) {
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
        DecoratedJsonObject jSaved = tl.getObject("saved");

        boolean valid = jSaved.getBoolean("valid");

        if (!valid)
            throw new InvalidSaveStateException("Attempted to load a save state marked as invalid.");

        List<String> savedPlayerNames = jSaved.getArray("participants")
                .asList()
                .stream()
                .map(djo -> djo.getString("name"))
                .collect(Collectors.toList());

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

        boolean finalFrenzy = jSaved.getBoolean("finalFrenzy");
        try {
            int roundsLeft = jSaved.getInt("roundsLeft");
            int currentTurnPlayer = jSaved.getInt("currentTurnPlayer");
            int boardType = jSaved.getInt("boardType");

            Game game = Game.create(finalFrenzy, roundsLeft, boardType, participants);
            game.currentTurnPlayer = currentTurnPlayer;

            List<DecoratedJsonObject> jKillers = jSaved.getArray("killers").asList();
            List<Player> killers = jKillers.stream()
                    .map(djo -> {
                        try {
                            return djo.getInt("id") - 1;
                        } catch (JullPointerException e) {
                            throw new JsonException("Can't parse killer id.");
                        }
                    })
                    .map(participants::get)
                    .collect(Collectors.toList());
            game.getBoard().setKillers(killers);


            List<DecoratedJsonObject> jDoubleKillers = jSaved.getArray("doubleKillers").asList();
            List<Player> doubleKillers = jDoubleKillers.stream()
                    .map(djo -> {
                        try {
                            return djo.getInt("id") - 1;
                        } catch (JullPointerException e) {
                            throw new JsonException("Can't parse double-killer id.");
                        }
                    })
                    .map(participants::get)
                    .collect(Collectors.toList());
            game.getBoard().setDoubleKillers(doubleKillers);

            List<DecoratedJsonObject> jCells = jSaved.getArray("cells").asList();
            jCells.forEach(
                    jc -> {
                        try {
                            int id = jc.getInt("id");
                            Cell cell = game.getBoard().getCells().get(id - 1);
                            if (cell.isSpawnPoint()) {
                                jc.getArray("weaponShop")
                                        .asList()
                                        .stream()
                                        .map(djo -> djo.getString("name"))
                                        .map(s -> game.getBoard().fetchWeapon(s))
                                        .forEach(
                                                opt -> opt.ifPresent(((SpawnCell) cell)::addToWeaponShop)
                                        );
                            } else {
                                DecoratedJsonObject jAmmoTile = jc.getObject("ammoTile");
                                try {
                                    int red = jAmmoTile.getInt("red");
                                    int yellow = jAmmoTile.getInt("yellow");
                                    int blue = jAmmoTile.getInt("blue");
                                    boolean includesPowerUp = jAmmoTile.getBoolean("includesPowerUp");
                                    game.getBoard()
                                            .fetchAmmoTile(red, yellow, blue, includesPowerUp)
                                            .ifPresent(((AmmoCell) cell)::setAmmoTile);
                                } catch (JullPointerException e) {
                                    ((AmmoCell) cell).setAmmoTile(null);
                                }
                            }
                        } catch (JullPointerException e) {
                            throw new JsonException("Something went wrong");
                        }
                    }
            );

            List<DecoratedJsonObject> jPlayers = jSaved.getArray("participants").asList();
            jPlayers.forEach(
                    jp -> {
                        try {
                            int id = jp.getInt("id");
                            Player player = participants.get(id - 1);

                            int score = jp.getInt("score");
                            player.giveScore(score);

                            int deathCount = jp.getInt("deathCount");
                            player.setDeathCount(deathCount);

                            boolean onFrenzy = jp.getBoolean("onFrenzy");
                            if (onFrenzy)
                                player.activateFrenzy();

                            boolean causedFrenzy = jp.getBoolean("causedFrenzy");
                            if (causedFrenzy)
                                player.causeFrenzy();

                            jp.getArray("damage")
                                    .asList()
                                    .stream()
                                    .map(djo -> {
                                        try {
                                            return djo.getInt("id");
                                        } catch (JullPointerException e) {
                                            throw new JsonException("Jamage from invalid source.");
                                        }
                                    })
                                    .map(pid -> pid - 1)
                                    .map(participants::get)
                                    .forEach(player::applyDamage);

                            jp.getArray("markings")
                                    .asList()
                                    .stream()
                                    .map(djo -> {
                                        try {
                                            return djo.getInt("id");
                                        } catch (JullPointerException e) {
                                            throw new JsonException("Jarking from invalid source");
                                        }
                                    })
                                    .map(pid -> pid - 1)
                                    .map(participants::get)
                                    .forEach(player::applyMarking);

                            jp.getArray("weapons")
                                    .asList()
                                    .stream()
                                    .map(djo -> djo.getString("name"))
                                    .map(game.getBoard()::fetchWeapon)
                                    .forEach(
                                            opt -> opt.ifPresent(w -> {
                                                try {
                                                    player.giveWeapon(w);
                                                } catch (FullHandException ignored) {
                                                }
                                            })
                                    );

                            jp.getArray("powerUps")
                                    .asList()
                                    .forEach(
                                            djo -> {
                                                String type = djo.getString("type");
                                                String color = djo.getString("color");
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

                            DecoratedJsonObject jAmmoCubes = jp.getObject("ammoCubes");
                            int red = jAmmoCubes.getInt("red");
                            int yellow = jAmmoCubes.getInt("yellow");
                            int blue = jAmmoCubes.getInt("blue");
                            AmmoCubes ammoCubes = new AmmoCubes(red, yellow, blue);
                            player.giveAmmoCubes(ammoCubes);

                            int positionId = jp.getInt("position");
                            Cell position = positionId == -1 ? null : game.getBoard().getCells().get(positionId - 1);
                            player.setPosition(position);
                        } catch (JullPointerException e) {
                            throw new JsonException("Something else went wrong");
                        }
                    }
            );
            return game;
        } catch (JullPointerException e) {
            throw new JsonException("Load failed.");
        }
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
        return s.toString();
    }
}
