package it.polimi.ingsw.powerups;

import it.polimi.ingsw.ammo.AmmoCubes;
import it.polimi.ingsw.board.cell.Cell;
import it.polimi.ingsw.player.Player;
import it.polimi.ingsw.weaponry.Attack;
import it.polimi.ingsw.weaponry.effects.Effect;
import it.polimi.ingsw.weaponry.effects.ForceMove;

import java.util.ArrayList;
import java.util.List;

public class Newton extends PowerUp{
    private final static int NEWTON_MOVES = 2;

    private Player target;
    private Cell destination;

    public Newton(AmmoCubes ammoCubes, Cell spawnPoint) {
        this.ammoCubes = ammoCubes;
        this.spawnPoint = spawnPoint;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public void setDestination(Cell destination) {
        this.destination = destination;
    }

    @Override
    public void use(Player subject) {
        ForceMove fm = new ForceMove(NEWTON_MOVES); // create a ForceMove Effect
        fm.setDestination(this.destination);

        List<Effect> e = new ArrayList<>();
        e.add(fm);

        Attack a = new Attack(e); // make that Effect into an Attack
        a.setAuthor(subject);
        a.setTarget(target);
        a.deal();
    }
}
