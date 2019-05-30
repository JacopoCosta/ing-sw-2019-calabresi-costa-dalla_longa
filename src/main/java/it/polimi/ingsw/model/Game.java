package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.exceptions.InvalidSaveStateException;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.exceptions.UnmatchedSavedParticipantsException;
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
    private Controller controller;
    private VirtualView virtualView;

    private Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.gameOver = false;
        this.participants = participants;
        this.currentTurnPlayer = 0;
        this.boardType = boardType;
        this.board = Board.generate(this, boardType);
        this.controller = new Controller(this);
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

    public Controller getController() {
        return controller;
    }

    public VirtualView getVirtualView() {
        return virtualView;
    }

    public void setup() {
        board.spreadAmmo();
        board.spreadWeapons();
    }

    public void playTurn() {
        Player subject = participants.get(currentTurnPlayer); // TODO check still connected

        save();

        if(subject.getPosition() == null) {
            List<PowerUp> powerUps = new ArrayList<>();
            for(int i = 0; i <= 1; i ++)
                powerUps.add(board.getPowerUpDeck().smartDraw(true).orElse(null)); //TODO i don't like null here

            virtualView.spawn(subject, powerUps);
        }

        subject.beginTurn();
        subject.savePosition();
        virtualView.usePowerUp(subject);
        while(subject.getRemainingExecutions() > 0) { // a turn is made by several executions
            List<Execution> options = Execution.getOptionsForPlayer(subject);
            Execution choice = virtualView.chooseExecution(subject, options);

            for(Activity activity : choice.getActivities()) { // each execution consists of some activities

                switch(activity.getType()) {
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
                        if(position.isSpawnPoint()) {
                            virtualView.grabWeapon(subject);
                        }
                        else
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
                .forEach(Player::scoreUponDeath);

        this.currentTurnPlayer = (this.currentTurnPlayer + 1) % this.participants.size();
    }

    public void play() {
        setup();
        while(!gameOver) {
            this.playTurn();
        }
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
                    try {
                        return new DecoratedJsonObject("id", p.getId());
                    } catch (NullPointerException e) {
                        return new DecoratedJsonObject("id", -1);
                    }
                }).collect(Collectors.toList()),
                null
        );
        jSaved.putArray("killers", jKillers);

        DecoratedJsonArray jDoubleKillers = new DecoratedJsonArray(
                board.getDoubleKillers().stream().map(p -> {
                    try {
                        return new DecoratedJsonObject("id" ,p.getId());
                    } catch (NullPointerException e) {
                        return new DecoratedJsonObject("id", -1);
                    }
                }).collect(Collectors.toList()),
                null
        );
        jSaved.putArray("doubleKillers", jDoubleKillers);

        DecoratedJsonArray jCells = new DecoratedJsonArray(
                board.getCells().stream().map(c -> {
                    DecoratedJsonObject jCell = new DecoratedJsonObject();
                    jCell.putValue("id", c.getId());
                    if(c.isSpawnPoint()) {
                        DecoratedJsonArray jWeaponShop = new DecoratedJsonArray(
                                ((SpawnCell) c).getWeaponShop()
                                        .stream()
                                        .map(w -> new DecoratedJsonObject("name", w.getName()))
                                        .collect(Collectors.toList()),
                                null
                        );
                        jCell.putArray("weaponShop", jWeaponShop);
                    }
                    else {
                        AmmoTile ammoTile = ((AmmoCell) c).getAmmoTile();
                        DecoratedJsonObject jAmmoTile = new DecoratedJsonObject();
                        try {
                            jAmmoTile.putValue("red", ammoTile.getAmmoCubes().getRed());
                            jAmmoTile.putValue("yellow", ammoTile.getAmmoCubes().getYellow());
                            jAmmoTile.putValue("blue", ammoTile.getAmmoCubes().getBlue());
                            jAmmoTile.putValue("includesPowerUp", ammoTile.includesPowerUp());
                            jCell.putObject("ammoTile", jAmmoTile);
                        } catch (NullPointerException e) {
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
                    jPlayer.putValue("onFrenzyBeforeStartingPlayer", p.isOnFrenzyBeforeStartingPlayer());
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

        if(!valid)
            throw new InvalidSaveStateException("Attempted to load a save state marked as invalid.");

        List<String> savedPlayerNames = jSaved.getArray("participants")
                .asList()
                .stream()
                .map(djo -> djo.getString("name"))
                .collect(Collectors.toList());

        List<String> playerNames = participants.stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        if(savedPlayerNames.size() != playerNames.size())
            throw new UnmatchedSavedParticipantsException("The saved game expected " + savedPlayerNames.size() + " players, but found " + playerNames.size() + " instead.");

        Optional<String> missingPlayer = savedPlayerNames.stream().filter(s -> !playerNames.contains(s)).findFirst();
        if(missingPlayer.isPresent())
            throw new UnmatchedSavedParticipantsException("The saved game expected player \"" + missingPlayer + "\" who was not found in the participants.");

        Optional<String> intrusivePlayer = playerNames.stream().filter(s -> !savedPlayerNames.contains(s)).findFirst();
        if(intrusivePlayer.isPresent())
            throw new UnmatchedSavedParticipantsException("The saved game did not expect a player named \"" + intrusivePlayer + "\".");

        boolean finalFrenzy = jSaved.getBoolean("finalFrenzy");
        int roundsLeft = jSaved.getInt("roundsLeft");
        int currentTurnPlayer = jSaved.getInt("currentTurnPlayer");
        int boardType = jSaved.getInt("boardType");

        Game game = Game.create(finalFrenzy, roundsLeft, boardType, participants);
        game.currentTurnPlayer = currentTurnPlayer;

        List<DecoratedJsonObject> jKillers = jSaved.getArray("killers").asList();
        List<Player> killers = jKillers.stream()
                .map(djo -> djo.getInt("id") - 1)
                .map(participants::get)
                .collect(Collectors.toList());
        game.getBoard().setKillers(killers);

        List<DecoratedJsonObject> jDoubleKillers = jSaved.getArray("doubleKillers").asList();
        List<Player> doubleKillers = jDoubleKillers.stream()
                .map(djo -> djo.getInt("id") - 1)
                .map(participants::get)
                .collect(Collectors.toList());
        game.getBoard().setKillers(doubleKillers);

        List<DecoratedJsonObject> jCells = jSaved.getArray("cells").asList();
        jCells.forEach(
                jc -> {
                    int id = jc.getInt("id");
                    Cell cell = game.getBoard().getCells().get(id - 1);
                    if(cell.isSpawnPoint()) {
                        jc.getArray("weaponShop")
                                .asList()
                                .stream()
                                .map(djo -> djo.getString("name"))
                                .map(s -> game.getBoard().fetchWeapon(s))
                                .forEach(
                                        opt -> opt.ifPresent(((SpawnCell) cell)::addToWeaponShop)
                                );
                    }
                    else {
                        DecoratedJsonObject jAmmoTile = jc.getObject("ammoTile");
                        int red = jAmmoTile.getInt("red");
                        int yellow = jAmmoTile.getInt("yellow");
                        int blue = jAmmoTile.getInt("blue");
                        boolean includesPowerUp = jAmmoTile.getBoolean("includesPowerUp");
                        game.getBoard()
                                .fetchAmmoTile(red, yellow, blue, includesPowerUp)
                                .ifPresent(((AmmoCell) cell)::setAmmoTile);
                    }
                }
        );

        List<DecoratedJsonObject> jPlayers = jSaved.getArray("participants").asList();
        jPlayers.forEach(
                jp -> {
                    int id = jp.getInt("id");
                    Player player = participants.get(id - 1);

                    int score = jp.getInt("score");
                    player.giveScore(score);

                    int deathCount = jp.getInt("deathCount");
                    player.setDeathCount(deathCount);

                    boolean onFrenzy = jp.getBoolean("onFrenzy");
                    if(onFrenzy)
                        player.activateFrenzy();

                    //TODO manage starting player

                    List<Player> damage = jp.getArray("damage")
                            .asList()
                            .stream()
                            .map(djo -> djo.getInt("id"))
                            .map(pid -> pid - 1)
                            .map(participants::get)
                            .collect(Collectors.toList());
                    player.setDamage(damage);

                    List<Player> markings = jp.getArray("markings")
                            .asList()
                            .stream()
                            .map(djo -> djo.getInt("id"))
                            .map(pid -> pid - 1)
                            .map(participants::get)
                            .collect(Collectors.toList());
                    player.setMarkings(markings);

                    jp.getArray("weapons")
                            .asList()
                            .stream()
                            .map(djo -> djo.getString("name"))
                            .map(game.getBoard()::fetchWeapon)
                            .forEach(
                                    opt -> opt.ifPresent(w -> {
                                        try {
                                            player.giveWeapon(w);
                                        } catch(FullHandException ignored) {}
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
                                                    } catch (FullHandException ignored) {}
                                                }
                                        );
                                    }
                            );
                }
        );


        return game;
    }

    @Override
    public String toString() {
        String s = "Board:\n";
        s += board.toString() + "\n";

        s += "Weapons : " + board.getWeaponDeck().toString() + "\n";
        s += "Power-Ups : " + board.getPowerUpDeck().toString() + "\n";
        s += "Ammo Tiles : " + board.getAmmoTileDeck().toString() + "\n";

        s += "Players:\n";
        s += Table.create(
                participants.stream().map(p -> "#" + p.getId()).collect(Collectors.toList()),
                participants.stream().map(Player::getName).collect(Collectors.toList()),
                participants.stream().map(Player::getScore).collect(Collectors.toList()),
                participants.stream().map(Player::getPosition).map(c -> "@" +  (c == null ? "null" : c.getId())).collect(Collectors.toList()),
                participants.stream().map(p -> "Damage[" +
                        Table.list(p.getDamageAsList().stream().map(Player::getId).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "Marks[" +
                        Table.list(p.getMarkingsAsList().stream().map(Player::getId).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "| Weapons[" +
                        Table.list(p.getWeapons().stream().map(Weapon::getName).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "Power-ups[" +
                        Table.list(p.getPowerUps().stream().map(PowerUp::toString).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "Ammo[" + p.getAmmoCubes() + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "| died " + p.getDeathCount() + " times").collect(Collectors.toList()),
                participants.stream().map(p -> p.isOnFrenzy() ? "(frenzy activated)" : "").collect(Collectors.toList())
        ) + "\n";

        s += "There are " + roundsLeft + " skulls on the killshot track.\n";
        s += "Currently playing: " + participants.get(currentTurnPlayer).getName() + " with " + participants.get(currentTurnPlayer).getRemainingExecutions() + " actions left.";

        return s;
    }
}
