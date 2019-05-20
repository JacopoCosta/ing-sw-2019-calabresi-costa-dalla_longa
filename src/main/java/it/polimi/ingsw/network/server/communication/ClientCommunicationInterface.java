package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;

public interface ClientCommunicationInterface {
    void sendMessage(Message message) throws ConnectionException;
}
