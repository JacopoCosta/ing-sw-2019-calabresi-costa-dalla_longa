package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;

import java.util.ArrayList;
import java.util.List;

public class AttackPattern {
    private Player author;
    private List<Integer> first;
    private List<AttackModule> content;

    public AttackPattern(List<Integer> first, List<AttackModule> content) {
        this.first = first;
        this.content = content;
    }

    public static AttackPattern build(DecoratedJsonObject jPattern) {
        List<Integer> ids = new ArrayList<>();
        List<AttackModule> attackModules = new ArrayList<>();

        try {
            for(DecoratedJsonObject jId : jPattern.getArray("first").asList()) {
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
            for(DecoratedJsonObject jAttackModule : jPattern.getArray("content").asList()) {
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

    public AttackModule getModule(int index) {
        return content.get(index);
    }

    public List<Integer> getFirst() {
        return first;
    }

    public void setAuthor(Player author) {
        if(author == null)
            throw new NullPointerException("An attack pattern cannot be authored by null");
        this.author = author;
    }

    public Player getAuthor() {
        return author;
    }

    public void resetAllModules() {
        content.forEach(m -> m.setUsed(false));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Attack modules:\n");
            for(AttackModule attackModule : content)
                s.append(attackModule.toString());
        return s.toString();
    }
}
