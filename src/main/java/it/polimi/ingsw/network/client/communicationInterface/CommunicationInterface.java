package it.polimi.ingsw.network.client.communicationInterface;

import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;

import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public interface CommunicationInterface {
    void register(String username) throws RemoteException, NoSuchElementException, ConnectionException, ClientAlreadyRegisteredException;

    void unregister(String username) throws RemoteException, NoSuchElementException, ConnectionException, ClientNotFoundException;
}