package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.targets.Target;

import java.util.ArrayList;
import java.util.List;

public class AttackModule {
    private int id;
    private String name;
    private String description;
    private AmmoCubes summonCost;

    private List<Target> targets;
    private List<Effect> effects;
    private List<Integer> next;
    private AttackPattern context;

    private boolean used;

    private AttackModule(int id, String name, String description, AmmoCubes summonCost, List<Target> targets, List<Effect> effects, List<Integer> next) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.summonCost = summonCost;
        this.targets = targets;
        this.effects = effects;
        this.next = next;
        this.used = false;
    }

    public static AttackModule build(DecoratedJsonObject jAttackModule) {
        int id;
        try {
            id = jAttackModule.getInt("id");
        } catch (JullPointerException e) {
            throw new JsonException("AttackModule id not found.");
        }
        String name;
        try {
            name = jAttackModule.getString("name");
        } catch (JullPointerException e) {
            throw new JsonException("AttackModule name not found.");
        }
        String description;
        try {
            description = jAttackModule.getString("description");
        } catch (JullPointerException e) {
            throw new JsonException("AttackModule description not found.");
        }
        AmmoCubes summonCost;
        try {
            summonCost = AmmoCubes.build(jAttackModule.getObject("summonCost"));
        } catch (JullPointerException e) {
            throw new JsonException("AttackModule summonCost not found.");
        }
        List<Target> targets = new ArrayList<>();
        List<Effect> effects = new ArrayList<>();
        List<Integer> next = new ArrayList<>();

        try {
            for(DecoratedJsonObject jTarget : jAttackModule.getArray("targets").toList())
                targets.add(Target.build(jTarget));
        } catch (JullPointerException e) {
            throw new JsonException("AttackModule targets not found.");
        }
        try {
            for(DecoratedJsonObject jEffect : jAttackModule.getArray("effects").toList())
                effects.add(Effect.build(jEffect));
        } catch (JullPointerException e) {
            throw new JsonException("AttackModule effects not found.");
        }
        try {
            for(DecoratedJsonObject jNext : jAttackModule.getArray("next").toList())
                try {
                    next.add(jNext.getInt("id"));
                } catch (JullPointerException e) {
                    throw new JsonException("AttackModule next id not found.");
                }
        } catch (JullPointerException e) {
            throw new JsonException("AttackModule next not found.");
        }

        return new AttackModule(id, name, description, summonCost, targets, effects, next);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public AmmoCubes getSummonCost() {
        return summonCost;
    }

    public List<Target> getTargets() {
        return targets;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public List<Integer> getNext() {
        return next;
    }

    public AttackPattern getContext() {
        return context;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setContext(AttackPattern context) {
        this.context = context;

        for(Target target : targets)
            target.setContext(context);

        for(Effect effect : effects)
            effect.setContext(context);
    }

    @Override
    public String toString() {
        return name + " [" + summonCost.toString() + "]" + ": " + description;
    }
}
