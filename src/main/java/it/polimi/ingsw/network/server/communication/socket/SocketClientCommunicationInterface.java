package it.polimi.ingsw.network.server.communication.socket;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class SocketClientCommunicationInterface implements ClientCommunicationInterface {
    private final ObjectOutputStream out;

    SocketClientCommunicationInterface(ObjectOutputStream out) {
        this.out = out;
    }

    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }
}
