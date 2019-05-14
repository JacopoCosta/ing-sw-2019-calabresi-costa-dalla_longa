package it.polimi.ingsw.network.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientController extends Remote {
    void sendMessage(String message) throws RemoteException;
}