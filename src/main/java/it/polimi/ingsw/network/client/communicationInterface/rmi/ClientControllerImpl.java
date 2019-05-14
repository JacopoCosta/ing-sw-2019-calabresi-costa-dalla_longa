package it.polimi.ingsw.network.client.communicationInterface.rmi;

import it.polimi.ingsw.network.common.rmi.ClientController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientControllerImpl extends UnicastRemoteObject implements ClientController {
    ClientControllerImpl() throws RemoteException {
    }

    @Override
    public void sendMessage(String message) {
        System.out.println("Message from server: " + message);
    }
}