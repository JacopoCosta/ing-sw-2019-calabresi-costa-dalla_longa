package it.polimi.ingsw.model.weaponry.targets;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.TargetInheritanceException;
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

public class TargetRoom extends Target {
    private Room room;

    public TargetRoom(String message, List<Constraint> constraints) {
        this.message = message;
        this.constraints = constraints;
        this.type = TargetType.ROOM;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    @Override
    public Player getPlayer() {
        throw new TargetInheritanceException("Constraints on players cannot be applied to rooms.");
    }

    @Override
    public Cell getCell() {
        throw new TargetInheritanceException("Constraints on cells cannot be applied to rooms.");
    }

    @Override
    public Room getRoom() {
        return room;
    }

    public List<Room> filter() {
        List<List<Room>> targetTable = new ArrayList<>();

        for(Constraint constraint : constraints)
            targetTable.add(constraint.filterRooms(context));

        Predicate<Room> inEveryList = r -> targetTable.stream()
                .map(list -> list.contains(r))
                .reduce(true, (a, b) -> a && b);

        return targetTable.stream()
                .map(Collection::stream)
                .flatMap(Function.identity())
                .sorted()
                .distinct()
                .filter(inEveryList)
                .collect(Collectors.toList());
    }
}
