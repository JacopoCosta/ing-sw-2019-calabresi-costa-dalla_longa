package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An {@link IdentityConstraint} requires either that two {@link Player}s actually be the same {@link Player}, or the opposite,
 * requiring two different identities from the two {@link Player}s.
 */
public class IdentityConstraint extends Constraint {
    /**
     * Whether or not the two {@link Player}s should actually be the same {@link Player} in order to satisfy the {@link Constraint}.
     */
    private boolean truth;

    /**
     * This is the only constructor.
     *
     * @param sourceAttackModuleId the id of the {@link AttackModule} containing the source {@link Target}.
     * @param sourceTargetId       the id of the source {@link Target}.
     * @param drainAttackModuleId  the id of the {@link AttackModule} containing the drain {@link Target}.
     * @param drainTargetId        the id of the drain {@link Target}.
     * @param truth                the truth value needed from the identity predicate, in order to satisfy the {@link Constraint}.
     */
    IdentityConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, boolean truth) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.truth = truth;
        this.type = ConstraintType.IDENTITY;
    }

    /**
     * Tells if the {@link Constraint} is satisfied by two given {@link Player}s.
     *
     * @param sourcePlayer the source {@link Player}.
     * @param drainPlayer  the drain {@link Player}.
     * @return true if the cells satisfy the {@link Constraint}, false if they don't.
     */
    private boolean verify(Player sourcePlayer, Player drainPlayer) {
        return sourcePlayer.equals(drainPlayer) == truth;
    }

    /**
     * Creates a list of all {@link Player}s that satisfy the {@link Constraint}.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
     * @return the list of {@link Player}s.
     */
    @Override
    public List<Player> filterPlayers(AttackPattern context) {
        if(sourceAttackModuleId == -3 && sourceTargetId == -3) {
            Player drainPlayer = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getPlayer();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(p, drainPlayer))
                    .distinct()
                    .collect(Collectors.toList());
        }
        if(drainAttackModuleId == -3 && drainTargetId == -3) {
            Player sourcePlayer = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getPlayer();

            return context.getAuthor()
                    .getGame()
                    .getParticipants()
                    .stream()
                    .filter(p -> !p.equals(context.getAuthor()))
                    .filter(p -> this.verify(sourcePlayer, p))
                    .distinct()
                    .collect(Collectors.toList());
        }
        throw new InvalidFilterInvocationException("This instance of constraint can't use a filter.");
    }

    /**
     * Since the {@link IdentityConstraint} is not defined on cells, this method instantly
     * throws an {@code InvalidFilterInvocationException} at runtime, since calling
     * this method on an instance of this class is symptom of logical flaws in the
     * {@link Constraint} management workflow. This method was implemented anyway in order
     * to reduce the number of explicit casts and to be able to factorize it in
     * the {@code Constraint} superclass, where it is declared as abstract.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
     * @return nothing.
     */
    @Override
    public List<Cell> filterCells(AttackPattern context) {
        throw new InvalidFilterInvocationException("A cell can't be a player.");
    }

    /**
     * Since the {@link IdentityConstraint} is not defined on {@link Room}s, this method instantly
     * throws an {@code InvalidFilterInvocationException} at runtime, since calling
     * this method on an instance of this class is symptom of logical flaws in the
     * {@link Constraint} management workflow. This method was implemented anyway in order
     * to reduce the number of explicit casts and to be able to factorize it in
     * the {@code Constraint} superclass, where it is declared as abstract.
     *
     * @param context the {@link AttackPattern} in which the {@link Constraint} is relevant.
     * @return nothing.
     */
    @Override
    public List<Room> filterRooms(AttackPattern context) {
        throw new InvalidFilterInvocationException("A room can't be a player.");
    }

    /**
     * Generates a short human-readable string summarizing what the {@link Constraint} requires.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        String s = getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += truth ? "is" : "is not";
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
