package it.polimi.ingsw.model.exceptions;

public class WeaponAlreadyLoadedException extends RuntimeException {
    public WeaponAlreadyLoadedException(String message) {
        super(message);
    }
}
