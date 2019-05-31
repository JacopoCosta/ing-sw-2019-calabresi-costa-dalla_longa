package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;
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

    public AttackModule(int id, String name, String description, AmmoCubes summonCost, List<Target> targets, List<Effect> effects, List<Integer> next) {
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
            throw new JsonException("Can't find attack module id");
        }
        String name = jAttackModule.getString("name");
        String description = jAttackModule.getString("description");
        AmmoCubes summonCost = AmmoCubes.build(jAttackModule.getObject("summonCost"));
        List<Target> targets = new ArrayList<>();
        List<Effect> effects = new ArrayList<>();
        List<Integer> next = new ArrayList<>();

        for(DecoratedJsonObject jTarget : jAttackModule.getArray("targets").asList())
            targets.add(Target.build(jTarget));
        for(DecoratedJsonObject jEffect : jAttackModule.getArray("effects").asList())
            effects.add(Effect.build(jEffect));
        for(DecoratedJsonObject jNext : jAttackModule.getArray("next").asList())
            try {
                next.add(jNext.getInt("id"));
            } catch (JullPointerException e) {
                throw new JsonException("Can't find next");
            }

        return new AttackModule(id, name, description, summonCost, targets, effects, next);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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
        StringBuilder s = new StringBuilder("\t" + id + ". " + name);
        if(context.getFirst().contains(id))
            s.append(" (first)");
        s.append("\n\t").append(summonCost.toString()).append(" to summon");

        s.append("\n\tTargets:");
        int targetId = 0;
        for(Target target : targets) {
            s.append("\n\t\t").append(targetId).append(". ").append(target.toString());
            targetId ++;
        }
        s.append("\n\tEffects:");
        for(Effect effect : effects)
            s.append("\n\t\t").append(effect.toString());

        s.append("\n\tNext modules:");
        for(int n : next)
            s.append(" ").append(n);

        return s + "\n";
    }
}
