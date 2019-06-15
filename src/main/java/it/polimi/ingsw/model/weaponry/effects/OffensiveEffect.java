package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import java.util.List;

/**
 * Offensive effects are irreversible {@link Effect}s on players: Damage and Mark.
 */
public abstract class OffensiveEffect extends Effect {
    /**
     * Indicates how much of that {@link Effect} to apply.
     */
    protected int amount;

    /**
     * The list of all requirements a player must meet in order to be affercted.
     */
    protected List<Constraint> constraints;
}
