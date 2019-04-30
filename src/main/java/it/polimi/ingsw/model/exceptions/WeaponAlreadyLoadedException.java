package it.polimi.ingsw.model.exceptions;

public class WeaponAlreadyLoadedException extends Exception {
    public WeaponAlreadyLoadedException(String message) {
        super(message);
    }
}
