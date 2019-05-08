package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Attack;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.effects.ForceMove;

import java.util.ArrayList;
import java.util.List;

public class Newton extends PowerUp{
    private final static int NEWTON_MOVES = 2;

    private Player target;
    private Cell destination;

    public Newton(AmmoCubes ammoCubes) {
        super(ammoCubes);
        this.type = PowerUpType.NEWTON;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    @Override
    public void use(Player subject) throws InvalidMoveException {
        ForceMove fm = new ForceMove(NEWTON_MOVES); // create a ForceMove Effect
        fm.setDestination(this.destination);

        List<Effect> e = new ArrayList<>();
        e.add(fm);

        Attack a = new Attack(false, false, false, e, null); // make that Effect into an Attack
        a.setAuthor(subject);
        a.setTarget(target);
        a.deal();
    }
}
