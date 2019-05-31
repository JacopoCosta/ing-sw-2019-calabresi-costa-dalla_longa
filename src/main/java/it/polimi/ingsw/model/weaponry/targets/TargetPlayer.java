package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TargetPlayer extends Target {
    private Player player;

    public TargetPlayer(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.PLAYER;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public Cell getCell() {
        System.out.println("debug$1$" + player);
        return player.getPosition(); //TODO this line threw npe several times
    }

    @Override
    public Room getRoom() {
        return player.getPosition().getRoom(); //TODO npe
    }

    public List<Player> filter() {
        return Constraint.filterPlayers(context, constraints);
    }
}
