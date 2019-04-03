package it.polimi.ingsw;

public class Damage extends Effect {
    public Damage(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(Attack attack) {
        for(int i = 0; i < this.amount; i ++)
            attack.getTarget().applyDamage(attack.getAuthor());
    }
}
