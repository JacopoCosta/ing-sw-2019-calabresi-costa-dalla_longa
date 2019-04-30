package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Attack {
    private Player target;
    private Player author;
    private boolean cellAttack;
    private boolean roomAttack;
    private List<Effect> effects;

    public Attack(boolean cellAttack, boolean roomAttack, List<Effect> effects) {
        this.target = null; // target is not defined upon deck generation
        this.author = null; // author is not defined upon deck generation
        this.cellAttack = cellAttack; // whether or not this Attack is dealt to all Players in the same cell
        this.roomAttack = roomAttack; // whether or not this Attack is dealt to all Players in the same room
        this.effects = effects;
    }

    public static Attack build(List<String> descriptors) {
        boolean cellAttack = !descriptors.remove(0).equals("0");
        boolean roomAttack = !descriptors.remove(0).equals("0");
        List<Effect> effects = new ArrayList<>();
        List<String> effectDescriptors = new ArrayList<>();

        for(String s : descriptors) {
            if(s.equals("E")) {
                effects.add(Effect.build(effectDescriptors));
                effectDescriptors.clear();
            }
            else
                effectDescriptors.add(s);
        }

        return new Attack(cellAttack, roomAttack, effects);
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
