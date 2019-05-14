package it.polimi.ingsw.network.server.communicationInterface.rmi;

import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.common.rmi.ClientController;
import it.polimi.ingsw.network.common.rmi.ServerController;
import it.polimi.ingsw.network.server.CommunicationHub;
import it.polimi.ingsw.network.server.communicationInterface.Client;
import it.polimi.ingsw.network.server.communicationInterface.CommunicationInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerControllerImpl extends UnicastRemoteObject implements ServerController {
    private final CommunicationHub communicationHub;

    public ServerControllerImpl() throws RemoteException {
        communicationHub = CommunicationHub.getInstance();
    }

    @Override
    public void register(String username, ClientController clientController) throws ClientAlreadyRegisteredException {
        CommunicationInterface clientInterface = new RmiCommunicationInterface(clientController);
        Client client = new Client(username, clientInterface);
        communicationHub.register(client);
    }

    @Override
    public void unregister(String username) throws ClientNotFoundException {
        communicationHub.unregister(communicationHub.getClientByName(username));
    }
}