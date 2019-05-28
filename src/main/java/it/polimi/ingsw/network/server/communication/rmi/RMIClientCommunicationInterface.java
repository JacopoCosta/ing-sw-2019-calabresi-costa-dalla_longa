package it.polimi.ingsw.network.server.communication.rmi;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

import java.rmi.RemoteException;

public class RMIClientCommunicationInterface implements ClientCommunicationInterface {
    private final RMIController clientController;

    RMIClientCommunicationInterface(RMIController clientController) {
        this.clientController = clientController;
    }

    /*
     * Message sent to the Client
     * */
    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            clientController.notifyMessageReceived(message);
        } catch (RemoteException e) {
            throw new ConnectionException(e);
        }
    }
}
