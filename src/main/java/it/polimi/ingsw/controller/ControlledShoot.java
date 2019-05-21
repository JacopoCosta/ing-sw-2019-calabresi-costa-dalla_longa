package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.WeaponAlreadyUnloadedException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.constraints.Constraint;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.effects.EffectType;
import it.polimi.ingsw.model.weaponry.effects.Mark;
import it.polimi.ingsw.model.weaponry.effects.OffensiveEffect;
import it.polimi.ingsw.model.weaponry.targets.*;
import it.polimi.ingsw.view.Dispatcher;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledShoot {
    private static final String WEAPON_CHOOSE = "Which weapon would you like to shoot with?";
    private static final String MODULE_CHOOSE = "Choose how to attack:";

    protected static synchronized void routine(Player subject) {
        List<Weapon> availableWeapons = subject.getWeapons()
                .stream()
                .filter(Weapon::isLoaded)
                .collect(Collectors.toList()); // gather all of the player's loaded weapons
        // at the time this method is called and entered, it is assumed that the player is actually able to shoot with at least one weapon

        Weapon weapon = availableWeapons.get(
            Dispatcher.requestIndex(WEAPON_CHOOSE, availableWeapons)
        ); // choose a weapon

        AttackPattern pattern = weapon.getPattern();
        applyPattern(pattern, subject);
        weapon.unload();
    }

    protected static synchronized void applyPattern(AttackPattern pattern, Player subject) {
        pattern.setAuthor(subject);
        pattern.resetAllModules();

        List<Integer> first = pattern.getFirst();

        int nextIndex = Dispatcher.requestIndex(MODULE_CHOOSE,
                first.stream()
                .map(i -> pattern.getModule(i).getName())
                .collect(Collectors.toList())
        );

        int nextId = pattern.getFirst().get(nextIndex);

        while(nextId != -1) {
            AttackModule attackModule = pattern.getModule(nextId);
            List<Target> targets = attackModule.getTargets();

            Dispatcher.sendMessage("For " + attackModule.getName() + ", I need to acquire: " + Table.list(targets.stream()
                    .map(Target::getType)
                    .collect(Collectors.toList())
            ) + ".\n");

            for(Target target : targets) {
                if(target.getType() == TargetType.PLAYER) {
                    List<Player> players = ((TargetPlayer) target).filter();
                    if(players.size() == 0) {
                        Dispatcher.sendMessage("Looks like there aren't valid players to select.\n");
                        return;
                    }

                    System.out.print("This is a temporary message about players.");
                    int playerId = Dispatcher.requestIndex(target.getMessage(),
                            players.stream()
                            .map(Player::getName)
                            .collect(Collectors.toList())
                    );

                    Player acquiredPlayer = players.get(playerId);
                    Dispatcher.sendMessage("Selected " + acquiredPlayer.getName() + ".\n");
                    ((TargetPlayer) target).setPlayer(acquiredPlayer);
                }
                else if(target.getType() == TargetType.CELL) {
                    List<Cell> cells = ((TargetCell) target).filter();

                    if(cells.size() == 0) {
                        Dispatcher.sendMessage("Looks like there aren't valid cells to select.\n");
                        return;
                    }

                    System.out.print("This is a temporary message about cells.");
                    int cellId = Dispatcher.requestNumberedOption(target.getMessage(),
                            cells.stream()
                            .map(Cell::getId)
                            .collect(Collectors.toList()),

                            cells.stream()
                            .map(Cell::getId)
                            .collect(Collectors.toList())
                    );

                    Cell acquiredCell = cells.get(cellId);
                    Dispatcher.sendMessage("Selected " + acquiredCell.getId() + ".\n");
                    ((TargetCell) target).setCell(acquiredCell);
                }
                else if(target.getType() == TargetType.ROOM) {
                    List<Room> rooms = ((TargetRoom) target).filter();

                    if(rooms.size() == 0) {
                        Dispatcher.sendMessage("Looks like there aren't valid rooms to select.\n");
                        return;
                    }

                    System.out.print("This is a temporary message about rooms.");
                    int roomId = Dispatcher.requestIndex(target.getMessage(),
                            rooms.stream()
                            .map(Room::toString)
                            .collect(Collectors.toList())
                    );

                    Room acquiredRoom = rooms.get(roomId);
                    Dispatcher.sendMessage("Selected " + acquiredRoom.toString() + ".\n");
                    ((TargetRoom) target).setRoom(acquiredRoom);
                }

            }

            attackModule.getEffects().forEach(e -> {
                if (e.getType() == EffectType.MOVE)
                    e.apply();
                else {
                    OffensiveEffect oe = (OffensiveEffect) e;
                    oe.setAuthor(subject);
                    oe.apply();
                }
            });

            attackModule.setUsed(true);

            List<Integer> next = attackModule.getNext()
                    .stream()
                    .filter(i -> {
                        try {
                            return !pattern.getModule(i).isUsed();
                        } catch (IndexOutOfBoundsException e) { // -1 needs to pass
                            return true;
                        }
                    })
                    .collect(Collectors.toList());

            nextIndex = Dispatcher.requestIndex(MODULE_CHOOSE,
                    next.stream()
                    .map(i -> {
                        try {
                            return pattern.getModule(i).getName() + ": " + pattern.getModule(i).getDescription();
                        } catch (Exception e) { // -1 ends action
                            return "End action.";
                        }
                    })
                    .collect(Collectors.toList())
            );
            nextId = next.get(nextIndex);
        }
    }
}
