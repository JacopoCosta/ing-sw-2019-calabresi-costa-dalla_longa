package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Move;
import it.polimi.ingsw.model.player.Player;

public abstract class ControlledMove {
    private static final String MOVE_REQUEST = "Where would you like to move?";

    protected static synchronized void routine(Player subject, Move move) {

        Board board = subject.getGame().getBoard();
        Cell destination;
        do {
            destination = board.getCells().get(
                    Dispatcher.requestInteger(MOVE_REQUEST, 0, board.getCells().size())
            );
        } while(destination.distance(subject.getPosition()) > move.getMaxDistance());

        subject.setPosition(destination);
    }
}
