package it.polimi.ingsw;

import it.polimi.ingsw.board.Board;
import it.polimi.ingsw.player.Player;

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

    public void playTurn() {
        Player currentPlayer = participants.get(currentTurnPlayer);

    }
}
