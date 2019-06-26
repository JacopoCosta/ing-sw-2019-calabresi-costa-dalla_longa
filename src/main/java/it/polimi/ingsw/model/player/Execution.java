package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.effects.Damage;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@code Execution} is an ordered list of {@link Activity}s the {@link Player} can perform during their turn.
 * On each turn, a {@link Player} can normally perform two {@code Execution}s, except when they use frenetic actions.
 */
public class Execution {
    /**
     * The maximum number of {@link Move}s in standard conditions.
     */
    private static final int MAX_MOVES = 3; // max number of moves in standard conditions

    /**
     * The maximum number of {@link Move}s before grabbing, in standard conditions.
     */
    private static final int MAX_MOVES_ON_GRAB = 1; // max number of moves before grabbing in standard conditions

    /**
     * The maximum number of {@link Move}s before grabbing in enhanced conditions,
     * i.e. after taking a certain amount of {@link Damage}.
     * @see Execution#GRAB_ENHANCE_THRESHOLD
     */
    private static final int MAX_MOVES_ON_GRAB_ENHANCED = 2; // max number of moves before grabbing in enhanced conditions (enhanced: after taking a set amount of {@link Damage})

    /**
     * The maximum number of {@link Move}s before {@link Shoot}ing in enhanced conditions,
     * i.e. after taking a certain amount of {@link Damage}.
     * @see Execution#SHOOT_ENHANCE_THRESHOLD
     */
    private static final int MAX_MOVES_ON_SHOOT_ENHANCED = 1; // max number of moves before shooting in enhanced conditions

    /**
     * The maximum number of {@link Move}s in standard conditions, when final frenzy is activated,
     * on a turn preceding the starting {@link Player}.
     */
    private static final int MAX_MOVES_FRENETIC = 4; // max number of moves in standard conditions on final frenzy on a turn before the starting player

    /**
     * The maximum number of {@link Move}s before grabbing, when final frenzy is activated,
     * on a turn preceding the starting {@link Player}.
     */
    private static final int MAX_MOVES_FRENETIC_ON_GRAB_BEFORE = 2; // max number of moves before grabbing on final frenzy on a turn before the starting player

    /**
     * The maximum number of {@link Move}s before grabbing, when final frenzy is activated,
     * on a turn equal to or following the starting {@link Player}'s turn.
     */
    private static final int MAX_MOVES_FRENETIC_ON_GRAB_AFTER = 3; // max number of moves before grabbing on final frenzy on a turn equal to or after the starting player

    /**
     * The maximum number of {@link Move}s before {@link Reload}ing and {@link Shoot}ing,
     * when final frenzy is activated, on a turn preceding the starting {@link Player}.
     */
    private static final int MAX_MOVES_FRENETIC_ON_SHOOT_BEFORE = 1; // max number of moves before reloading and shooting on final frenzy on a turn before the starting player

    /**
     * The maximum number of {@link Move}s before {@link Reload}ing and {@link Shoot}ing,
     * when final frenzy is activated, on a turn equal to or following the starting {@link Player}'s turn.
     */
    private static final int MAX_MOVES_FRENETIC_ON_SHOOT_AFTER = 2; // max number of moves before reloading and shooting on final frenzy on a turn equal to or after the starting player

    /**
     * The amount of {@link Damage} a {@link Player} must take before being able to use the enhanced version of the {@link Grab}.
     * @see Execution#MAX_MOVES_ON_GRAB_ENHANCED
     */
    private static final int GRAB_ENHANCE_THRESHOLD = 3; // the amount of damage a player must take before being able to use the enhanced version of grab

    /**
     * The amount of {@link Damage} a {@link Player} must take before being able to {@link Move} before using the {@link Shoot}.
     * @see Execution#MAX_MOVES_ON_SHOOT_ENHANCED
     */
    private static final int SHOOT_ENHANCE_THRESHOLD = 6; // the amount of damage a player must take before being able to use the enhanced version of shoot

    /**
     * The list of {@link Activity}s the {@code Execution} consists of.
     */
    private List<Activity> activities;

    /**
     * This is the only constructor.
     * @param activities the list of {@link Activity}s the {@code Execution} will be made out of.
     */
    private Execution(List<Activity> activities) {
        this.activities = activities;
    }

    /**
     * Returns the list of {@link Activity}s composing the {@code Execution}.
     * @return the list.
     */
    public List<Activity> getActivities() {
        return this.activities;
    }

    /**
     * Generates an {@code Execution} using the {@code Move} {@link Activity},
     * based on the condition of the {@link Player} passed as argument.
     * @param subject The {@link Player} for whom this method needs to generate the {@code Execution}.
     * @return The {@code Execution} deemed correct for the {@link Player}, given their status.
     * @see Move
     */
    private static Execution generateMove(Player subject) { // moves only
        List<Activity> a = new ArrayList<>();
        a.add(new Move(
                subject.isOnFrenzy() ? MAX_MOVES_FRENETIC : MAX_MOVES
        ));
        concatenateReload(a, subject);
        return new Execution(a);
    }

