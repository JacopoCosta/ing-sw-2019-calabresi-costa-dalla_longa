package it.polimi.ingsw.network.client.rmi;

import it.polimi.ingsw.network.common.controller.RmiController;
import it.polimi.ingsw.network.common.message.MessageController;
import it.polimi.ingsw.network.common.message.NetworkMessage;

public class ClientController extends MessageController implements RmiController {
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
