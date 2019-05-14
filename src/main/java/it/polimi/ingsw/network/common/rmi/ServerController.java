package it.polimi.ingsw.network.common.rmi;

import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerController extends Remote {
    void register(String username, ClientController clientController) throws RemoteException, ClientAlreadyRegisteredException;

    void unregister(String username) throws RemoteException, ClientNotFoundException;
}
