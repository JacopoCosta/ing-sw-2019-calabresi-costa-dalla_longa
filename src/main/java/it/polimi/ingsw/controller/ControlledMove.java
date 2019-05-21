package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.DistanceFromNullException;
import it.polimi.ingsw.model.player.Move;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.view.Dispatcher;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledMove {
    private static final String MOVE_REQUEST = "Where would you like to move?";

    protected static synchronized void routine(Player subject, Move move) {

        Board board = subject.getGame().getBoard();
        Cell destination;
        List<Cell> validDestinations = board.getCells()
                .stream()
                .filter(c -> {
                            try {
                                return c.distance(subject.getPosition()) <= move.getMaxDistance();
                            } catch (DistanceFromNullException e) {
                                return false;
                            }
                        }
                )
                .collect(Collectors.toList());
        destination = validDestinations.get(
                Dispatcher.requestNumberedOption(MOVE_REQUEST, validDestinations, validDestinations.stream().map(Cell::getId).collect(Collectors.toList()))
        );

        subject.setPosition(destination);
    }
}
