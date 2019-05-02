package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.AppendedToAppendableActionException;
import it.polimi.ingsw.model.exceptions.AppendedUnappendableActionException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

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

    public static Action build(DecoratedJSONObject jAction) {
        String name = jAction.getString("name");
        String description = jAction.getString("description");
        DecoratedJSONObject jSummon = jAction.getObject("summonCost");
        int red = jSummon.getInt("red");
        int yellow = jSummon.getInt("yellow");
        int blue = jSummon.getInt("blue");
        AmmoCubes summonCost = new AmmoCubes(red, yellow, blue);
        boolean appendable = jAction.getBoolean("appendable");
        int requires = jAction.getInt("requires");
        List<Attack> attacks = new ArrayList<>();

        for(DecoratedJSONObject jAttack : jAction.getArray("attacks").asList()) {
            attacks.add(Attack.build(jAttack));
        }
        return new Action(name, description, summonCost, appendable, requires, attacks);
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
