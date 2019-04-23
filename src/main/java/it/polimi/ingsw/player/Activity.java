package it.polimi.ingsw.player;

public abstract class Activity {
    protected ActivityType type;

    public ActivityType getType() {
        return type;
    }

    public abstract void perform(Player player);
}
