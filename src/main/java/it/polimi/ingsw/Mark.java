package it.polimi.ingsw;

public class Mark extends Effect{
    public Mark(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(Attack attack) {
        for(int i = 0; i < this.amount; i ++)
            attack.getTarget().applyMarking(attack.getAuthor());
    }
}
