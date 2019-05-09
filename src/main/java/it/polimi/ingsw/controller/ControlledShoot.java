package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.ConstraintNotSatisfiedException;
import it.polimi.ingsw.model.exceptions.InvalidMoveException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Shoot;
import it.polimi.ingsw.model.weaponry.Action;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledShoot {
    private static final String WEAPON_CHOOSE = "Which weapon would you like to shoot with?";
    private static final String ACTION_CHOOSE = "Choose how to attack:";
    private static final String TARGET_CHOOSE = "Who do you want to target?";

    protected static synchronized void routine(Player subject, Shoot shoot) {
        subject.savePosition();

        boolean shootDone = false;
        while(!shootDone) { // until the entire activity has been completed
            List<Weapon> weapons = subject.getWeapons();
            Weapon weapon = null;
            boolean weaponDone = false;
            while(!weaponDone) { // until a valid (loaded) weapon is chosen
                int weaponId = Dispatcher.requestInteger(WEAPON_CHOOSE, 0, weapons.size());
                weapon = weapons.get(weaponId);
                if(weapon.isLoaded()) {
                    subject.loadActionsFromWeapon(weapon);
                    weaponDone = true;
                }
            }
            boolean actionDone = false;
            while(!actionDone) { // until an action is chosen and completed
                subject.restorePosition(); // TODO although logically correct, restoring a player's position after the weapon choice might be confusing
                List<Action> actions = weapon.getActions();
                int actionId = Dispatcher.requestInteger(ACTION_CHOOSE, 0, actions.size());
                Action action = actions.get(actionId);

                shoot.setAction(action);
                try {
                    shoot.perform(subject);
                } catch (ConstraintNotSatisfiedException e) {
                    e.printStackTrace();
                } catch (InvalidMoveException e) {
                    e.printStackTrace();
                }

                boolean effectDone = false;
                while(!effectDone) { // until each effect is completed

                }
            }
        }
    }

    public static synchronized Player chooseTarget(Player author) {
        List<Player> targets = author.getPosition()
                                .getBoard()
                                .getGame()
                                .getParticipants()
                                .stream()
                                .filter(p -> !p.equals(author))
                                .collect(Collectors.toList());
        int targetId = Dispatcher.requestInteger(TARGET_CHOOSE, 0, targets.size());
        return targets.get(targetId);
    }

}
