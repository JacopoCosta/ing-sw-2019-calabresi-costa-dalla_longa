package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.constraints.DistanceConstraint;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;

import java.util.ArrayList;
import java.util.List;

public class Move extends Activity {
    private int maxDistance;

    public Move(int maxDistance) {
        this.type = ActivityType.MOVE;
    }

    @Override
    public void perform(Player player) {
        List<Integer> first = new ArrayList<>();
        first.add(0);

        List<Target> targets = new ArrayList<>();
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(
                new DistanceConstraint(-1, -1, 0, 0, 0, maxDistance)
        );
        targets.add(
                new TargetCell("", constraints)
        );

        List<Effect> effects = new ArrayList<>();
        effects.add(
            new it.polimi.ingsw.model.weaponry.effects.Move(-1, -1, 0, 0)
        );

        List<Integer> next = new ArrayList<>();
        next.add(-1);

        List<AttackModule> content = new ArrayList<>();
        content.add(
           new AttackModule(0, null, null, null, targets, effects, next)
        );
    }
}
