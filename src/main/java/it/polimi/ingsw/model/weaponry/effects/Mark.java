package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.List;

public class Mark extends OffensiveEffect{

    public Mark(int amount, List<Constraint> constraints) {
        this.amount = amount;
        this.constraints = constraints;
        this.type = EffectType.MARK;
    }

    @Override
    public void apply() {
        for(int i = 0; i < amount; i ++)
            getTargetStream()
                    .forEach(p -> p.applyDamage(author));
    }

    @Override
    public String toString() {
        String s = amount + " mark(s) to player such that";
        for(Constraint constraint : constraints)
            s += " (" + constraint.toString() + ")";
        return s;
    }
}
