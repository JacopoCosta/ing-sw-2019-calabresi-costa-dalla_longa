package it.polimi.ingsw.network.server.communicationInterface.socket;

import it.polimi.ingsw.network.common.socket.SocketMessage;
import it.polimi.ingsw.network.server.communicationInterface.CommunicationInterface;

import java.io.PrintWriter;
import java.util.NoSuchElementException;

public class SocketCommunicationInterface implements CommunicationInterface {
    private final PrintWriter out;

    public SocketCommunicationInterface(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void sendMessage(String message) throws NoSuchElementException {
        out.println(SocketMessage.MESSAGE_INCOMING);
        out.println(message);
    }
}
