package it.polimi.ingsw.controller;


import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.*;

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
                ControlledGrab.routine(subject, (Grab) activity);
                break;
            case SHOOT:
                ControlledShoot.routine(subject, (Shoot) activity);
                break;
            case RELOAD:
                ControlledReload.routine(subject, (Reload) activity);
                break;
            default:
                break;
        }
    }

    public int getExecutionIndex(Player subject, int upperBound) {
        return Dispatcher.requestInteger(EXECUTION_REQUEST, 0, upperBound);
    }

}
