package it.polimi.ingsw.network.client.communication.rmi;

import it.polimi.ingsw.network.common.message.MessageController;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;

public class ClientController extends MessageController implements RMIController {
    ClientController() {
        super();
    }

    /*
     * called when a new Message has been received from the server
     * */
    @Override
    public synchronized void notifyMessageReceived(NetworkMessage message) {
        onMessageReceived(message);
    }

    public NetworkMessage getMessage() {
        return getNextMessage();
    }
}
