package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An identity constraint requires either that two players actually be the same player, or the opposite,
 * requiring two different identities from the two players.
 */
public class IdentityConstraint extends Constraint {
    /**
     * Whether or not the two players should actually be the same player in order to satisfy the constraint.
     */
    private boolean truth;

    /**
     * This is the only constructor.
     * @param sourceAttackModuleId the id of the attack module containing the source target.
     * @param sourceTargetId the id of the source target.
     * @param drainAttackModuleId the id of the attack module containing the drain target.
     * @param drainTargetId the id of the drain target.
     * @param truth the truth value needed from the identity predicate, in order to satisfy the constraint.
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
     * Tells if the constraint is satisfied by two given players.
     * @param sourcePlayer the source player.
     * @param drainPlayer the drain player.
     * @return true if the cells satisfy the constraint, false if they don't.
     */
    private boolean verify(Player sourcePlayer, Player drainPlayer) {
        return sourcePlayer.equals(drainPlayer) == truth;
    }

    /**
     * Creates a list of all players that satisfy the constraint.
     * @param context the attack pattern in which the constraint is relevant.
     * @return the list of players.
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
     * Since the identity constraint is not defined on cells, this method instantly
     * throws an {@code InvalidFilterInvocationException} at runtime, since calling
     * this method on an instance of this class is symptom of logical flaws in the
     * constraint management workflow. This method was implemented anyway in order
     * to reduce the number of explicit casts and to be able to factorize it in
     * the {@code Constraint} superclass, where it is declared as abstract.
     * @param context the attack pattern in which the constraint is relevant.
     * @return nothing.
     */
    @Override
    public List<Cell> filterCells(AttackPattern context) {
        throw new InvalidFilterInvocationException("A cell can't be a player.");
    }

    /**
     * Since the identity constraint is not defined on rooms, this method instantly
     * throws an {@code InvalidFilterInvocationException} at runtime, since calling
     * this method on an instance of this class is symptom of logical flaws in the
     * constraint management workflow. This method was implemented anyway in order
     * to reduce the number of explicit casts and to be able to factorize it in
     * the {@code Constraint} superclass, where it is declared as abstract.
     * @param context the attack pattern in which the constraint is relevant.
     * @return nothing.
     */
    @Override
    public List<Room> filterRooms(AttackPattern context) {
        throw new InvalidFilterInvocationException("A room can't be a player.");
    }

    @Override
    public String toString() {
        String s = getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += truth ? "is" : "is not";
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
