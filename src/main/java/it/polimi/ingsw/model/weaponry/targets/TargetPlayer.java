package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.List;

public class TargetPlayer extends Target {
    /**
     * The chosen {@link Player}.
     */
    private Player player;

    /**
     * This is the only constructor.
     * @param message The message to be presented to the user when they need to choose their target.
     * @param constraints The rules that a target needs to satisfy in order to be eligible.
     */
    public TargetPlayer(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.PLAYER;
    }

    /**
     * Sets the {@link #player} attribute.
     * @param player the {@link Player} to set as the new value.
     */
    public void setPlayer(Player player) {
        if(player == null)
            throw new NullPointerException("Attempted to set target player to null.");
        this.player = player;
    }

    /**
     * Returns the chosen {@link TargetPlayer#player}.
     *
     * @return the {@link Player}.
     */
    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the {@link Cell} containing the chosen {@link TargetPlayer#player}.
     *
     * @return the {@link Cell}.
     */
    @Override
    public Cell getCell() {
        return player.getPosition();
    }

    /**
     * Returns the {@link Room} containing the {@link Cell} containing the chosen {@link TargetPlayer#player}.
     *
     * @return the {@link Room}.
     */
    @Override
    public Room getRoom() {
        if(player.getPosition() == null)
            return null;
        return player.getPosition().getRoom();
    }

    /**
     * Creates a list containing all the {@link Player}s available to choose as {@code TargetPlayer}s
     * that respect all of the {@link Target#constraints}.
     *
     * @return the list.
     */
    public List<Player> filter() {
        return Constraint.filterPlayers(context, constraints);
    }
}
