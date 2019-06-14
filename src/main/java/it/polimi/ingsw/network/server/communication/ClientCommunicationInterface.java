package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.network.client.communication.ServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.server.VirtualClient;

/**
 * Provides a standardized method to send {@link NetworkMessage}s from a {@link VirtualClient} or any other server-side
 * entity to a client-side entity.
 * Note that this interface provides a unidirectional communication and should not be used from a client-side entity
 * to communicate with a server-side entity. To do so, please refer to {@link ServerCommunicationInterface}.
 */
public interface ClientCommunicationInterface {

    /**
     * Provides a standardized method to send a given {@link NetworkMessage} to a client-side entity.
     *
     * @param message the {@link NetworkMessage} to be sent.
     * @throws ConnectionException if any network related exception is thrown by the implementer method.
     */
    void sendMessage(NetworkMessage message) throws ConnectionException;
}
