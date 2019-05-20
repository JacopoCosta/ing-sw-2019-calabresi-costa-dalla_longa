package it.polimi.ingsw.network.server.communication.socket;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SocketClientCommunicationInterface implements ClientCommunicationInterface {
    private final ObjectOutputStream out;

    public SocketClientCommunicationInterface(ObjectOutputStream out) {
        this.out = out;
    }

    @Override
    public void sendMessage(Message message) throws ConnectionException {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new ConnectionException("Client connection error", e);
        }
    }
}
