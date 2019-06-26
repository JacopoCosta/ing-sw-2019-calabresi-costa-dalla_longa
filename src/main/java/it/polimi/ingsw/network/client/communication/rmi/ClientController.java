package it.polimi.ingsw.network.client.communication.rmi;

import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;

public class ClientController implements RMIController {
    private NetworkMessage message;
    private boolean receivedNew;

    ClientController() {
        message = null;
        receivedNew = false;
    }

    /*
     * called when a new Message has been received from the server
     * */
    @Override
    public synchronized void notifyMessageReceived(NetworkMessage message) {
        this.message = message;
        receivedNew = true;
    }

    @SuppressWarnings({"WhileLoopSpinsOnField", "StatementWithEmptyBody"})
    synchronized NetworkMessage nextMessage() {
        while (!receivedNew) ;

        receivedNew = false;
        return message;
    }
}
