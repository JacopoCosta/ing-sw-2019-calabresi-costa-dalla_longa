package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.constraints.DistanceConstraint;
import it.polimi.ingsw.model.weaponry.constraints.VisibilityConstraint;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.effects.Move;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class ControlledNewton {
    private static final int NEWTON_MOVES = 2;
    private static final String PLAYER_CHOOSE = "Who would you like to move?";
    private static final String CELL_CHOOSE = "Where would you like to move them?";


    protected static synchronized void routine(Player subject, PowerUp powerUp) {

        List<Constraint> playerConstraints = new ArrayList<>();
        TargetPlayer targetPlayer = new TargetPlayer(
                PLAYER_CHOOSE,
                playerConstraints
        );

        List<Constraint> cellConstraints = new ArrayList<>();
        cellConstraints.add(
                new VisibilityConstraint(-1, -1, 0, 1, true)
        );
        cellConstraints.add(
                new DistanceConstraint(0, 0, 0, 1, 0, NEWTON_MOVES)
        );
        TargetCell targetCell = new TargetCell(
                CELL_CHOOSE,
                cellConstraints
        );

        List<Target> targets = new ArrayList<>();
        targets.add(targetPlayer);
        targets.add(targetCell);

        List<Effect> effects = new ArrayList<>();
        effects.add(
                new Move(0, 0, 0, 1)
        );

        List<Integer> next = new ArrayList<>();
        next.add(-1);

        AttackModule attackModule = new AttackModule(0, null, null, null ,targets, effects, next);

        List<Integer> first = new ArrayList<>();
        first.add(0);

        List<AttackModule> content = new ArrayList<>();
        content.add(attackModule);

        AttackPattern attackPattern = new AttackPattern(first, content);

    //    ControlledShoot.applyPattern(attackPattern, subject);
        subject.discardPowerUp(powerUp);
    }
}
