package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;

public interface ClientCommunicationInterface {
    void sendMessage(NetworkMessage message) throws ConnectionException;
}
