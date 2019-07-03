package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.json.DecoratedJsonObject;

import java.util.List;

/**
 * This exception is thrown when attempting to load a {@link Game} but, as far as {@link Player} names are concerned,
 * the set of online {@link Player}s attempting to join the {@link Game} and the set of saved {@link Player} do not match.
 * @see it.polimi.ingsw.model.Game#load(DecoratedJsonObject, List)
 */
public class UnmatchedSavedParticipantsException extends Exception {
    public UnmatchedSavedParticipantsException(String message) {
        super(message);
    }
}
