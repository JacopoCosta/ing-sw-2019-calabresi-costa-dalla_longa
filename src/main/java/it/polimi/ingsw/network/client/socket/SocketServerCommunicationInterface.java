package it.polimi.ingsw.network.client.socket;

import it.polimi.ingsw.network.client.ServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketServerCommunicationInterface implements ServerCommunicationInterface {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;


    public SocketServerCommunicationInterface(String hostAddress, int port) throws ConnectionException {
        try {
            socket = new Socket(hostAddress, port);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new ConnectionException("Socket communication error", e);
        }
    }

    private void closeConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    @Override
    public void sendMessage(Message message) throws ConnectionException {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new ConnectionException("Connection error to the server", e);
        }
    }

    @Override
    public Message nextMessage() throws ConnectionException {
        try {
            Message message = (Message) in.readObject();

            if (message.getType().equals(MessageType.UNREGISTER_SUCCESS))
                closeConnection();

            return message;
        } catch (IOException | ClassNotFoundException e) {
            throw new ConnectionException("Connection error to the server", e);
        }
    }
}
