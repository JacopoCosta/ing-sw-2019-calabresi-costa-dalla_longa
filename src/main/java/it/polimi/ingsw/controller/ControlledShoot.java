package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;
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
    }

    protected static synchronized void applyPattern(AttackPattern pattern, Player subject) {
        pattern.setAuthor(subject);
        pattern.resetAllModules();

        List<AttackModule> first = pattern.getFirst()
                .stream()
                .map(pattern::getModule)
                .collect(Collectors.toList());

        // prompt details are missing
        int nextIndex = first.size() > 1 ? Dispatcher.requestIndex(MODULE_CHOOSE, first) : 0;
        int nextId = pattern.getFirst().get(nextIndex);
        while(nextId != -1) {
            AttackModule attackModule = first.get(nextId);
            List<Target> targets = attackModule.getTargets();
            for(Target target : targets) {
                if(target.getType() == TargetType.PLAYER) {
                    List<Player> players = ((TargetPlayer) target).filter();

                    ((TargetPlayer) target).setPlayer(
                            players.get(
                                    Dispatcher.requestIndex(target.getMessage(), players.stream().map(Player::getName).collect(Collectors.toList()))
                            )
                    );
                }
                else if(target.getType() == TargetType.CELL) {
                    List<Cell> cells = ((TargetCell) target).filter();

                    ((TargetCell) target).setCell(
                            cells.get(
                                    Dispatcher.requestIndex(target.getMessage(), cells.stream().map(Cell::getId).collect(Collectors.toList()))
                            )
                    );
                }
                else if(target.getType() == TargetType.ROOM) {
                    List<Room> rooms = ((TargetRoom) target).filter();

                    ((TargetRoom) target).setRoom(
                            rooms.get(
                                    Dispatcher.requestIndex(target.getMessage(), rooms.stream().map(Room::toString).collect(Collectors.toList()))
                            )
                    );
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

            List<Integer> next = attackModule.getNext();
            nextId = next.size() > 1 ? Dispatcher.requestIndex(MODULE_CHOOSE, next) : 0;
        }
    }
}
