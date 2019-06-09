package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.powerups.PowerUpType;
import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.status.RemoteBoard;
import it.polimi.ingsw.view.remote.status.RemotePlayer;
import it.polimi.ingsw.view.remote.status.RemotePowerUp;
import it.polimi.ingsw.view.remote.status.RemoteWeapon;
import it.polimi.ingsw.view.virtual.*;

import java.util.ArrayList;
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
                case UPDATE_BOARDKILL:
                    break;
                case UPDATE_SCORE:
                    break;
                case UPDATE_FRENZY:
                    break;
                case UPDATE_WINNER:
                    break;
                case UPDATE_DISCONNECT:
                    break;
                    //FIXME: this list needs an update (will be done when creating GUI)
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

                //creates a new damageList from the deliverable content, then sets it as damageList of the player
                List<String> damageList = new ArrayList<>();

                for(int i=1; i < ((List<String>) bulk).size(); i++) {
                    damageList.add(((List<String>) bulk).get(i));
                }
                //sets damageList as new damageList for the player who needs the update
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1).setDamage(damageList);

                break;
            case UPDATE_MARKING:
                //creates a new markingList from the deliverable content, then sets it as markingList of the player
                List<String> markingList = new ArrayList<>();

                for(int i=1; i < ((List<String>) bulk).size(); i++) {
                    markingList.add(((List<String>) bulk).get(i));
                }
                //sets markingList as new markingList for the player who needs the update
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1).setMarkings(markingList);

                break;
            case UPDATE_MOVE:
                //simply sets the new player position to the new position written in the deliverable content
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1).setPosition(((List<Integer>) bulk).get(1));
                //NOTE:
                //Players Ids range from 1 to n, so its value must be decreased by 1 to get its location in RemoteBoard.getParticipants();
                //Cells Id also range from 1 to n, but in this case we are just setting player's position: we are not getting a cell position
                //in a list, that's why its value must not be decreased by 1.

                break;
            case UPDATE_SCORE:
                //sets the new player score extracting it from content
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1).setScore(((List<Integer>) bulk).get(1));

                break;
            case UPDATE_DEATHCOUNT:
                //sets the deathcount of this player
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) -1).setDeathCount(((List<Integer>) bulk).get(1));

                break;
            case UPDATE_INVENTORY:

                UpdateInventory(bulk);

                break;
            case UPDATE_BOARDKILL:

                List<String> boardKill = new ArrayList<>();

                //adds new names to the new boardKill
                for(int i=0; i < ((List<String>) bulk).size(); i++) {
                    boardKill.add(((List<String>) bulk).get(i));
                }
                //replaces the old boardKill with the new boardKill
                RemoteBoard.setKillers(boardKill);

                break;
            case UPDATE_BOARDDOUBLEKILL:

                List<String> boardDoubleKill = new ArrayList<>();

                //adds new names to the new boardDoubleKill
                for(int i=0; i < ((List<String>) bulk).size(); i++) {
                    boardDoubleKill.add(((List<String>) bulk).get(i));
                }
                //replaces the old boardDoubleKIll with the new one
                RemoteBoard.setDoubleKillers(boardDoubleKill);

                break;
            case STATUS_INIT:

                StatusInitializer(bulk);
                break;
        }
    }

    private void UpdateInventory(Object bulk) {

        List<RemotePowerUp> powerUps = new ArrayList<>();
        List<RemoteWeapon> weapons = new ArrayList<>();

        RemotePlayer player = RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1);    //Shorthand for the player who must be updated

        //update ammo
        player.setRedAmmo(((List<Integer>)((List<Object>) bulk).get(1)).get(0));    //yes, the cast is right
        player.setYellowAmmo(((List<Integer>)((List<Object>) bulk).get(1)).get(1));
        player.setBlueAmmo(((List<Integer>)((List<Object>) bulk).get(1)).get(2));

        //update powerups
        for(int i = 0; i < ((List<String>)((List<Object>) bulk).get(2)).size(); i++) {

            Object powerUpList = ((List<Object>) bulk).get(2);
            Object powerUpElement = ((List<Object>) powerUpList).get(i);

            String powerUpType = ((List<String>) powerUpElement).get(0);
            String powerUpColor = ((List<String>) powerUpElement).get(1);
            switch (powerUpType) {
                case ("GRENADE"):
                    powerUps.add(new RemotePowerUp(PowerUpType.GRENADE, powerUpColor));
                    break;
                case ("NEWTON"):
                    powerUps.add(new RemotePowerUp(PowerUpType.NEWTON, powerUpColor));
                    break;
                case ("SCOPE"):
                    powerUps.add(new RemotePowerUp(PowerUpType.SCOPE, powerUpColor));
                    break;
                case ("TELEPORT"):
                    powerUps.add(new RemotePowerUp(PowerUpType.TELEPORT, powerUpColor));
                    break;
            } //end switch
        }//end for
        player.setPowerUps(powerUps);

        //update weapons
        for(int i = 0; i < ((List<String>)((List<Object>) bulk).get(3)).size(); i++) {

            Object weaponList = ((List<Object>) bulk).get(3);
            Object weaponElement = ((List<Object>) weaponList).get(i);

            String weaponName = ((List<String>) weaponElement).get(0);
            String purchaseCost = ((List<String>) weaponElement).get(1);
            String reloadCost = ((List<String>) weaponElement).get(2);
            String isLoaded = ((List<String>) weaponElement).get(3);

            weapons.add(new RemoteWeapon(weaponName, purchaseCost, reloadCost, isLoaded.equals("1")));
        }//end for
        player.setWeapons(weapons);

    }

    private void StatusInitializer(Object bulk) {
        //TODO
    }

}