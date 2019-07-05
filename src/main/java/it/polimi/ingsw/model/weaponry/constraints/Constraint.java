package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A constraint is a simple rule that can be verified or falsified. It is expressed in a form
 * similar to a well-formed formula of the monadic first-order logic. Constraints come in different types,
 * each representing a different predicate. Most predicates expressed by constraints have arity 2, i.e.
 * they describe a situation verifiable on 2 entities, they're also known as binary constraints.
 * All constraints are verified inside an {@link AttackPattern} that acts like a context outside of which
 * the predicate is ambiguous and cannot therefore be verified.<br>
 * Binary constraints always include:<br>
 * <ul>
 *     <li><b>{@code Source}</b>: the first actor of the predicate, or the "subject".</li>
 *     <li><b>{@code Drain}</b>: the last actor of the predicate, or the "object".</li>
 * </ul>
 * There are also ternary constraints, that make a statement about three entities, and also include:<br>
 * <ul>
 *     <li><b>{@code Gate}</b>: the middle actor of the predicate, or the "intermediary".</li>
 * </ul>
 * <br>
 * When constraints are evaluated, their subjects, objects and/or intermediaries often need to
 * be referenced in a host of different situations, making integers an easy and versatile solution
 * for creating a path of references that leads to the entity of interest for each role in the constraint.
 * Specifically, each entity is reached by:
 * <ul>
 *     <li>An {@link AttackModule} id: used to identify the {@link AttackModule} in which the entity resides.
 *          This is possible because constraints are limited to one {@link AttackPattern}, making it
 *          unambiguous which {@link AttackModule} the id refers to.</li>
 *     <li>A {@link Target} id: used to identify the {@link Target} inside the {@link AttackModule} that refers to
 *          the entity of interest, whether it be a {@link Cell}, a {@link Room} or a {@link Player}.</li>
 * </ul>
 * This indexed access algorithm makes it impossible to reference {@link Target}s defined in a
 * future moment with respect to the moment in which a constraint attempts to reference
 * an entity as one of its actors, even within the same {@link AttackModule}. In other words,
 * it is impossible to evaluate a constraint depending partially on decisions not yet taken by the player.
 * <br>
 * In order to grant the constraints internal language a fully operational expressive power, at least as
 * far as the game logic is concerned, the following encoding rules for numeric ids were devised:
 * <ul>
 *     <li><b>Any non-negative integer</b>: simply refers to the index of the {@link AttackModule} inside
 *          the context, or to the index of the {@link Target} inside the {@link AttackModule}, as described above.</li>
 *     <li><b>{@code -1}</b>: Refers to the attacker (always known from within the context, as an {@link AttackPattern}
 *          cannot be used until it has been signed by a {@link Player} as its author).</li>
 *     <li><b>{@code -2}</b>: Refers to the attacker, but in the position they had before the beginning of the
 *          current {@link AttackPattern}.</li>
 *     <li><b>{@code -3}</b>: Refers to anyone, much like the "for all" operator in the monadic first order logic.
 *          This value is very important when generating lists of entities that make the constraint evaluate
 *          to {@code true}: each entity is given the role the {@code -3} is found in, and gets replaced by the following entity
 *          for elaborating the truth table. All entities that make the constraint evaluate to {@code true}
 *          are considered eligible for a choice of the {@link Player}, when they'll be presented with a question.</li>
 * </ul>
 */
public abstract class Constraint {

    /**
     * The type of constraint.
     */
    protected ConstraintType type;

    /**
     * The id of the {@link AttackModule} in which the subject is located.
     */
    protected int sourceAttackModuleId;

    /**
     * The id of the {@link Target} representing the subject.
     */
    protected int sourceTargetId;

    /**
     * The id of the {@link AttackModule} in which the object is located.
     */
    protected int drainAttackModuleId;

    /**
     * The id of the {@link Target} representing the object.
     */
    protected int drainTargetId;

