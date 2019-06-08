package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to instantiate a new effect whose type is not in the
 * enumeration of possible effect types. It is declared as a runtime exception because its main
 * cause are spelling mistakes in the JSON files, and attempting to run an application in such conditions
 * makes no sense.
 * @see it.polimi.ingsw.model.weaponry.effects.Effect
 * @see it.polimi.ingsw.model.weaponry.effects.EffectType
 */
public class InvalidEffectTypeException extends RuntimeException {
    public InvalidEffectTypeException(String message) {
        super(message);
    }
}
