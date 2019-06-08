package it.polimi.ingsw.model.exceptions;

/**
 * This exception is thrown upon a failed attempt to handle an unexpected value from a JSON file.
 * It is declared as a runtime exception since its main cause are mistakes in writing the JSON file,
 * and it would make little sense to try to keep running an application in such unstable conditions,
 * and in normal conditions this scenario should never happen anyway.
 */
public class JsonException extends RuntimeException {
    public JsonException(String message) {
        super(message);
    }
}
