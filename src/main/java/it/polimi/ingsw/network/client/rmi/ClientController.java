package it.polimi.ingsw.network.client.rmi;

import it.polimi.ingsw.network.common.controller.RmiController;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageController;

public class ClientController extends MessageController implements RmiController {
    ClientController() {
        super();
    }

    /*
     * called when a new message has been message received from the server
     * */
    @Override
    public synchronized void notifyMessageReceived(Message message) {
        onMessageReceived(message);
    }

    public Message getMessage() {
        return getNextMessage();
    }
}
