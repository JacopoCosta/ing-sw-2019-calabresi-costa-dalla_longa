package it.polimi.ingsw.network.server.communicationInterface;

import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public interface CommunicationInterface {
    void sendMessage(String message) throws RemoteException, NoSuchElementException;
}