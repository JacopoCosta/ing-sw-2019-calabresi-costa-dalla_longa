package it.polimi.ingsw.weaponry;

import it.polimi.ingsw.ammo.AmmoCubes;

import java.util.List;

public class Action {
    private String name;
    private String description;

    private int moves;
    private boolean canMoveAfter;

    private AmmoCubes summonCost;

    private List<Attack> attacks;

    public Action(String name, String description, int moves, boolean canMoveAfter, AmmoCubes summonCost, List<Attack> attacks) {
        this.name = name;
        this.description = description;
        this.moves = moves;
        this.canMoveAfter = canMoveAfter;
        this.summonCost = summonCost;
        this.attacks = attacks;
    }
}
