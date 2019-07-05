package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown when attempting to access a value in a JSON file by using a key not present
 * in the current scope of the file. As the name suggests, it is a mask for the famous {@code NullPointerException}
 * but with two key differences:<br>
 * * Its causes are somewhat always known, since it can only generate while reading from a JSON file using a non-existent key.<br>
 * * It is a checked exception, allowing the use of a {@code try}/{@code catch} block without falling in the
 * bad practice of catching runtime exceptions. <br>
 * In case of failed management of this exception, or unexpected missing value, a new runtime exception, {@code JsonException}, is thrown.
 *
 * @see JsonException
 */
public class JullPointerException extends Exception {
    public JullPointerException(String message) {
        super(message);
    }
}
