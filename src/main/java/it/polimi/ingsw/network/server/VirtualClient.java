package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageController;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.view.virtual.Deliverable;

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

    public void deliver(Deliverable deliverable) throws ConnectionException {
        sendMessage(Message.completeMessage(null, MessageType.CLIENT_MESSAGE, deliverable));
    }

    public Deliverable nextDeliverable() {
        Message message = getNextMessage();
        return (Deliverable) message.getContent();
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
