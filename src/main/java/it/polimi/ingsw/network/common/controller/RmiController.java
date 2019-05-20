package it.polimi.ingsw.network.common.controller;

import it.polimi.ingsw.network.common.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RmiController extends Remote {
    void notifyMessageReceived(Message message) throws RemoteException;
}
