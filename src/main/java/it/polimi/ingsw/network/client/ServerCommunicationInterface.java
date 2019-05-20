package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;

public interface ServerCommunicationInterface {
    void sendMessage(Message message) throws ConnectionException;

    Message nextMessage() throws ConnectionException;
}
