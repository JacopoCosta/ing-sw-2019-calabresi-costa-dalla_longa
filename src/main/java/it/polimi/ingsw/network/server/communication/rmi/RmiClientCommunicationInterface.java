package it.polimi.ingsw.network.server.communication.rmi;

import it.polimi.ingsw.network.common.controller.RmiController;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

import java.rmi.RemoteException;

public class RmiClientCommunicationInterface implements ClientCommunicationInterface {
    private final RmiController clientController;

    public RmiClientCommunicationInterface(RmiController clientController) {
        this.clientController = clientController;
    }

    /*
    * message sent to the Client
    * */
    @Override
    public void sendMessage(Message message) throws ConnectionException {
        try {
            clientController.notifyMessageReceived(message);
        } catch (RemoteException e) {
            throw new ConnectionException("Server communication error", e);
        }
    }
}
