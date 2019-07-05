package it.polimi.ingsw.model.exceptions;

import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.effects.EffectType;

/**
 * This exception is thrown when attempting to instantiate a new {@link Effect} whose type is not in the
 * enumeration of possible {@link EffectType}s. It is declared as a runtime exception because its main
 * cause are spelling mistakes in the JSON files, and attempting to run an application in such conditions
 * makes no sense.
 *
 * @see Effect
 * @see EffectType
 */
public class InvalidEffectTypeException extends RuntimeException {
    public InvalidEffectTypeException(String message) {
        super(message);
    }
}
