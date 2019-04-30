package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Move;
import it.polimi.ingsw.model.weaponry.Attack;

public class ForceMove extends Effect {
    private Cell destination;

    public ForceMove(int amount) {
        this.type = EffectType.FORCEMOVE;
        this.amount = amount;
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    @Override
    public void apply(Attack attack) {
        Move fm = new Move(this.amount);
        fm.setDestination(this.destination);
        fm.perform(attack.getTarget());
    }
}
