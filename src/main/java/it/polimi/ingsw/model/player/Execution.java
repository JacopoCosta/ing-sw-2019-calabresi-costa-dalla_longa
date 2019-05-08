package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.exceptions.InvalidMoveException;

import java.util.ArrayList;
import java.util.List;

public class Execution {
    private static final int MAX_MOVES = 3; // max number of moves in standard conditions
    private static final int MAX_MOVES_ON_GRAB = 1; // max number of moves before grabbing in standard conditions
    private static final int MAX_MOVES_ON_GRAB_ENHANCED = 2; // max number of moves before grabbing in enhanced conditions (enhanced: after taking a set amount of damage)
    private static final int MAX_MOVES_ON_SHOOT_ENHANCED = 1; // max number of moves before shooting in enhanced conditions

    private static final int MAX_MOVES_FRENETIC = 4; // max number of moves in standard conditions on final frenzy on a turn before the starting player
    private static final int MAX_MOVES_FRENETIC_ON_GRAB_BEFORE = 2; // max number of moves before grabbing on final frenzy on a turn before the starting player
    private static final int MAX_MOVES_FRENETIC_ON_GRAB_AFTER = 3; // max number of moves before grabbing on final frenzy on a turn equal to or after the starting player
    private static final int MAX_MOVES_FRENETIC_ON_SHOOT_BEFORE = 1; // max number of moves before reloading and shooting on final frenzy on a turn before the starting player
    private static final int MAX_MOVES_FRENETIC_ON_SHOOT_AFTER = 2; // max number of moves before reloading and shooting on final frenzy on a turn equal to or after the starting player

    private static final int GRAB_ENHANCE_THRESHOLD = 3; // the amount of damage a player must take before being able to use the enhanced version of grab
    private static final int SHOOT_ENHANCE_THRESHOLD = 6; // the amount of damage a player must take before being able to use the enhanced version of shoot

    private List<Activity> activities;

    private Execution(List<Activity> activities) {
        this.activities = activities;
    }

    public List<Activity> getActivities() {
        return this.activities;
    }

    private static void concatenateReload(List<Activity> a, Player subject) { // each last execution of a non-frenzy turn also offers a reload at the end
        if(!subject.isOnFrenzy() && subject.getRemainingExecutions() == 1) // this is the last execution of a non-frenzy turn
            a.add(new Reload());
    }

    public static Execution generateMove(Player subject) throws InvalidMoveException { // moves only
        List<Activity> a = new ArrayList<>();
        a.add(new Move(
                subject.isOnFrenzy() ? MAX_MOVES_FRENETIC : MAX_MOVES
        ));
        concatenateReload(a, subject);
        return new Execution(a);
    }

    public static Execution generateGrab(Player subject) throws InvalidMoveException { // moves, then grabs
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

    public static Execution generateShoot(Player subject) throws InvalidMoveException { // moves (unless not enhanced and not on frenzy), then shoots -- reloads before shooting if on frenzy
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

    public static List<Execution> getOptionsForPlayer(Player subject) throws InvalidMoveException {
        List<Execution> e = new ArrayList<>();
        if(!subject.isOnFrenzy() || subject.isOnFrenzyBeforeStartingPlayer()) // the "move only" execution is not available outside of these conditions
            e.add(generateMove(subject));
        e.add(generateGrab(subject));
        e.add(generateShoot(subject));
        return e;
    }
}
