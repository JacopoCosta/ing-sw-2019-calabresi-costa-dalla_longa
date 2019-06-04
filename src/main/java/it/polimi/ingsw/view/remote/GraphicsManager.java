package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.virtual.*;

public class GraphicsManager {

    private boolean usesGUI;
    private CommunicationHandler communicationHandler;

    public GraphicsManager(boolean usesGUI, CommunicationHandler communicationHandler) {
        this.usesGUI = usesGUI;
        this.communicationHandler = communicationHandler;
    }

    public void deliverableCatcher() {

        Deliverable deliverable;
        try {
            deliverable = communicationHandler.nextDeliverable();
        } catch (ConnectionException e) {
            System.exit(-1);
            return;
        }

        try {
            ActionFilter(deliverable);
        } catch (ConnectionException e) {
            System.exit(-1);
        }

    }

    private void ActionFilter(Deliverable deliverable) throws ConnectionException {

        if(usesGUI) {
            switch (deliverable.getEvent()) {
                case SPAWN_REQUEST:
                    //spawnRequest(deliverable);
                    break;
                case SPAWN_SUCCESS:
                    break;
                case DISCARD_POWERUP_REQUEST:
                    break;
                case DISCARD_WEAPON_REQUEST:
                    break;
                case CHOOSE_EXECUTION_REQUEST:
                    break;
                case MOVE_REQUEST:
                    break;
                case GRAB_AMMO_SUCCESS:
                    break;
                case GRAB_AMMO_FAILURE:
                    break;
                case GRAB_WEAPON_REQUEST_IF:
                    break;
                case GRAB_WEAPON_REQUEST_WHICH:
                    break;
                case GRAB_WEAPON_SUCCESS:
                    break;
                case GRAB_WEAPON_NEEDS_POWERUP:
                    break;
                case SHOOT_WEAPON_REQUEST:
                    break;
                case SHOOT_MODULE_REQUEST:
                    break;
                case SHOOT_PLAYER_FAILURE:
                    break;
                case SHOOT_CELL_FAILURE:
                    break;
                case SHOOT_ROOM_FAILURE:
                    break;
                case RELOAD_REQUEST_IF:
                    break;
                case RELOAD_REQUEST_WHICH:
                    break;
                case RELOAD_SUCCESS:
                    break;
                case RELOAD_NEEDS_POWERUP:
                    break;
                case POWERUP_REQUEST_IF:
                    break;
                case POWERUP_REQUEST_WHICH:
                    break;
                case SCOPE_REQUEST_IF:
                    break;
                case SCOPE_REQUEST_WHICH:
                    break;
                case SCOPE_REQUEST_AMMO:
                    break;
                case SCOPE_REQUEST_TARGET:
                    break;
                case GRENADE_REQUEST_IF:
                    break;
                case GRENADE_REQUEST_WHICH:
                    break;
                case NEWTON_REQUEST_PLAYER:
                    break;
                case NEWTON_REQUEST_CELL:
                    break;
                case NEWTON_FAILURE:
                    break;
                case TELEPORT_REQUEST_CELL:
                    break;
                case TARGET_REQUEST:
                    break;
                case ANNOUNCE_DAMAGE:
                    break;
                case ANNOUNCE_MARKING:
                    break;
                case ANNOUNCE_MOVE:
                    break;
                case ANNOUNCE_KILL:
                    break;
                case ANNOUNCE_SCORE:
                    break;
                case ANNOUNCE_FRENZY:
                    break;
                case ANNOUNCE_WINNER:
                    break;
                case ANNOUNCE_DISCONNECT:
                    break;
            }
        }//end if(usesGUI)

        else {  //the client is using CLI
            switch(deliverable.getType()) {
                case INFO:
                    infoHandler(deliverable);
                    break;
                case DUAL:
                    dualHandler(deliverable);
                    break;
                case MAPPED:
                    mappedHandler(deliverable);
                    break;
                case BULK:
                    bulkHandler(deliverable);
                    break;
            }
        }
    }

    private void infoHandler(Deliverable deliverable) {
        CLI.println(deliverable.getMessage());
    }

    private void dualHandler(Deliverable deliverable) throws ConnectionException {
        boolean choice = Dispatcher.requestBoolean(deliverable.getMessage());

        Deliverable response = new Response(choice ? 1: 0);  //sending 1 if true, 0 if false
        communicationHandler.deliver(response);
    }

    private void mappedHandler(Deliverable deliverable) throws ConnectionException {

        int choice = Dispatcher.requestMappedOption(deliverable.getMessage(), ((Mapped) deliverable).getOptions(), ((Mapped) deliverable).getKeys());

        Deliverable response = new Response(choice);
        communicationHandler.deliver(response);
    }

    private void bulkHandler(Deliverable deliverable) {
        Object info = ((Bulk) deliverable).unpack();

        switch (deliverable.getEvent()) {
            case BOARD:
                CLI.printBoard((Board) info);
                break;
        }

    }

    /*
    private void spawnRequest(Deliverable deliverable) throws ConnectionException {

        int choice = Dispatcher.requestListedOption(deliverable.getMessage(), deliverable.getOptions());

        Deliverable response = new Deliverable(choice);
        communicationHandler.deliver(response);
    }

    This is an example of how spawnRequest would look like, if it used CLI instead of GUI
     */

}