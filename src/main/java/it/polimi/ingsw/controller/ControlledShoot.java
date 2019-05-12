package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Shoot;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.targets.*;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledShoot {
    private static final String WEAPON_CHOOSE = "Which weapon would you like to shoot with?";
    private static final String MODULE_CHOOSE = "Choose how to attack:";

    protected static synchronized void routine(Player subject, Shoot shoot) {
        List<Weapon> availableWeapons = subject.getWeapons()
                .stream()
                .filter(Weapon::isLoaded)
                .collect(Collectors.toList());

        Weapon weapon = availableWeapons.get(
            Dispatcher.requestInteger(WEAPON_CHOOSE, 0, availableWeapons.size())
        );

        AttackPattern pattern = weapon.getPattern();
        pattern.setAuthor(subject);
        pattern.resetAllModules();

        List<AttackModule> first = pattern.getFirst()
                .stream()
                .map(pattern::getModule)
                .collect(Collectors.toList());

        int nextId = first.size() > 1 ? Dispatcher.requestInteger(MODULE_CHOOSE, 0, first.size()) : 0;

        while(nextId != -1) {
            AttackModule attackModule = first.get(nextId);
            List<Target> targets = attackModule.getTargets();
            for(Target target : targets) {
                do {
                    if(target.getType() == TargetType.PLAYER) {
                        List<Player> players = subject.getGame().getParticipants();

                        ((TargetPlayer) target).setPlayer(
                            players.get(
                                    Dispatcher.requestInteger(target.getMessage(), 0, players.size())
                            )
                        );
                    }
                    else if(target.getType() == TargetType.CELL) {
                        List<Cell> cells = subject.getPosition().getBoard().getCells();

                        ((TargetCell) target).setCell(
                                cells.get(
                                        Dispatcher.requestInteger(target.getMessage(), 0, cells.size())
                                )
                        );
                    }
                    else if(target.getType() == TargetType.ROOM) {
                        List<Room> rooms = subject.getPosition()
                                .getBoard()
                                .getCells()
                                .stream()
                                .map(Cell::getRoom)
                                .distinct()
                                .collect(Collectors.toList());

                        ((TargetRoom) target).setRoom(
                                rooms.get(
                                        Dispatcher.requestInteger(target.getMessage(), 0, rooms.size())
                                )
                        );
                    }
                } while (
                        target.getConstraints()
                        .stream()
                        .map(Constraint::verify)
                        .reduce(true, (c1, c2) -> c1 && c2)
                );
            }

            attackModule.getEffects()
                    .stream()
                    .forEach(Effect::apply);

            attackModule.setUsed(true);

            List<Integer> next = attackModule.getNext();
            nextId = next.size() > 1 ? Dispatcher.requestInteger(MODULE_CHOOSE, 0, next.size()) : 0;
        }
    }
}
