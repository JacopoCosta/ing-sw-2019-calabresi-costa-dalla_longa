package it.polimi.ingsw.model.exceptions;

public class WeaponAlreadyUnloadedException extends RuntimeException {
    public WeaponAlreadyUnloadedException(String message) {
        super(message);
    }
}
