package it.polimi.ingsw.weaponry.effects;

import it.polimi.ingsw.weaponry.Attack;

public class Damage extends Effect {
    public Damage(int amount) {
        this.type = EffectType.DAMAGE;
        this.amount = amount;
    }

    @Override
    public void apply(Attack attack) {
        this.amount += attack.getAuthor().exhaustScopes(); // the increment in damage is equal to the number of Scopes used
        for(int i = 0; i < this.amount; i ++)
            attack.getTarget().applyDamage(attack.getAuthor());
    }
}
