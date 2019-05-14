package it.polimi.ingsw.network.client.communicationInterface.rmi;

import it.polimi.ingsw.network.client.communicationInterface.CommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.common.rmi.ClientController;
import it.polimi.ingsw.network.common.rmi.ServerController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiCommunicationInterface implements CommunicationInterface {
    private ServerController serverController;

    public RmiCommunicationInterface(String hostAddress, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(hostAddress);
        serverController = (ServerController) registry.lookup("rmi://" + hostAddress + ":" + port + "/network");
    }

    @Override
    public void register(String username) throws RemoteException, ClientAlreadyRegisteredException {
        ClientController clientController = new ClientControllerImpl();
        serverController.register(username, clientController);
    }

    @Override
    public void unregister(String username) throws RemoteException, ClientNotFoundException {
        serverController.unregister(username);
    }
}
