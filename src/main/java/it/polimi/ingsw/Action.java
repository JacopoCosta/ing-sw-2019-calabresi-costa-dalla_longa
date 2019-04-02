package it.polimi.ingsw;

import java.util.List;

public class Action {
    private String name;
    private String description;

    private int moves;
    private boolean canMoveBefore;
    private boolean canMoveAfter;

    private AmmoCubes summonCost;

    private List<Attack> attacks;

    public Action(String name, String description, int moves, boolean canMoveBefore, boolean canMoveAfter, AmmoCubes summonCost, List<Attack> attacks) {
        this.name = name;
        this.description = description;
        this.moves = moves;
        this.canMoveBefore = canMoveBefore;
        this.canMoveAfter = canMoveAfter;
        this.summonCost = summonCost;
        this.attacks = attacks;
    }
}
