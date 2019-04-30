package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Attack;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.effects.Mark;

import java.util.ArrayList;
import java.util.List;

public class Grenade extends PowerUp {
    private static final int GRENADE_TAGBACK_MARKS = 1;

    private Player target;

    public Grenade(AmmoCubes ammoCubes) {
        super(ammoCubes);
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    @Override
    public void use(Player subject) {
        Mark m = new Mark(GRENADE_TAGBACK_MARKS); // create a Mark Effect

        List<Effect> e = new ArrayList<>();
        e.add(m);

        Attack a = new Attack(false, false, e); // make that Effect into an Attack
        a.setAuthor(subject);
        a.setTarget(target);
        a.deal();
    }
}
