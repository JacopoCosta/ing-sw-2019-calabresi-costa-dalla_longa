package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageController;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;

public abstract class VirtualClient extends MessageController {
    private final String name;
    private ClientCommunicationInterface communicationInterface;

    public VirtualClient(String name) {
        super();
        this.name = name;
        this.communicationInterface = null;
    }

    public void setCommunicationInterface(ClientCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    public String getName() {
        return name;
    }

    public void sendMessage(Message message) throws ConnectionException {
        if (communicationInterface == null)
            throw new NullPointerException("ClientCommunicationInterface is null");

        communicationInterface.sendMessage(message);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (!(object instanceof VirtualClient))
            return false;
        return ((VirtualClient) object).getName().equals(name);
    }
}
