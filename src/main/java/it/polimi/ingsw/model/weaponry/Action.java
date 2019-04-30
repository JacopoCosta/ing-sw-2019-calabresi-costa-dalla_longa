package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.AppendedToAppendableAction;
import it.polimi.ingsw.model.exceptions.AppendedUnappendableActionException;

import java.util.ArrayList;
import java.util.List;

public class Action {
    private String name;
    private String description;
    private AmmoCubes summonCost;
    private int moves;
    private boolean canMoveAfter;
    private List<Attack> attacks;
    private boolean appendable;

    public Action(String name, String description, AmmoCubes summonCost, int moves, boolean canMoveAfter, List<Attack> attacks, boolean appendable) {
        this.name = name;
        this.description = description;
        this.summonCost = summonCost;
        this.moves = moves;
        this.canMoveAfter = canMoveAfter;
        this.attacks = attacks;
        this.appendable = appendable;
    }

    public static Action build(List<String> descriptors) {
        String name = descriptors.remove(0);
        String description = descriptors.remove(0);
        AmmoCubes summonCost = AmmoCubes.build(descriptors.remove(0));
        int moves = Integer.parseInt(descriptors.remove(0));
        boolean canMoveAfter = !descriptors.remove(0).equals("0");
        List<Attack> attacks = new ArrayList<>();
        List<String> attackDescriptors = new ArrayList<>();
        for(String s : descriptors) {
            if(s.equals("K")) {
                attacks.add(Attack.build(attackDescriptors));
                attackDescriptors.clear();
            }
            else
                attackDescriptors.add(s);
        }
        return new Action(name, description, summonCost, moves, canMoveAfter, attacks, false); //TODO set appendable
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public AmmoCubes getSummonCost() {
        return summonCost;
    }

    public int getMoves() {
        return moves;
    }

    public boolean canMoveAfter() {
        return canMoveAfter;
    }

    public List<Attack> getAttacks() {
        return this.attacks;
    }

    public boolean isAppendable() {
        return appendable;
    }

    public void append(Action appendableAction) throws AppendedUnappendableActionException, AppendedToAppendableAction {
        if(!appendableAction.isAppendable())
            throw new AppendedUnappendableActionException("Tried to append an action that is not appendable.");
        if(this.isAppendable())
            throw new AppendedToAppendableAction("Tried to append an action to another appendable action");
        this.attacks.addAll(appendableAction.attacks);
    }
}
