package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.client.rmi.RmiServerCommunicationInterface;
import it.polimi.ingsw.network.client.socket.SocketServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;

public class CommunicationHandler {
    public enum Interface {
        SOCKET_INTERFACE,
        RMI_INTERFACE
    }

    private final ServerCommunicationInterface communicationInterface;

    public CommunicationHandler(String hostAddress, int port, Interface interfaceType) throws ConnectionException {
        switch (interfaceType) {
            case SOCKET_INTERFACE:
                try {
                    this.communicationInterface = new SocketServerCommunicationInterface(hostAddress, port);
                } catch (ConnectionException e) {
                    throw new ConnectionException("SocketServerCommunicationInterface configuration failed", e);
                }
                break;
            case RMI_INTERFACE:
                try {
                    this.communicationInterface = new RmiServerCommunicationInterface(hostAddress, port);
                } catch (ConnectionException e) {
                    throw new ConnectionException("RmiServerCommunicationInterface configuration failed", e);
                }
                break;
            default:
                throw new ConnectionException("Interface must be of type SOCKET_INTERFACE or RMI_INTERFACE");
        }
    }

    public void sendMessage(Message message) throws ConnectionException {
        communicationInterface.sendMessage(message);
    }

    public Message nextMessage() throws ConnectionException {
        return communicationInterface.nextMessage();
    }
}