    /**
     * The {@link AttackPattern} in which the constraint needs to be evaluated.
     */
    protected AttackPattern context;

    /**
     * This factory method instantiates and returns a constraint, with the properties found inside the JSON object passed as argument.
     *
     * @param jConstraint the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     * @throws InvalidConstraintTypeException when attempting to instantiate a new constraint whose type is not in the
     *                                        enumeration of possible {@link PowerUp} types.
     */
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

        if(type.equals("alignment")) {
            boolean truth;
            try {
                truth = jConstraint.getBoolean("truth");
            } catch (JullPointerException e) {
                throw new JsonException("Distance constraint truth not found.");
            }
            return new AlignmentConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
        }
        if(type.equals("distance")) {
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
        if(type.equals("identity")) {
            boolean truth;
            try {
                truth = jConstraint.getBoolean("truth");
            } catch (JullPointerException e) {
                throw new JsonException("Identity constraint truth not found.");
            }
            return new IdentityConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
        }
        if(type.equals("order")) {
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
        if(type.equals("room")) {
            boolean truth;
            try {
                truth = jConstraint.getBoolean("truth");
            } catch (JullPointerException e) {
                throw new JsonException("Room constraint truth not found.");
            }
            return new RoomConstraint(sourceAttackModuleId, sourceTargetId, drainAttackModuleId, drainTargetId, truth);
        }
        if(type.equals("visibility")) {
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

    /**
     * Given an {@link AttackPattern} to provide a context, it searches for the {@link Target}
     * identified by the {@link AttackModule} id inside the context and the {@link Target} id
     * inside the module.
     *
     * @param context        The {@link AttackPattern} of reference.
     * @param attackModuleId The id of the {@link AttackModule} inside which to look for the {@link Target}.
     * @param targetId       The id of the {@link Target} inside the {@link AttackModule}.
     * @return the {@link Target}.
     */
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

    /**
     * Given an {@link AttackPattern} to provide a context, it searches for all {@link Player}s
     * that meet the requirements expressed by all of the {@code Constraint}s inside a list.
     *
     * @param context     The {@link AttackPattern} of interest.
     * @param constraints The list of {@code Constraint}s.
     * @return all the {@link Player}s that satisfy every {@code Constraint} in the list.
     */
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

    /**
     * Given an {@link AttackPattern} to provide a context, it searches for all {@link Cell}s
     * that meet the requirements expressed by all of the {@code Constraint}s inside a list.
     *
     * @param context     The {@link AttackPattern} of interest.
     * @param constraints The list of {@code Constraint}s.
     * @return all the {@link Cell}s that satisfy every {@code Constraint} in the list.
     */
    public static List<Cell> filterCells(AttackPattern context, List<Constraint> constraints) {
        List<List<Cell>> targetTable = new ArrayList<>();

        for (Constraint constraint : constraints) {
            targetTable.add(constraint.filterCells(context));
        }

        Predicate<Cell> missingFromAtLeastOneList = c -> targetTable.stream()
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

    /**
     * Given an {@link AttackPattern} to provide a context, it searches for all {@link Room}s
     * that meet the requirements expressed by all of the {@code Constraint}s inside a list.
     *
     * @param context     The {@link AttackPattern} of interest.
     * @param constraints The list of {@code Constraint}s.
     * @return all the {@link Room}s that satisfy every {@code Constraint} in the list.
     */
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

    /**
     * Sets the context for the {@code Constraint}.
     *
     * @param context the {@link AttackPattern} inside which the {@code Constraint} should be evaluated.
     */
    public void setContext(AttackPattern context) {
        this.context = context;
    }

    @Override
    public abstract String toString();

    /**
     * Creates a string containing a short description of how the {@code Constraint} will
     * work given an id pair.
     *
     * @param attackModuleId The id of the {@link AttackModule} inside which to look for the {@link Target}.
     * @param targetId       The id of the {@link Target} inside the {@link AttackModule}.
     * @return the string.
     */
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
