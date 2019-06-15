package it.polimi.ingsw.model.player;

/**
 * {@link Activity}s are performed by {@link Player}s during their own turn.
 */
public abstract class Activity {
    /**
     * The type of the {@link Activity}.
     */
    protected ActivityType type;

    /**
     * Returns the type of the {@link Activity}.
     * @return the type of the {@link Activity}.
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
