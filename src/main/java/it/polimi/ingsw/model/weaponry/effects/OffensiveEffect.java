package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import java.util.List;

public abstract class OffensiveEffect extends Effect {
    protected int amount;
    protected List<Constraint> constraints;
}
