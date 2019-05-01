package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.AppendedToAppendableActionException;
import it.polimi.ingsw.model.exceptions.AppendedUnappendableActionException;

import java.util.ArrayList;
import java.util.List;

public class Action {
    private String name;
    private String description;
    private AmmoCubes summonCost;
    private boolean appendable;
    private int requiredActions;
    private List<Attack> attacks;

    public Action(String name, String description, AmmoCubes summonCost, boolean appendable, int requiredActions, List<Attack> attacks) {
        this.name = name;
        this.description = description;
        this.summonCost = summonCost;
        this.appendable = appendable;
        this.requiredActions = requiredActions;
        this.attacks = attacks;
    }

    public static Action build(List<String> descriptors) {
        String name = descriptors.remove(0);
        String description = descriptors.remove(0);
        AmmoCubes summonCost = AmmoCubes.build(descriptors.remove(0));
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
        return new Action(name, description, summonCost, false, 0, attacks); //TODO set appendable
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

    public List<Attack> getAttacks() {
        return this.attacks;
    }

    public boolean isAppendable() {
        return appendable;
    }

    public void append(Action appendableAction) throws AppendedUnappendableActionException, AppendedToAppendableActionException {
        if(!appendableAction.isAppendable())
            throw new AppendedUnappendableActionException("Tried to append an action that is not appendable.");
        if(this.isAppendable())
            throw new AppendedToAppendableActionException("Tried to append an action to another appendable action");
        this.attacks.addAll(appendableAction.attacks);
    }
}
