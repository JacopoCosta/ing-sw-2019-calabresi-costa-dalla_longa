package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.status.RemoteBoard;
import it.polimi.ingsw.view.remote.status.RemotePlayer;
import it.polimi.ingsw.view.virtual.*;

import java.util.List;

public class GraphicsEventHandler {

    private boolean usesGUI;
    private CommunicationHandler communicationHandler;

    private RemoteBoard remoteBoard;
    private List<RemotePlayer> participants;

    public GraphicsEventHandler(boolean usesGUI, CommunicationHandler communicationHandler) {
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
                case UPDATE_DAMAGE:
                    break;
                case UPDATE_MARKING:
                    break;
                case UPDATE_MOVE:
                    break;
                case UPDATE_KILL:
                    break;
                case UPDATE_SCORE:
                    break;
                case UPDATE_FRENZY:
                    break;
                case UPDATE_WINNER:
                    break;
                case UPDATE_DISCONNECT:
                    break;
            }
        }//end if(usesGUI)

        else {  //the client is using CLI
            switch(deliverable.getType()) {
                case INFO:
                    CLIInfoHandler(deliverable);
                    break;
                case DUAL:
                    CLIDualHandler(deliverable);
                    break;
                case MAPPED:
                    CLIMappedHandler(deliverable);
                    break;
                case BULK:
                    CLIBulkHandler(deliverable);
                    break;
            }
        }
    }

    private void CLIInfoHandler(Deliverable deliverable) {
        CLI.println(deliverable.getMessage());
    }

    private void CLIDualHandler(Deliverable deliverable) throws ConnectionException {
        boolean choice = Dispatcher.requestBoolean(deliverable.getMessage());

        Deliverable response = new Response(choice ? 1: 0);  //sending 1 if true, 0 if false
        communicationHandler.deliver(response);
    }

    private void CLIMappedHandler(Deliverable deliverable) throws ConnectionException {

        int choice = Dispatcher.requestMappedOption(deliverable.getMessage(), ((Mapped) deliverable).getOptions(), ((Mapped) deliverable).getKeys());

        Deliverable response = new Response(choice);
        communicationHandler.deliver(response);
    }

    private void CLIBulkHandler(Deliverable deliverable) {
        Object bulk = ((Bulk) deliverable).unpack();

        switch (deliverable.getEvent()) {
            case UPDATE_DAMAGE:
                break;
            case UPDATE_MARKING:
                break;
            case UPDATE_MOVE:
                break;
            case UPDATE_KILL:
                break;
            case UPDATE_SCORE:
                break;
            case STATUS_INIT:
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