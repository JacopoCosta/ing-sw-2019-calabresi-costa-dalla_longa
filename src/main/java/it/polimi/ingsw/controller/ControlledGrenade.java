package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;

public abstract class ControlledGrenade {
    private static final String GRENADE_REQUEST = "Would you like to respond with a grenade?";

    protected static synchronized void routine(Player subject, PowerUp powerUp) {
        boolean tagBack = Dispatcher.requestBoolean(GRENADE_REQUEST);

        if(tagBack) {
            subject.getMostRecentDamager().applyMarking(subject);
            subject.discardPowerUp(powerUp);
        }
    }
}
