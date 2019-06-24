package it.polimi.ingsw.network.server.communication.socket;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * A {@code SocketClientCommunicationInterface} offers a transparent way to send {@link NetworkMessage}s via the
 * {@code Socket} protocol. To achieve such goal, this class implements the {@link ClientCommunicationInterface} interface.
 *
 * @see ClientCommunicationInterface
 */
public class SocketClientCommunicationInterface implements ClientCommunicationInterface {
    /**
     * The {@link ObjectOutputStream} used to send {@link NetworkMessage} via {@code Socket} protocol.
     */
    private final ObjectOutputStream out;

    /**
     * This is the only constructor used to create a new {@code SocketClientCommunicationInterface} by specifying the
     * {@link ObjectOutputStream} stream used by the {@code Socket} communication protocol.
     *
     * @param out the {@link ObjectOutputStream} stream to communicate with.
     */
    SocketClientCommunicationInterface(ObjectOutputStream out) {
        this.out = out;
    }

    /**
     * Sends a given {@link NetworkMessage} through an {@link ObjectOutputStream} stream using the {@code Socket} protocol.
     *
     * @param message the {@link NetworkMessage} to be sent.
     * @throws ConnectionException if any other exception is thrown at a lower level.
     */
    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            this.out.writeObject(message); // FIXME this always throws NotSerializableException
        } catch (IOException e) {
            e.printStackTrace(); // I added this so that the call trace is visible
            throw new ConnectionException(e);
        }
    }
}
