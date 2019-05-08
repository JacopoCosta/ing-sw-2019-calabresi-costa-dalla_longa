package it.polimi.ingsw.model;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.CannotGrabException;
import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.exceptions.WeaponAlreadyLoadedException;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.weaponry.Action;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private boolean finalFrenzy;
    private int roundsLeft;

    private List<Player> participants;
    private int currentTurnPlayer;

    private Board board;

    public Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.participants = participants;
        this.currentTurnPlayer = 0;

        this.board = Board.generate(boardType);
    }

    public List<Player> getPlayersByCell(Cell cell) {
        List<Player> result = new ArrayList<>();
        for(Player p : this.participants) {
            if(p.getPosition() == cell)
                result.add(p);
        }
        return result;
    }

    public List<Player> getPlayersByRoom(Room room) {
        List<Player> result = new ArrayList<>();
        for(Player p : this.participants) {
            if(p.getPosition().getRoom() == room)
                result.add(p);
        }
        return result;
    }

    public void playTurn() {
        Player subject = participants.get(currentTurnPlayer);
        subject.beginTurn();
        while(subject.getRemainingExecutions() > 0) { // a turn is made by several executions
            List<Execution> options = Execution.getOptionsForPlayer(subject);
            int choice = 0; //TODO get this value legitimately

            for(Activity activity : options.get(choice).getActivities()) { // each execution consists of some activities
                if(activity.getType() == ActivityType.MOVE) {
                    Move move = (Move) activity;
                    Cell destination = null; //TODO get this value legitimately
                    move.setDestination(destination);
                }
                else if(activity.getType() == ActivityType.GRAB) {
                    Grab grab = (Grab) activity;
                }
                else if(activity.getType() == ActivityType.SHOOT) {
                    Shoot shoot = (Shoot) activity;
                    Action action = null; //TODO get this value legitimately
                    ((Shoot)activity).setAction(action);
                }
                else if(activity.getType() == ActivityType.RELOAD) {
                    Reload reload = (Reload) activity;
                    Weapon weapon = null; //TODO get this value legitimately
                    ((Reload)activity).setWeapon(weapon);
                }
            }
        }

        participants.stream() // score all dead players
                .filter(Player::isKilled)
                .forEach(Player::scoreUponDeath);

        this.currentTurnPlayer = (this.currentTurnPlayer + 1) % this.participants.size();
    }
}
