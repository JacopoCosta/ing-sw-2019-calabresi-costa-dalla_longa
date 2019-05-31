package it.polimi.ingsw.network.client.communication.socket;

import it.polimi.ingsw.network.client.communication.ServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;

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
            throw new ConnectionException(e);
        }
    }

    private void closeConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public NetworkMessage nextMessage() throws ConnectionException {
        try {
            NetworkMessage message;

            do message = (NetworkMessage) in.readObject();
            while (message.getType().equals(MessageType.PING_MESSAGE));

            if (message.getType() == MessageType.UNREGISTER_SUCCESS)
                closeConnection();

            return message;
        } catch (IOException | ClassNotFoundException e) {
            throw new ConnectionException(e);
        }
    }
}
