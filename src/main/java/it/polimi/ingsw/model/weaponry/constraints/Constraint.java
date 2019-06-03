package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Constraint {

    protected ConstraintType type;
    protected int sourceAttackModuleId;
    protected int sourceTargetId;
    protected int drainAttackModuleId;
    protected int drainTargetId;
    protected AttackPattern context;

    public static Constraint build(DecoratedJsonObject jConstraint) throws InvalidConstraintTypeException {
        int sourceAttackModuleId;
        try {
            sourceAttackModuleId = jConstraint.getInt("sourceAttackModuleId");
        } catch (JullPointerException e) {
            throw new JsonException("Constraint sourceAttackModuleId not found.");
        }
        int sourceTargetId;
        try {
            sourceTargetId = jConstraint.getInt("sourceTargetId");
        } catch (JullPointerException e) {
            throw new JsonException("Constraint sourceAttackModuleId not found.");
        }
        int drainAttackModuleId;
        try {
            drainAttackModuleId = jConstraint.getInt("drainAttackModuleId");
        } catch (JullPointerException e) {
            throw new JsonException("Constraint sourceAttackModuleId not found.");
        }
        int drainTargetId;
        try {
            drainTargetId = jConstraint.getInt("drainTargetId");
        } catch (JullPointerException e) {
            throw new JsonException("Constraint sourceAttackModuleId not found.");
        }
        String type;
        try {
            type = jConstraint.getString("type");
        } catch (JullPointerException e) {
            throw new JsonException("Constraint type not found.");
        }

        if (type.equals("alignment")) {
                boolean truth;
                try {
                    truth = jConstraint.getBoolean("truth");
                } catch (JullPointerException e) {
                    throw new JsonException("Distance constraint truth not found.");
                }
                return new AlignmentConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
            }
            if (type.equals("distance")) {
                int lowerBound;
                try {
                    lowerBound = jConstraint.getInt("lowerBound");
                } catch (JullPointerException e) {
                    throw new JsonException("Distance constraint lowerBound is null.");
                }
                int upperBound;
                try {
                    upperBound = jConstraint.getInt("upperBound");
                } catch (JullPointerException e) {
                    throw new JsonException("Distance constraint upperBound is null.");
                }
                return new DistanceConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, lowerBound, upperBound);
            }
            if (type.equals("identity")) {
                boolean truth;
                try {
                    truth = jConstraint.getBoolean("truth");
                } catch (JullPointerException e) {
                    throw new JsonException("Identity constraint truth not found.");
                }
                return new IdentityConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
            }
            if (type.equals("order")) {
                int gateAttackModuleId;
                try {
                    gateAttackModuleId = jConstraint.getInt("gateAttackModuleId");
                } catch (JullPointerException e) {
                    throw new JsonException("Order constraint gateAttackModuleId not found.");
                }
                int gateTargetId;
                try {
                    gateTargetId = jConstraint.getInt("gateTargetId");
                } catch (JullPointerException e) {
                    throw new JsonException("Order constraint gateTargetId not found.");
                }
                return new OrderConstraint(sourceAttackModuleId, sourceTargetId, gateAttackModuleId, gateTargetId, drainAttackModuleId, drainTargetId);
            }
            if (type.equals("room")) {
                boolean truth;
                try {
                    truth = jConstraint.getBoolean("truth");
                } catch (JullPointerException e) {
                    throw new JsonException("Room constraint truth not found.");
                }
                return new RoomConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
            }
            if (type.equals("visibility")) {
                boolean truth;
                try {
                    truth = jConstraint.getBoolean("truth");
                } catch (JullPointerException e) {
                    throw new JsonException("Visibility constraint truth not found");
                }
                return new VisibilityConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
            }
            throw new InvalidEffectTypeException(type + " is not a valid name for a Constraint type. Use \"alignment\", \"distance\", \"identity\", \"order\", \"room\", or \"visibility\"");
    }

    public static Target getTarget(AttackPattern context, int attackModuleId, int targetId) {
        if(context.getAuthor() == null)
            throw new UnauthoredAttackPatternException("Cannot evaluate target for an attack pattern without a valid author.");

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

        for(Constraint constraint : constraints) {
            targetTable.add(constraint.filterPlayers(context));
        }

        Predicate<Player> missingFromAtLeastOneList = p -> targetTable.stream()
                .map(list -> !list.contains(p))
                .reduce(false, (a, b) -> a || b);

        return context.getAuthor()
                .getGame()
                .getParticipants()
                .stream()
                .sorted(Comparator.comparingInt(Player::getId))
                .distinct()
                .filter(p -> !missingFromAtLeastOneList.test(p))
                .filter(p -> !p.equals(context.getAuthor()))
                .filter(p -> p.getPosition() != null)
                .collect(Collectors.toList());
    }

    public static List<Cell> filterCells(AttackPattern context, List<Constraint> constraints) {
        List<List<Cell>> targetTable = new ArrayList<>();

        for(Constraint constraint : constraints) {
            targetTable.add(constraint.filterCells(context));
        }

        Predicate<Cell> missingFromAtLeastOneList = c -> targetTable.stream()  // TODO does it work when targetTable contains zero lists?
                .map(list -> !list.contains(c))
                .reduce(false, (a, b) -> a || b);

        return context.getAuthor()
                .getGame()
                .getBoard()
                .getCells()
                .stream()
                .sorted(Comparator.comparingInt(Cell::getId))
                .distinct()
                .filter(p -> !missingFromAtLeastOneList.test(p))
                .collect(Collectors.toList());
    }

    public static List<Room> filterRooms(AttackPattern context, List<Constraint> constraints) {
        List<List<Room>> targetTable = new ArrayList<>();

        for(Constraint constraint : constraints) {
            targetTable.add(constraint.filterRooms(context));
        }

        Predicate<Room> missingFromAtLeastOneList = r -> targetTable.stream()
                .map(list -> !list.contains(r))
                .reduce(false, (a, b) -> a || b);

        return context.getAuthor()
                .getGame()
                .getBoard()
                .getCells()
                .stream()
                .map(Cell::getRoom)
                .sorted(Comparator.comparing(Room::toString))
                .distinct()
                .filter(p -> !missingFromAtLeastOneList.test(p))
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
