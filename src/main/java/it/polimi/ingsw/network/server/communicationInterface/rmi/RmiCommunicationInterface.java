package it.polimi.ingsw.network.server.communicationInterface.rmi;

import it.polimi.ingsw.network.common.rmi.ClientController;
import it.polimi.ingsw.network.server.communicationInterface.CommunicationInterface;

import java.rmi.RemoteException;

public class RmiCommunicationInterface implements CommunicationInterface {
    private final ClientController clientController;

    RmiCommunicationInterface(ClientController clientController) {
        this.clientController = clientController;
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        clientController.sendMessage(message);
    }
}