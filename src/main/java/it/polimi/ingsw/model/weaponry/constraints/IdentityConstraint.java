package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.InvalidFilterInvocationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;

import java.util.List;
import java.util.stream.Collectors;

public class IdentityConstraint extends Constraint {
    private boolean truth;

    public IdentityConstraint(int sourceAttackModuleId, int sourceTargetId, int drainAttackModuleId, int drainTargetId, boolean truth) {
        this.sourceAttackModuleId = sourceAttackModuleId;
        this.sourceTargetId = sourceTargetId;
        this.drainAttackModuleId = drainAttackModuleId;
        this.drainTargetId = drainTargetId;
        this.truth = truth;
        this.type = ConstraintType.IDENTITY;
    }

    private boolean verify(Player sourcePlayer, Player drainPlayer) {
        return sourcePlayer.equals(drainPlayer) == truth;
    }

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

    @Override
    public List<Cell> filterCells(AttackPattern context) {
        throw new InvalidFilterInvocationException("A cell can't be a player.");
    }

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
