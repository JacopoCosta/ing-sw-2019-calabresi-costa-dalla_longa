package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;

public abstract class ControlledTeleport {
    private static final String TELEPORT_REQUEST = "Where would you like to teleport?";

    protected static synchronized void routine(Player subject, PowerUp powerUp) {
        Board board = subject.getGame().getBoard();
        Cell destination = board.getCells().get(
                Dispatcher.requestInteger(TELEPORT_REQUEST, 0, board.getCells().size())
        );

        subject.setPosition(destination);
        subject.discardPowerUp(powerUp);
    }
}
