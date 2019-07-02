package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.network.server.lobby.Lobby;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.Game;

/**
 * Indicates that an attempt was made to access a {@link Lobby} by a {@link Player} while the corresponding {@link Game}
 * has already been started.
 */
public class GameAlreadyStartedException extends Exception {

    /**
     * Constructs a new {@code GameAlreadyStartedException} with {@code null} as its default error message.
     */
    public GameAlreadyStartedException() {
        super();
    }
}
