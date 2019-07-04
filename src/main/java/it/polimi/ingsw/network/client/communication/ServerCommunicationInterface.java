package it.polimi.ingsw.network.client.communication;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.network.client.executable.Client;

/**
 * Provides a standardized method to send and receive {@link NetworkMessage}s from a {@link Client} entity to a server-side entity.
 * Note that this interface provides a unidirectional communication and should not be used from a server-side entity
 * to communicate with a client-side entity. To do so, please refer to {@link ClientCommunicationInterface}.
 */
public interface ServerCommunicationInterface {
    /**
     * Provides a standardized method to send a given {@link NetworkMessage} to a server-side entity.
     *
     * @param message the {@link NetworkMessage} to be sent.
     * @throws ConnectionException if any network related exception is thrown by the implementer method.
     */
    void sendMessage(NetworkMessage message) throws ConnectionException;

    /**
     * Provides a standardized method to receive a given {@link NetworkMessage} from a server-side entity.
     *
     * @return the received {@link NetworkMessage}.
     * @throws ConnectionException if any network related exception is thrown by the implementer method.
     */
    NetworkMessage nextMessage() throws ConnectionException;
}
