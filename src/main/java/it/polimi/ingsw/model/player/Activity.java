package it.polimi.ingsw.model.player;

public abstract class Activity {
    protected ActivityType type;

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
