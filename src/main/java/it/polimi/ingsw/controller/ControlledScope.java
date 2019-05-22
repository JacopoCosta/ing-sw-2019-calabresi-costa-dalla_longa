package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.view.remote.Dispatcher;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledScope {
    private static final String TARGET_REQUEST = "Who would you like to give additional damage to?";

    protected static synchronized void routine(Player subject, PowerUp powerUp) {
        List<Player> availableTargets = subject.getRecentlyDamaged()
                .stream()
                .distinct()
                .collect(Collectors.toList());
        int targetIndex = Dispatcher.requestIndex(TARGET_REQUEST, availableTargets);

        availableTargets.get(targetIndex).applyDamage(subject);
        subject.discardPowerUp(powerUp);
    }
}
