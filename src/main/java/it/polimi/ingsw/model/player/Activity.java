package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.exceptions.CannotGrabException;
import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.exceptions.WeaponAlreadyLoadedException;

public abstract class Activity {
    protected ActivityType type;

    public ActivityType getType() {
        return type;
    }

    public abstract void perform(Player player) throws WeaponAlreadyLoadedException, ConstraintNotSatisfiedException, CannotGrabException, InvalidMoveException;
}
