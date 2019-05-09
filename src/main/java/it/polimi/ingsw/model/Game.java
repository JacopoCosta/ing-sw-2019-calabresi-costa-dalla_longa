package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.*;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private boolean finalFrenzy;
    private int roundsLeft;

    private List<Player> participants;
    private int currentTurnPlayer;

    private Board board;
    private Controller controller;

    public Board getBoard() {
        return this.board;
    }

    public Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.participants = participants;
        this.currentTurnPlayer = 0;

        this.board = Board.generate(this, boardType);
        this.controller = new Controller(this);
    }

    public List<Player> getParticipants() {
        return participants;
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
            int choice = controller.getExecutionIndex(subject, options.size());

            for(Activity activity : options.get(choice).getActivities()) // each execution consists of some activities
                this.controller.activityRoutine(subject, activity);
        }

        participants.stream() // score all dead players
                .filter(Player::isKilled)
                .forEach(Player::scoreUponDeath);

        this.currentTurnPlayer = (this.currentTurnPlayer + 1) % this.participants.size();
    }

    public void play() {
        boolean gameOver = false;

        while(!gameOver) {
            this.playTurn();
        }
    }
}
