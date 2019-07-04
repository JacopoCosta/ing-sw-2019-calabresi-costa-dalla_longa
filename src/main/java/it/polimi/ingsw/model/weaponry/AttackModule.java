package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.targets.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code AttackModule} is an atomic event composing the workflow of a {@link Weapon}'s {@link AttackPattern}.
 */
public class AttackModule {

    /**
     * The id of the {@code AttackModule} inside the {@link AttackPattern} it belongs to.
     */
    private int id;

    /**
     * The name of the module.
     */
    private String name;

    /**
     * A string containing useful information on what the module can or cannot do.
     */
    private String description;

    /**
     * The cost of activating the module.
     */
    private AmmoCubes summonCost;

    /**
     * A list of choices the user has to make in order to activate the {@link AttackModule#effects}.
     */
    private List<Target> targets;

    /**
     * The consequences of the module, often influenced by the choices made by the user when acquiring the list of {@link AttackModule#targets}
     */
    private List<Effect> effects;

    /**
     * A list containing all the {@link AttackModule#id}s of other {@code AttackModule}s that can be played immediately after the current one.
     */
    private List<Integer> next;

    /**
     * The {@link AttackPattern} the {@code AttackModule} is played within.
     */
    private AttackPattern context;

    /**
     * Whether or not the {@code AttackModule} has already been used within the same attack.
     */
    private boolean used;

    /**
     * This is the only constructor.
     * @param id the {@code AttackModule}'s {@link AttackModule#id}.
     * @param name its {@link AttackModule#name}.
     * @param description its {@link AttackModule#description}.
     * @param summonCost its {@link AttackModule#summonCost}.
     * @param targets its {@link AttackModule#targets}.
     * @param effects its {@link AttackModule#effects}.
     * @param next its {@link AttackModule#next}.
     */
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

    /**
     * This factory method instantiates and returns an {@code AttackModule}, with the properties found inside the JSON object passed as argument.
     * @param jAttackModule the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     */
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

    /**
     * Gets the {@code AttackModule}'s {@link AttackModule#id}.
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the {@code AttackModule}'s {@link AttackModule#name}.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the {@code AttackModule}'s {@link AttackModule#description}.
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the {@code AttackModule}'s {@link AttackModule#summonCost}.
     * @return the summon cost.
     */
    public AmmoCubes getSummonCost() {
        return summonCost;
    }

    /**
     * Gets the {@code AttackModule}'s {@link AttackModule#targets}.
     * @return the list of targets.
     */
    public List<Target> getTargets() {
        return targets;
    }

    /**
     * Gets the {@code AttackModule}'s {@link AttackModule#effects}.
     * @return the list of effects.
     */
    public List<Effect> getEffects() {
        return effects;
    }

    /**
     * Gets the {@code AttackModule}'s list of {@link AttackModule#next}.
     * @return the list.
     */
    public List<Integer> getNext() {
        return next;
    }

    /**
     * Gets the {@code AttackModule}'s {@link AttackModule#context}.
     * @return the {@link AttackPattern}.
     */
    public AttackPattern getContext() {
        return context;
    }

    /**
     * Tells whether or not the {@code AttackModule} has already been {@link AttackModule#used}.
     * @return {@code true} if it has.
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * Sets the {@code AttackModule}'s {@link AttackModule#used} flag.
     * @param used the new value.
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Sets the {@code AttackModule}'s {@link AttackModule#context}.
     * @param context the {@link AttackPattern} of relevance.
     */
    public void setContext(AttackPattern context) {
        this.context = context;

        for(Target target : targets)
            target.setContext(context);

        for(Effect effect : effects)
            effect.setContext(context);
    }

    /**
     * Creates a string containing a short description of the {@code AttackModule}.
     * @return the string.
     */
    @Override
    public String toString() {
        return name + " [" + summonCost.toString() + "]" + ": " + description;
    }
}
