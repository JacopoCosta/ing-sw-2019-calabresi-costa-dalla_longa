package it.polimi.ingsw.model.player;

/**
 * Activities are performed by players during their own turn.
 */
public abstract class Activity {
    /**
     * The type of the activity.
     */
    protected ActivityType type;

    /**
     * Returns the type of the activity.
     * @return the type of the activity.
     */
    public ActivityType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type == ActivityType.MOVE ?
                type.toString() + " x" + ((Move) this).getMaxDistance() :
                type.toString();
    }
}
