package it.polimi.ingsw.model.weaponry;

import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Attack {
    private Player target;
    private Player author;
    private boolean optional;
    private boolean cellAttack;
    private boolean roomAttack;
    private List<Effect> effects;
    private List<Constraint> constraints;

    public Attack(boolean optional, boolean cellAttack, boolean roomAttack, List<Effect> effects, List<Constraint> constraints) {
        this.target = null; // target is not defined upon deck generation
        this.author = null; // author is not defined upon deck generation
        this.optional = optional;
        this.cellAttack = cellAttack; // whether or not this Attack is dealt to all Players in the same cell
        this.roomAttack = roomAttack; // whether or not this Attack is dealt to all Players in the same room
        this.effects = effects;
        this.constraints = constraints;
    }

    public static Attack build(DecoratedJSONObject jAttack) {
        boolean optional = jAttack.getBoolean("optional");
        boolean cellAttack = jAttack.getBoolean("cellAttack");
        boolean roomAttack = jAttack.getBoolean("roomAttack");
        List<Effect> effects = new ArrayList<>();
        List<Constraint> constraints = new ArrayList<>();

        for(DecoratedJSONObject jEffect : jAttack.getArray("effects").asList())
            effects.add(Effect.build(jEffect));

        try {
            for (DecoratedJSONObject jConstraint : jAttack.getArray("constrains").asList())
                constraints.add(Constraint.build(jConstraint));
        }
        catch (NullPointerException e) {
            System.out.println(jAttack + " is missing constraints."); //TODO complete json and remove try/catch block (NPE should no longer occur)
        }

        return new Attack(optional, cellAttack, roomAttack, effects, constraints);
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public void setAuthor(Player author) {
        this.author = author;
    }

    public Player getTarget() {
        return this.target;
    }

    public Player getAuthor() {
        return this.author;
    }

    public List<Constraint> getConstraints() {
        return this.constraints;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isCellAttack() {
        return cellAttack;
    }

    public boolean isRoomAttack() {
        return roomAttack;
    }

    public void deal() throws InvalidMoveException {
        for(Effect e : effects)
            e.apply(this);
    }

    public String toString() {
        boolean addComma = false;

        String s = "";
        if(optional)
            s += "(optional) ";
        if(cellAttack)
            s += "(cell) ";
        if(roomAttack)
            s+= "(room) ";
        for(Effect e : effects) {
            if(addComma)
                s += ", ";
            addComma = true;
            s += e.toString();
        }
        return s;
    }
}
