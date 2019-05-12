package it.polimi.ingsw.model.weaponry.constraints;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;
import it.polimi.ingsw.model.weaponry.targets.TargetType;

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
    public boolean verify() {
        Player sourcePlayer = Constraint.getTarget(context, sourceAttackModuleId, sourceTargetId).getPlayer();
        Player drainPlayer = Constraint.getTarget(context, drainAttackModuleId, drainTargetId).getPlayer();

        return verify(sourcePlayer, drainPlayer);
    }

    @Override
    public List<Player> filter(AttackPattern context) {
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
        throw new IllegalStateException("This instance of constraint can't use a filter.");
    }

    @Override
    public String toString() {
        String s = getHumanReadableName(sourceAttackModuleId, sourceTargetId) + " ";
        s += truth ? "is" : "is not";
        s += " " + getHumanReadableName(drainAttackModuleId, drainTargetId);
        return s;
    }
}
