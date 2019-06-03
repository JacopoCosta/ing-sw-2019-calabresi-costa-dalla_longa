package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.List;

public class TargetPlayer extends Target {
    private Player player;

    public TargetPlayer(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.PLAYER;
    }

    public void setPlayer(Player player) {
        if(player == null)
            throw new NullPointerException("Attempted to set target player to null.");
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Cell getCell() {
        return player.getPosition();
    }

    @Override
    public Room getRoom() {
        if(player.getPosition() == null)
            return null;
        return player.getPosition().getRoom();
    }

    public List<Player> filter() {
        return Constraint.filterPlayers(context, constraints);
    }
}
