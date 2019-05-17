package it.polimi.ingsw.controller;


import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.powerups.PowerUp;

public class Controller implements Controllable {

    private static final String EXECUTION_REQUEST = "Choose a moveset:";

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

    public int getExecutionIndex(Player subject, int upperBound) {
        return Dispatcher.requestInteger(EXECUTION_REQUEST, 0, upperBound);
    }

}
