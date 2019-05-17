package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class ControlledNewton {
    private static final int NEWTON_MOVES = 2;

    protected static synchronized void routine(Player subject, PowerUp powerUp) {

        List<Constraint> cellConstraints = new ArrayList<>();

        List<Constraint> playerConstraints = new ArrayList<>();

        TargetPlayer targetPlayer = new TargetPlayer(
                "Who would you like to move?",
                playerConstraints
        );
    }
}
