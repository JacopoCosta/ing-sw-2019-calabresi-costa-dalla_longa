package it.polimi.ingsw.controller;


import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.Dispatcher;

import java.util.List;

public class Controller implements Controllable {

    private static final String EXECUTION_REQUEST = "choose a moveset:";
    private static final String WEAPONS_FULL = "Looks like your hand is full of weapons, please discard one:";
    private static final String POWERUPS_FULL = "Looks like your hand is full of weapons, please discard one:";

    private Game game;

    public Controller(Game game) {
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public void activityRoutine(Player subject, Activity activity) {
        switch(activity.getType()) {
            case MOVE:
                ControlledMove.routine(subject, (Move) activity);
                break;
            case GRAB:
                ControlledGrab.routine(subject);
                break;
            case SHOOT:
                ControlledShoot.routine(subject);
                break;
            case RELOAD:
                ControlledReload.routine(subject);
                break;
            default:
                break;
        }
    }

    public void powerUpRoutine(Player subject, PowerUp powerUp) {
        switch(powerUp.getType()) {
            case GRENADE:
                ControlledGrenade.routine(subject, powerUp);
                break;
            case NEWTON:
                ControlledNewton.routine(subject, powerUp);
                break;
            case SCOPE:
                ControlledScope.routine(subject, powerUp);
                break;
            case TELEPORT:
                ControlledTeleport.routine(subject, powerUp);
                break;
            default:
                break;
        }
    }

    public void discardWeaponRoutine(Player subject) {
        List<Weapon> weapons = subject.getWeapons();
        int discardIndex = Dispatcher.requestIndex(WEAPONS_FULL, weapons);
        subject.discardWeapon(weapons.get(discardIndex));
    }

    public void discardPowerUpRoutine(Player subject) {
        List<PowerUp> powerUps = subject.getPowerUps();
        int discardIndex = Dispatcher.requestIndex(POWERUPS_FULL, powerUps);
        subject.discardPowerUp(powerUps.get(discardIndex));
    }

    public Execution requestExecution(Player subject, List<Execution> executions) {
        return executions.get(
                Dispatcher.requestIndex(subject.getName() + ", " + EXECUTION_REQUEST, executions)
        );
    }

}
