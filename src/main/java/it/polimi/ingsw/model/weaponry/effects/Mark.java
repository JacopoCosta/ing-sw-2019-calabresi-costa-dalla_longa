package it.polimi.ingsw.model.weaponry.effects;

import it.polimi.ingsw.model.weaponry.Attack;

public class Mark extends Effect{
    public Mark(int amount) {
        this.type = EffectType.MARK;
        this.amount = amount;
    }

    @Override
    public void apply(Attack attack) {
        for(int i = 0; i < this.amount; i ++)
            attack.getTarget().applyMarking(attack.getAuthor());
    }
}
