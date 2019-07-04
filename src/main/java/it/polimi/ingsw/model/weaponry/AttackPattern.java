package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.ColoredString;
import it.polimi.ingsw.util.json.DecoratedJsonObject;
import it.polimi.ingsw.view.virtual.cli.CliWeapons;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@code AttackPattern} is a directional network of {@link AttackModule}s, fully describing the working
 * process of a {@link Weapon}.
 */
public class AttackPattern {
    /**
     * The {@link Player} launching the attack.
     */
    private Player author;

    /**
     * A list containing the ids of all the {@link AttackModule}s the attack can start with.
     */
    private List<Integer> first;

    /**
     * A list containing all of the {@link AttackModule}s of the {@link Weapon} defined by the {@code AttackPattern}.
     */
    private List<AttackModule> content;

    /**
     * This is the only constructor.
     * @param first the {@link AttackPattern#first} list.
     * @param content the {@link AttackPattern#content} list.
     */
    public AttackPattern(List<Integer> first, List<AttackModule> content) {
        this.first = first;
        this.content = content;
    }

    /**
     * This factory method instantiates and returns an {@code AttackPattern}, with the properties found inside the JSON object passed as argument.
     * @param jPattern the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     */
    public static AttackPattern build(DecoratedJsonObject jPattern) {
        List<Integer> ids = new ArrayList<>();
        List<AttackModule> attackModules = new ArrayList<>();

        try {
            for(DecoratedJsonObject jId : jPattern.getArray("first").toList()) {
                try {
                    ids.add(jId.getInt("id"));
                } catch (JullPointerException e) {
                    throw new JsonException("AttackPattern first id not found.");
                }
            }
        } catch (JullPointerException e) {
            throw new JsonException("AttackPattern first not found.");
        }
        try {
            for(DecoratedJsonObject jAttackModule : jPattern.getArray("content").toList()) {
                attackModules.add(AttackModule.build(jAttackModule));
            }
        } catch (JullPointerException e) {
            throw new JsonException("AttackPattern content not found.");
        }

        AttackPattern attackPattern = new AttackPattern(ids, attackModules);
        for(AttackModule attackModule: attackPattern.content) {
            attackModule.setContext(attackPattern);
        }
        return attackPattern;
    }

    /**
     * Gets the {@link AttackModule} with a specified id.
     * @param index the id.
     * @return the {@link AttackModule}.
     */
    public AttackModule getModule(int index) {
        return content.get(index);
    }

    /**
     * Returns the {@code AttackPattern}'s {@link AttackPattern#first} list.
     * @return the list.
     */
    public List<Integer> getFirst() {
        return first;
    }

    /**
     * Signs all the attacks dealt through this {@link AttackPattern} by a {@link Player}.
     * @param author the attacker.
     */
    public void setAuthor(Player author) {
        if(author == null)
            throw new NullPointerException("An attack pattern cannot be authored by null");
        this.author = author;
    }

    /**
     * Returns the {@code AttackPattern}'s {@link AttackPattern#author}.
     * @return the {@link Player} by whom every attack dealt through this {@code AttackPatten} will be registered.
     */
    public Player getAuthor() {
        return author;
    }

    /**
     * Prepares the {@code AttackPattern} for a new attack by setting all of its {@link AttackModule}s to not used.
     */
    public void resetAllModules() {
        content.forEach(m -> m.setUsed(false));
    }

    /**
     * Generates a string containing a small description of the {@code AttackPattern}.
     * @return the string.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        List<String> attackModuleNames = new ArrayList<>();
        for(AttackModule am : content) {
            if(!attackModuleNames.contains(am.getName())) {
                attackModuleNames.add(am.getName());
                s.append("\n ").append(am.toString());
            }
        }
        return s.toString();
    }

    public List<List<ColoredString>> getHeaders() {
        List<List<ColoredString>> coloredStrings = new ArrayList<>();

        List<AttackModule> showableModules = new ArrayList<>();

        for(AttackModule a : content) {
            if(showableModules.stream().noneMatch(a2 -> a.getName().equals(a2.getName())))
                showableModules.add(a);
        }

        for(AttackModule a : showableModules) {
            List<ColoredString> module = new ArrayList<>();
            module.add(new ColoredString(" >", Color.ANSI_RESET));
            module.addAll(a.getSummonCost().toColoredStrings());
            module.add(new ColoredString(" " + a.getName() + ":", Color.ANSI_RESET));

            coloredStrings.add(module);
        }

        return coloredStrings;
    }

    public List<List<String>> getDescriptions() {
        List<List<String>> descriptions = new ArrayList<>();

        List<AttackModule> showableModules = new ArrayList<>();

        for(AttackModule a : content) {
            if(showableModules.stream().noneMatch(a2 -> a.getName().equals(a2.getName())))
                showableModules.add(a);
        }

        for(AttackModule a : showableModules) {
            String description = a.getDescription();

            List<String> brokenDescription = new ArrayList<>();

            while(description.length() > CliWeapons.width - 4) {
                int caret = CliWeapons.width - 4;
                while (caret >= CliWeapons.width - 12 && description.charAt(caret) != ' ')
                    caret --;
                brokenDescription.add(description.substring(0, caret));
                if(caret + 1 < description.length())
                    description = description.substring(caret + 1);
                else
                    description = "";
            }
            if(description.length() > 0)
                brokenDescription.add(description);
            descriptions.add(brokenDescription);
        }

        return descriptions;
    }
}
