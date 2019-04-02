package it.polimi.ingsw;

import java.util.List;

public class Attack {
    private Player target;
    private Player author;
    private List<Effect> effects;

    public Attack(List<Effect> effects) {
        this.target = null; // target is not defined upon deck generation
        this.author = null; // author is not defined upon deck generation
        this.effects = effects;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public Player getTarget() {
        return this.target;
    }

    public Player getAuthor() {
        return this.author;
    }

    public void deal() {
        for(Effect e : effects)
            e.apply(this);
    }
}
