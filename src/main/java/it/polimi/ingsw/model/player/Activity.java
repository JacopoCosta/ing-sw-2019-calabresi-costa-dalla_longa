package it.polimi.ingsw.model.player;

/**
 * {@code Activity}s are performed by {@link Player}s during their own turn.
 */
public abstract class Activity {
    /**
     * The type of the {@code Activity}.
     */
    protected ActivityType type;

    /**
     * Returns the type of the {@code Activity}.
     *
     * @return the type of the {@code Activity}.
     */
    public ActivityType getType() {
        return type;
    }

    /**
     * Creates a string containing a brief description of the activity.
     *
     * @return the string.
     */
    @Override
    public String toString() {
        return type == ActivityType.MOVE ?
                type.toString() + " x" + ((Move) this).getMaxDistance() :
                type.toString();
    }
}
