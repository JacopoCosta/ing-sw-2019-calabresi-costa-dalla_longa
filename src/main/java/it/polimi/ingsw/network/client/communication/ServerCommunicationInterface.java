package it.polimi.ingsw.network.client.communication;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.NetworkMessage;

public interface ServerCommunicationInterface {
    void sendMessage(NetworkMessage message) throws ConnectionException;

    NetworkMessage nextMessage() throws ConnectionException;
}
