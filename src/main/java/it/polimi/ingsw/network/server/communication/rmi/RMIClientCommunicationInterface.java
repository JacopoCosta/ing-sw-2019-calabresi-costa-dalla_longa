package it.polimi.ingsw.network.server.communication.rmi;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

import java.rmi.RemoteException;

/**
 * A {@code SocketClientCommunicationInterface} offers a transparent way to send {@link NetworkMessage}s via the
 * {@code RMI} protocol. To achieve such goal, this class implements the {@link ClientCommunicationInterface} interface.
 *
 * @see ClientCommunicationInterface
 */
public class RMIClientCommunicationInterface implements ClientCommunicationInterface {
    /**
     * The {@link RMIController} representing the remote client-side RMI interface to whose the message will be sent to.
     */
    private final RMIController clientController;

    /**
     * This is the only constructor. It creates a new {@code RMICommunicationInterface} by specifying the {@link RMIController}
     * interface representing the remote client-side application to receive the {@link NetworkMessage}s from.
     *
     * @param clientController the {@link RMIController} from whose the {@link NetworkMessage}s will be sent from.
     */
    RMIClientCommunicationInterface(RMIController clientController) {
        this.clientController = clientController;
    }

    /**
     * Sends a given {@link NetworkMessage} through an {@link RMIController} stream using the {@code RMI} protocol.
     *
     * @param message the {@link NetworkMessage} to be sent.
     * @throws ConnectionException if any exception is thrown at a lower level.
     */
    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            this.clientController.notifyMessageReceived(message);
        } catch (RemoteException e) {
            throw new ConnectionException(e);
        }
    }
}