    /**
     * Generates an {@code Execution} using the {@code Move} and {@code Grab} {@link Activity}s,
     * based on the condition of the {@link Player} passed as argument.
     * @param subject The {@link Player} for whom this method needs to generate the {@code Execution}.
     * @return The {@code Execution} deemed correct for the {@link Player}, given their status.
     * @see Move
     * @see Grab
     */
    private static Execution generateGrab(Player subject) { // moves, then grabs
        List<Activity> a = new ArrayList<>();
        a.add(new Move(
                subject.isOnFrenzy() ?
                        (subject.isOnFrenzyBeforeStartingPlayer() ? MAX_MOVES_FRENETIC_ON_GRAB_BEFORE : MAX_MOVES_FRENETIC_ON_GRAB_AFTER) :
                        (subject.getDamage() >= GRAB_ENHANCE_THRESHOLD ? MAX_MOVES_ON_GRAB_ENHANCED : MAX_MOVES_ON_GRAB)
        ));
        a.add(new Grab());
        concatenateReload(a, subject);
        return new Execution(a);
    }

    /**
     * Generates an {@code Execution} using the {@code Shoot} {@link Activity},
     * occasionally coupled with the {@code Move} and/or {@code Reload} {@link Activity}s,
     * based on the condition of the {@link Player} passed as argument.
     * @param subject The {@link Player} for whom this methods needs to generate the {@code Execution}.
     * @return The {@code Execution} deemed correct for the {@link Player}, given their status.
     * @see Move
     * @see Shoot
     * @see Reload
     */
    private static Execution generateShoot(Player subject) { // moves (unless not enhanced and not on frenzy), then shoots -- reloads before shooting if on frenzy
        List<Activity> a = new ArrayList<>();
        if(subject.isOnFrenzy() || subject.getDamage() >= SHOOT_ENHANCE_THRESHOLD) { // the moves will not be included unless the player is either well damaged or on frenzy
            a.add(new Move(
                    subject.isOnFrenzy() ?
                            (subject.isOnFrenzyBeforeStartingPlayer() ? MAX_MOVES_FRENETIC_ON_SHOOT_BEFORE : MAX_MOVES_FRENETIC_ON_SHOOT_AFTER) :
                            MAX_MOVES_ON_SHOOT_ENHANCED
            ));
        }
        if(subject.isOnFrenzy())
            a.add(new Reload());
        a.add(new Shoot());
        concatenateReload(a, subject);
        return new Execution(a);
    }

    /**
     * This method takes in a list of {@link Activity} (the soon-to-be {@code Execution}) and a {@link Player}.
     * If the {@link Player} is not on final frenzy and they are about to perform the last {@code Execution} of their turn, the option
     * to {@link Reload} a {@link Weapon} at the end is added.
     * This is achieved by concatenating a {@code Reload} {@link Activity} at the end
     * of the list of {@link Activity}s passed in as argument.
     * Should the {@link Player} not meet the requirements to be able to {@link Reload}
     * at the end of the {@code Execution}, this method will have no effect on the {@link Activity} list.
     * @param activityList The list of {@link Activity}s that will shortly be turned into an {@code Execution}.
     * @param subject The {@link Player} for whom to evaluate whether or not to allow a {@link Reload} at the end of their turn.
     */
    private static void concatenateReload(List<Activity> activityList, Player subject) { // each last execution of a non-frenzy turn also offers a reload at the end
        if(!subject.isOnFrenzy() && subject.getRemainingExecutions() == 1) // this is the last execution of a non-frenzy turn
            activityList.add(new Reload());
    }

    /**
     * Generates a list of {@code Execution}s based on a {@link Player}'s current condition.
     * @param subject The {@link Player} for whom to generate a list of options.
     * @return A list of {@code Execution}s from which the {@link Player} will pick one to perform.
     */
    public static List<Execution> getOptionsForPlayer(Player subject) {
        List<Execution> e = new ArrayList<>();
        if(!subject.isOnFrenzy() || subject.isOnFrenzyBeforeStartingPlayer()) // the "move only" execution is not available outside of these conditions
            e.add(generateMove(subject));
        e.add(generateGrab(subject));
        if(subject.getWeapons().stream().anyMatch(Weapon::isLoaded))
            e.add(generateShoot(subject));
        return e;
    }

    /**
     * Generates a string containing a short description of the execution.
     * @return the string.
     */
    @Override
    public String toString() {
        boolean addPlus = false;

        StringBuilder s = new StringBuilder();
        for(Activity a : activities) {
            if(addPlus) {
                s.append(" + ");
            }
            s.append(a);
            addPlus = true;
        }
        return s.toString();
    }
}
