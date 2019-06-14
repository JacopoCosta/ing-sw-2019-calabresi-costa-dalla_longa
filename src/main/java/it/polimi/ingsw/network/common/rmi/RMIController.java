package it.polimi.ingsw.network.common.rmi;

import it.polimi.ingsw.network.common.message.NetworkMessage;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * An {@code RMIController} interface offers a standardized way to notify a class about the presence of a new {@link NetworkMessage}
 * that needs to be read and processed. Even though this interface works with all kinds of communication protocol, it should
 * be only extended by a class that uses the RMI communication protocol.
 */
public interface RMIController extends Remote {
    /**
     * Notifies the extender class about the presence of a new {@link NetworkMessage}, sent via the RMI communication
     * protocol.
     *
     * @param message the new {@link NetworkMessage} sent to the extender class.
     * @throws RemoteException if any exception is thrown at a lower level.
     */
    void notifyMessageReceived(NetworkMessage message) throws RemoteException;
}
