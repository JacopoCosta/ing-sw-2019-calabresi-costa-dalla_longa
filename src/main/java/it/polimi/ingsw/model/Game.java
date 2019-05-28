package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
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
import java.util.stream.Collectors;

public class Game {
    private boolean finalFrenzy;
    private int roundsLeft;
    private boolean gameOver;

    private List<Player> participants;
    private int currentTurnPlayer;

    private Board board;
    private Controller controller;
    private VirtualView virtualView;

    private Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.gameOver = false;
        this.participants = participants;
        this.currentTurnPlayer = 0;
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
        DecoratedJsonObject saved = new DecoratedJsonObject();
        DecoratedJsonObject testObject = new DecoratedJsonObject();
        testObject.putValue("testKey", "testValue");
        saved.putObject("saved", testObject);
        saved.writeToFile(JsonPathGenerator.getPath("saved.json"));
    }

    public void load() {
        DecoratedJsonObject saved = DecoratedJsonObject.getFromFile("saved.json");
        DecoratedJsonObject testObject = saved.getObject("saved");
        System.out.println(testObject.getString("testObject"));
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
