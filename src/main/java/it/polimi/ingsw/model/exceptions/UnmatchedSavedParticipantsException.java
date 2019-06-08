package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.util.json.DecoratedJsonObject;

import java.util.List;

/**
 * This exception is thrown when attempting to load a game but, as far as player names are concerned,
 * the set of online players attempting to join the game and the set of saved players do not match.
 * @see it.polimi.ingsw.model.Game#load(DecoratedJsonObject, List)
 */
public class UnmatchedSavedParticipantsException extends Exception {
    public UnmatchedSavedParticipantsException(String message) {
        super(message);
    }
}
