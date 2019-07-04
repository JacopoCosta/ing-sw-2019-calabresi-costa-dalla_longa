package it.polimi.ingsw.network.client.communication.socket;

import it.polimi.ingsw.network.client.communication.ServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;

import java.io.*;
import java.net.Socket;

/**
 * A {@code SocketClientCommunicationInterface} offers a transparent way to send and receive {@link NetworkMessage}s via the
 * {@code Socket} protocol. To achieve such goal, this class implements the {@link ServerCommunicationInterface} interface.
 *
 * @see ServerCommunicationInterface
 */
public class SocketServerCommunicationInterface implements ServerCommunicationInterface {
    /**
     * The {@code Socket} instance belonging to the current client session.
     */
    private final Socket socket;

    /**
     * The standard output stream used to send {@link NetworkMessage}s to the remote {@code Server}.
     */
    private final ObjectOutputStream out;

    /**
     * The standard input stream used to receive {@link NetworkMessage}s from the remote {@code Server}.
     */
    private final ObjectInputStream in;

    /**
     * This is the only constructor. It creates a new {@code SocketServerCommunicationInterface} from the given arguments.
     *
     * @param hostAddress the remote {@code Server} ip address to connect to.
     * @param port        the remote {@code Server} port to listen from.
     * @throws ConnectionException if any lower level socket exception is thrown by the input and output streams.
     */
    public SocketServerCommunicationInterface(String hostAddress, int port) throws ConnectionException {
        try {
            this.socket = new Socket(hostAddress, port);

            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Safely closes the socket connection and terminate the current session
     *
     * @throws IOException if some error occurs during the closing procedure
     */
    private void closeConnection() throws IOException {
        this.in.close();
        this.out.close();
        this.socket.close();
    }

    /**
     * Sends a given {@link NetworkMessage} through an {@link ObjectOutputStream} stream using the {@code Socket} protocol.
     *
     * @param message the {@link NetworkMessage} to be sent.
     * @throws ConnectionException if any exception is thrown by the output stream.
     */
    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            this.out.writeObject(message);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Reads an incoming {@link NetworkMessage} from the {@link ObjectInputStream} stream using the {@code Socket} protocol.
     *
     * @return the incoming {@link NetworkMessage}.
     * @throws ConnectionException if any exception is thrown by the input stream.
     */
    @Override
    public NetworkMessage nextMessage() throws ConnectionException {
        try {
            NetworkMessage message;

            do message = (NetworkMessage) this.in.readObject();
            while (message.getType().equals(MessageType.PING_MESSAGE));

            if (message.getType() == MessageType.UNREGISTER_SUCCESS)
                this.closeConnection();

            return message;
        } catch (IOException | ClassNotFoundException e) {
            throw new ConnectionException(e);
        }
    }
}
