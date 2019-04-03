package it.polimi.ingsw;

public class ForceMove extends Effect {
    private Cell destination;

    public ForceMove(int amount) {
        this.amount = amount;
    }

    public void setDestination(Cell destination) { // check that destination is within range
        this.destination = destination;
    }

    @Override
    public void apply(Attack attack) {
        // make attack.getTarget() perform a "move" Activity
    }
}
