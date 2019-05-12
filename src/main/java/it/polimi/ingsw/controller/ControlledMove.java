package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.player.Move;
import it.polimi.ingsw.model.player.Player;

import java.util.List;

public abstract class ControlledMove {

    private static final String MOVE_REQUEST = "Where would you like to move?";
    private static final String MOVE_ERROR = "You can't move to the selected cell.";

    protected static synchronized void routine(Player subject, Move move) {

    }
}
