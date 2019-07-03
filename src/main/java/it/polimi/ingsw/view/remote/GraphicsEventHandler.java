package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.deliverable.*;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.view.remote.gui.Token;
import it.polimi.ingsw.view.remote.status.*;

import java.util.ArrayList;
import java.util.List;

public class GraphicsEventHandler {

    private boolean usesGUI;
    private CommunicationHandler communicationHandler;

    private static Console console = Console.getInstance();

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
            actionFilter(deliverable);
        } catch (ConnectionException e) {
            System.exit(-1);
        }

    }

    private void actionFilter(Deliverable deliverable) throws ConnectionException {

        System.out.println("received deliverable of type " + deliverable.getType() + " of event " + deliverable.getEvent());

        if(usesGUI) {

            if(deliverable.getType() == DeliverableType.BULK) {
                bulkHandler(deliverable, true);
            }

            else {
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
                case UPDATE_FRENZY:
                    break;
                case UPDATE_WINNER:
                    break;
                case UPDATE_DISCONNECT:
                    break;
                //FIXME: this list needs an update (will be done when creating GUI)
                } //end switch(deliverable.getEvent())
            } //end else (not BULK)
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
                    bulkHandler(deliverable, false);
                    break;
            }
        }
    }

    private void CLIInfoHandler(Deliverable deliverable) {
        console.tinyPrintln(deliverable.getMessage());
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


    @SuppressWarnings("unchecked")
    private void bulkHandler(Deliverable deliverable, boolean usesGUI) {
        Object bulk = ((Bulk) deliverable).unpack();

        switch (deliverable.getEvent()) {
            case UPDATE_DAMAGE:

                //creates a new damageList from the deliverable content, then sets it as damageList of the player
                List<String> newDamageList = new ArrayList<>();

                for(int i=1; i < ((List<String>) bulk).size(); i++) {
                    newDamageList.add(((List<String>) bulk).get(i));
                }
                //sets damageList as new damageList for the player who needs the update
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1).setDamage(newDamageList);

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
                int participantIndex = ((List<Integer>) bulk).get(0);
                int newPositionIndex = -1;

                if (((List<Object>) bulk).get(1) != null)   //it happens when the player hasn't spawned yet
                    newPositionIndex = ((List<Integer>) bulk).get(1);
                RemoteBoard.getParticipants().get(participantIndex - 1).setPosition(newPositionIndex);
                //NOTE:
                //Players Ids range from 1 to n, so its value must be decreased by 1 to get its location in RemoteBoard.getParticipants();
                //Cells Id also range from 1 to n, but in this case we are just setting player's position: we are not getting a cell position
                //in a list, that's why its value must not be decreased by 1.

                break;
            case UPDATE_SCORE:
                //sets the new player score extracting it from content
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1).setScore(((List<Integer>) bulk).get(1));

                break;
            case UPDATE_DEATH_COUNT:
                //sets the deathcount of this player
                RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) -1).setDeathCount(((List<Integer>) bulk).get(1));

                break;
            case UPDATE_INVENTORY:

                updateInventory(bulk);

                break;
            case UPDATE_BOARD_KILL:

                List<String> boardKill = new ArrayList<>();

                //adds new names to the new boardKill
                for(int i=0; i < ((List<String>) bulk).size(); i++) {
                    boardKill.add(((List<String>) bulk).get(i));
                }
                //replaces the old boardKill with the new boardKill
                RemoteBoard.setKillers(boardKill);

                break;
            case UPDATE_BOARD_DOUBLE_KILL:

                List<String> boardDoubleKill = new ArrayList<>();

                //adds new names to the new boardDoubleKill
                for(int i=0; i < ((List<String>) bulk).size(); i++) {
                    boardDoubleKill.add(((List<String>) bulk).get(i));
                }
                //replaces the old boardDoubleKIll with the new one
                RemoteBoard.setDoubleKillers(boardDoubleKill);

                break;
            case UPDATE_CELL:

                RemoteCell cell = RemoteBoard.getCellByLogicalIndex(((List<Integer>) bulk).get(0) - 1);    //Shorthand for the cell that must be modified

                if(((List<String>) bulk).get(1).equals("0")) {  //cell is an AmmoCell

                    cell.setAmmoCell(true); //it will never produce NPE
                    int redAmmo = ((List<Integer>) bulk).get(2);
                    int yellowAmmo = ((List<Integer>) bulk).get(3);
                    int blueAmmo = ((List<Integer>) bulk).get(4);
                    boolean includesPowerUp = ((List<String>) bulk).get(5).equals("1");

                    cell.rewrite(redAmmo, yellowAmmo, blueAmmo, includesPowerUp);
                } //end if (ammoCell)
                else {

                    cell.setAmmoCell(false); //it will never produce NPE

                    switch (((List<String>) bulk).get(2).toLowerCase()) {
                        case "red":
                            cell.rewrite(1, 0, 0, false);
                            break;
                        case "yellow":
                            cell.rewrite(0, 1, 0, false);
                            break;
                        case "blue":
                            cell.rewrite(0, 0, 1, false);
                            break;
                    }

                    List<RemoteWeapon> newShopList = new ArrayList<>();

                    for(int i = 3; i < ((List<Object>) bulk).size(); i++) {

                        Object weaponElement = ((List<String>) bulk).get(i);    //element of the weaponList

                        String weaponName = ((List<String>) weaponElement).get(0);
                        String purchaseCost = ((List<String>) weaponElement).get(1);
                        String reloadCost = ((List<String>) weaponElement).get(2);

                        newShopList.add(new RemoteWeapon(weaponName, purchaseCost, reloadCost, true));
                    }
                    cell.setShop(newShopList);
                }

                break;
            case BOARD_INIT:

                boardInitializer(bulk);
                RemoteBoard.setUsername(communicationHandler.getUsername());

                if(usesGUI) {

                }

                break;
            case STATUS_UPDATE:
                if(usesGUI) {
                    //TODO
                }
                else {  //cli printing methods
                    BoardGraph.printBoardStatus();
                    BoardGraph.printBoard();
                    for (RemotePlayer p : RemoteBoard.getParticipants()) {
                        BoardGraph.printPlayerStatus(p, p.isUser());    //TODO: this can be improved
                    }
                } //end else (using cli)
                break;

        }
    }

    private void updateInventory(Object bulk) {

        List<RemotePowerUp> powerUps = new ArrayList<>();
        List<RemoteWeapon> weapons = new ArrayList<>();

        RemotePlayer player = RemoteBoard.getParticipants().get(((List<Integer>) bulk).get(0) - 1);    //Shorthand for the player who must be updated

        //update onFrenzy
        player.setOnFrenzy(((List<Boolean>) bulk).get(1));

        //update ammo
        player.setRedAmmo(((List<Integer>)((List<Object>) bulk).get(2)).get(0));    //yes, the cast is right
        player.setYellowAmmo(((List<Integer>)((List<Object>) bulk).get(2)).get(1));
        player.setBlueAmmo(((List<Integer>)((List<Object>) bulk).get(2)).get(2));

        //update powerups
        for(int i = 0; i < ((List<String>)((List<Object>) bulk).get(3)).size(); i++) {

            Object powerUpList = ((List<Object>) bulk).get(2);
            Object powerUpElement = ((List<Object>) powerUpList).get(i);

            String powerUpType = ((List<String>) powerUpElement).get(0);
            String powerUpColor = ((List<String>) powerUpElement).get(1);

            powerUps.add(new RemotePowerUp(powerUpType, powerUpColor));
        }//end for
        player.setPowerUps(powerUps);

        //update weapons
        for(int i = 0; i < ((List<String>)((List<Object>) bulk).get(4)).size(); i++) {

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

    private void boardInitializer(Object bulk) {

        //extracts info about the board
        RemoteBoard.calculateBoardImage(((List<Integer>) bulk).get(0));
        RemoteBoard.setWidth(((List<Integer>) bulk).get(1));
        RemoteBoard.setHeight(((List<Integer>) bulk).get(2));

        //extracts board morphology (sent as List<ContentType>)
        List<ContentType> morphology = (List<ContentType>) ((List<Object>) bulk).get(3);
        RemoteBoard.setMorphology(morphology);
        RemoteBoard.generateCellScheme();

        //extracts info about participants, adding them to the RemoteBoard
        List<String> playerNames = (List<String>) ((List<Object>) bulk).get(4);
        List<RemotePlayer> participants = new ArrayList<>();
        for(int i=0; i < playerNames.size(); i++) {

            RemotePlayer remotePlayer = new RemotePlayer(playerNames.get(i), participants.size());
            remotePlayer.setToken(new Token(i));    //since i is a progressive number, every player will have a different token associated
            participants.add(remotePlayer);
        }
        RemoteBoard.setParticipants(participants);
    }

}