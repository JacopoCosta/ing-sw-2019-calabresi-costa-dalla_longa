package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.player.Move;
import it.polimi.ingsw.model.weaponry.Attack;

public class SelfMove extends Effect {
    private Cell destination;

    protected SelfMove(int amount) {
        this.type = EffectType.SELFMOVE;
        this.amount = amount;
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    @Override
    public void apply(Attack attack) throws InvalidMoveException {
        Move sm = new Move(this.amount);
        sm.setDestination(this.destination);
        sm.perform(attack.getAuthor());
    }
}
