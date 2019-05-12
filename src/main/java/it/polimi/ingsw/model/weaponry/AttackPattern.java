package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

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

    public static AttackPattern build(DecoratedJSONObject jPattern) {
        List<Integer> ids = new ArrayList<>();
        List<AttackModule> attackModules = new ArrayList<>();

        for(DecoratedJSONObject jId : jPattern.getArray("first").asList()) {
            ids.add(jId.getInt("id"));
        }
        for(DecoratedJSONObject jAttackModule : jPattern.getArray("content").asList()) {
            attackModules.add(AttackModule.build(jAttackModule));
        }

        AttackPattern attackPattern = new AttackPattern(ids, attackModules);
        for(AttackModule attackModule: attackPattern.content)
            attackModule.setContext(attackPattern);
        return attackPattern;
    }

    public AttackModule getModule(int index) {
        return content.get(index);
    }

    public List<Integer> getFirst() {
        return first;
    }

    public Player getAuthor() {
        return author;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public void resetAllModules() {
        content.stream().forEach(m -> m.setUsed(false));
    }

    @Override
    public String toString() {
        String s = "Attack modules:\n";
            for(AttackModule attackModule : content)
                s += attackModule.toString();
        return s;
    }
}
