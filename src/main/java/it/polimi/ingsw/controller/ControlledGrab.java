package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.CannotGrabException;
import it.polimi.ingsw.model.player.Grab;
import it.polimi.ingsw.model.player.Player;

public abstract class ControlledGrab {
    protected static synchronized void routine(Player subject, Grab grab) {
        try {
            grab.perform(subject);
        } catch (CannotGrabException e) {
            System.out.println(e.getMessage());
        }
    }
}
