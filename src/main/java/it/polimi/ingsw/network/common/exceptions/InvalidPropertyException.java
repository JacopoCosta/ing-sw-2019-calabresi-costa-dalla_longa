package it.polimi.ingsw.network.common.exceptions;

import it.polimi.ingsw.network.common.util.property.GameProperty;
import it.polimi.ingsw.network.common.util.property.GamePropertyLoader;

/**
 * Indicates that an invalid property was found into the configuration file. This exception is related to the {@link GameProperty}
 * and {@link GamePropertyLoader} classes.
 *
 * @see GameProperty
 * @see GamePropertyLoader
 */
public class InvalidPropertyException extends Exception {
    /**
     * Constructs a new {@code InvalidPropertyException} with {@code s} as its default error message.
     *
     * @param s the default error message.
     */
    public InvalidPropertyException(String s) {
        super(s);
    }
}
