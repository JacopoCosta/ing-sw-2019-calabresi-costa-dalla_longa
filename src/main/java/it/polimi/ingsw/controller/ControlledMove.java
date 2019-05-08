package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.player.Move;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public abstract class ControlledMove {

    private static final String MOVE_REQUEST = "Where would you like to move?";
    private static final String MOVE_ERROR = "You can't move to the selected cell.";

    protected static synchronized void routine(Player subject, Move move) {
        boolean done = false;

        List<Cell> cells = subject.getPosition().getBoard().getCells();

        while(!done) {
            try {
                move.setDestination(
                        cells.get(
                                Dispatcher.requestInteger(MOVE_REQUEST, 0, cells.size())
                        )
                );
                move.perform(subject);
                done = true;
            } catch (InvalidMoveException e) {
                System.out.println(MOVE_ERROR);
            }
        }
    }
}
