package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidConstraintTypeException;
import it.polimi.ingsw.model.exceptions.InvalidEffectTypeException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Constraint {

    protected ConstraintType type;
    protected int sourceAttackModuleId;
    protected int sourceTargetId;
    protected int drainAttackModuleId;
    protected int drainTargetId;
    protected AttackPattern context;

    public static Constraint build(DecoratedJSONObject jConstraint) throws InvalidConstraintTypeException {
        int sourceAttackModuleId = jConstraint.getInt("sourceAttackModuleId");
        int sourceTargetId = jConstraint.getInt("sourceTargetId");
        int drainAttackModuleId = jConstraint.getInt("drainAttackModuleId");
        int drainTargetId = jConstraint.getInt("drainTargetId");
        String type = jConstraint.getString("type");

        if(type.equals("alignment")) {
            boolean truth = jConstraint.getBoolean("truth");
            return new AlignmentConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
        }
        if(type.equals("distance")) {
            int lowerBound = jConstraint.getInt("lowerBound");
            int upperBound = jConstraint.getInt("upperBound");
            return new DistanceConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, lowerBound, upperBound);
        }
        if(type.equals("identity")) {
            boolean truth = jConstraint.getBoolean("truth");
            return new IdentityConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
        }
        if(type.equals("order")) {
            int gateAttackModuleId = jConstraint.getInt("gateAttackModuleId");
            int gateTargetId = jConstraint.getInt("gateTargetId");
            return new OrderConstraint(sourceAttackModuleId, sourceTargetId, gateAttackModuleId, gateTargetId, drainAttackModuleId, drainTargetId);
        }
        if(type.equals("room")) {
            boolean truth = jConstraint.getBoolean("truth");
            return new RoomConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
        }
        if(type.equals("visibility")) {
            boolean truth = jConstraint.getBoolean("truth");
            return new VisibilityConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
        }
        throw new InvalidEffectTypeException(type + " is not a valid name for a Constraint type. Use \"alignment\", \"distance\", \"identity\", \"order\", \"room\", or \"visibility\"");
    }

    public static Target getTarget(AttackPattern context, int attackModuleId, int targetId) {
        if(attackModuleId == -2 && targetId == -2) {
            TargetCell target = new TargetCell(null, null);
            target.setCell(context.getAuthor().getSavedPosition());
            return target;
        }
        if(attackModuleId == -1 && targetId == -1) {
            TargetPlayer target = new TargetPlayer(null, null);
            target.setPlayer(context.getAuthor());
            return target;
        }
        if(attackModuleId >= 0 && targetId >= 0) {
            return context.getModule(attackModuleId).getTargets().get(targetId);
        }
        throw new IllegalArgumentException("Generic targets are not gettable.");
    }

    public abstract List<Player> filterPlayers(AttackPattern context);

    public abstract List<Cell> filterCells(AttackPattern context);

    public abstract List<Room> filterRooms(AttackPattern context);

    public static List<Player> filterPlayers(AttackPattern context, List<Constraint> constraints) {
        List<List<Player>> targetTable = new ArrayList<>();

        for(Constraint constraint : constraints)
            targetTable.add(constraint.filterPlayers(context));

        Predicate<Player> inEveryList = p -> targetTable.stream()
                .map(list -> list.contains(p))
                .reduce(true, (a, b) -> a && b);

        return targetTable.stream()
                .map(Collection::stream)
                .flatMap(Function.identity())
                .sorted(Comparator.comparingInt(Player::getID))
                .distinct()
                .filter(inEveryList)
                .collect(Collectors.toList());
    }

    public static List<Cell> filterCells(AttackPattern context, List<Constraint> constraints) {
        List<List<Cell>> targetTable = new ArrayList<>();

        for(Constraint constraint : constraints)
            targetTable.add(constraint.filterCells(context));

        Predicate<Cell> inEveryList = c -> targetTable.stream()
                .map(list -> list.contains(c))
                .reduce(true, (a, b) -> a && b);

        return targetTable.stream()
                .map(Collection::stream)
                .flatMap(Function.identity())
                .sorted(Comparator.comparingInt(Cell::getId))
                .distinct()
                .filter(inEveryList)
                .collect(Collectors.toList());
    }

    public static List<Room> filterRooms(AttackPattern context, List<Constraint> constraints) {
        List<List<Room>> targetTable = new ArrayList<>();

        for(Constraint constraint : constraints)
            targetTable.add(constraint.filterRooms(context));

        Predicate<Room> inEveryList = r -> targetTable.stream()
                .map(list -> list.contains(r))
                .reduce(true, (a, b) -> a && b);

        return targetTable.stream()
                .map(Collection::stream)
                .flatMap(Function.identity())
                .sorted()
                .distinct()
                .filter(inEveryList)
                .collect(Collectors.toList());
    }

    public void setContext(AttackPattern context) {
        this.context = context;
    }

    @Override
    public abstract String toString();

    public static String getHumanReadableName(int attackModuleId, int targetId) {
        if(attackModuleId == -3 && targetId == -3)
            return "player";
        if(attackModuleId == -2 && targetId == -2)
            return "the attacker's original position";
        if(attackModuleId == -1 && targetId == -1)
            return "the attacker";
        return attackModuleId + " " + targetId;
    }
}
