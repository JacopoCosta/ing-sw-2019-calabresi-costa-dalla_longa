package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.DistanceFromNullException;
import it.polimi.ingsw.view.virtual.VirtualView;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.exceptions.FullHandException;
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

    public Board getBoard() {
        return this.board;
    }

    public Controller getController() {
        return controller;
    }

    public VirtualView getDispatcher() {
        return virtualView;
    }

    private Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.gameOver = false;
        this.participants = participants;
        this.currentTurnPlayer = 0;
        this.board = Board.generate(this, boardType);
        this.controller = new Controller();
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

    public void setup() {

        // <temp>
        for(int i = 0; i < 21; i ++) {
            try {
                participants.get(0).giveWeapon(board.getWeaponDeck().smartDraw(false).orElse(null));
            } catch (FullHandException ignored) { }
        }

        int[] positions = {7, 7, 6, 9, 3, 4, 0};
        for(int i = 0; i < positions.length; i ++)
            participants.get(i).setPosition(board.getCells().get(positions[i]));
        // </temp>

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
        subject.resetRecentlyDamaged();
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
                                    } catch (DistanceFromNullException e) {
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

    @Override
    public String toString() {
        String s = "Board:\n";
        s += board.toString() + "\n";

        s += "Weapons : " + board.getWeaponDeck().toString() + "\n";
        s += "Power-Ups : " + board.getPowerUpDeck().toString() + "\n";
        s += "Ammo Tiles : " + board.getAmmoTileDeck().toString() + "\n";

        s += "Players:\n";
        s += Table.create(
                participants.stream().map(p -> "#" + p.getID()).collect(Collectors.toList()),
                participants.stream().map(Player::getName).collect(Collectors.toList()),
                participants.stream().map(Player::getScore).collect(Collectors.toList()),
                participants.stream().map(Player::getPosition).map(c -> "@" +  (c == null ? "null" : c.getId())).collect(Collectors.toList()),
                participants.stream().map(p -> "Damage[" +
                        Table.list(p.getDamagersList().stream().map(Player::getID).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "Marks[" +
                        Table.list(p.getMarkersList().stream().map(Player::getID).collect(Collectors.toList()))
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
