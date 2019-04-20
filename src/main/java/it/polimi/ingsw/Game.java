package it.polimi.ingsw;

import it.polimi.ingsw.board.Board;
import it.polimi.ingsw.board.Room;
import it.polimi.ingsw.board.cell.Cell;
import it.polimi.ingsw.player.Activity;
import it.polimi.ingsw.player.Execution;
import it.polimi.ingsw.player.Player;

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
                // activity may be either Move, Grab, Shoot or Reload

            }
        }
        currentTurnPlayer = (currentTurnPlayer + 1) % this.participants.size();
    }
}
