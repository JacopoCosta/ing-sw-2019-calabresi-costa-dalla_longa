package it.polimi.ingsw.weaponry.effects;

import it.polimi.ingsw.board.cell.Cell;
import it.polimi.ingsw.player.Move;
import it.polimi.ingsw.weaponry.Attack;

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
