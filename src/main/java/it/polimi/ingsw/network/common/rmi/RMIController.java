package it.polimi.ingsw.network.common.rmi;

import it.polimi.ingsw.network.common.message.NetworkMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIController extends Remote {
    int DEFAULT_PORT = 1099;

    void notifyMessageReceived(NetworkMessage message) throws RemoteException;
}
