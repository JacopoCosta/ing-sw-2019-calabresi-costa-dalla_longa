package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.MessageController;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.view.virtual.Deliverable;

/**
 * A {@code VirtualClient} represents the communication abstraction of a {@link Player}. The goal of this class is to
 * offer a communication {@code API} which is fully transparent to the {@link Player}, so that he can maintain all of
 * the logical functions separated from the network aspect, while being able to easily communicate with his remote
 * counterpart.
 */
public abstract class VirtualClient extends MessageController {

    /**
     * The {@code VirtualClient} name.
     */
    private final String name;

    /**
     * The {@link ClientCommunicationInterface} used to send and receive {@link NetworkMessage}s from his remote
     * counterpart.
     *
     * @see ClientCommunicationInterface
     * @see NetworkMessage
     */
    private ClientCommunicationInterface communicationInterface;

    /**
     * This is the only constructor. It creates a new {@code VirtualClient} with the given {@code name}.
     *
     * @param name The new {@code VirtualClient} name.
     */
    public VirtualClient(String name) {
        super();
        this.name = name;
        this.communicationInterface = null;
    }

    /**
     * Sets the {@code VirtualClient} {@link ClientCommunicationInterface} with the given one. Any method call involving
     * sending and receiving any {@link NetworkMessage} performed before this method, will result in a thrown {@link NullPointerException}.
     *
     * @param communicationInterface the interface used to connect to the remote counterpart.
     */
    public void setCommunicationInterface(ClientCommunicationInterface communicationInterface) {
        this.communicationInterface = communicationInterface;
    }

    /**
     * Returns the {@code VirtualClient} current {@link ClientCommunicationInterface}.
     * @return the {@code VirtualClient} {@link ClientCommunicationInterface}.
     */
    public ClientCommunicationInterface getCommunicationInterface(){
        return this.communicationInterface;
    }

    /**
     * Returns the current {@code name} of this {@code VirtualClient}.
     *
     * @return the {@code VirtualClient} name.
     */
    public String getName() {
        return name;
    }

    /**
     * Forwards a {@link NetworkMessage} to the lower level trough a {@link ClientCommunicationInterface}.
     *
     * @param message the {@link NetworkMessage} to be forwarded.
     * @throws ConnectionException if any exception is thrown at a lower level.
     */
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        if (this.communicationInterface == null)
            throw new NullPointerException("ClientCommunicationInterface is null");

        this.communicationInterface.sendMessage(message);
    }

    /**
     * Delivers a {@link Deliverable} to the remote client counterpart by encapsulating into a lower level {@link NetworkMessage}.
     * This behavior emulates the Transport level in an ISO/OSI stack communication protocol.
     *
     * @param deliverable the {@link Deliverable} to be encapsulate.
     * @throws ConnectionException if any exception is thrown at a lower level.
     */
    public void deliver(Deliverable deliverable) throws ConnectionException {
        sendMessage(NetworkMessage.completeServerMessage(MessageType.CLIENT_MESSAGE, deliverable));
    }

    /**
     * Returns to the caller a {@link Deliverable} received from the {@code VirtualClient} remote counterpart.
     * This is a blocking call: the caller will wait until a new {@link Deliverable} is available or an exception is thrown.
     * Note that calling this method subsequently guarantees that every time a different {@link Deliverable} is returned.
     * The {@link NetworkMessage} containing the requested {@link Deliverable} can be {@code null} if an exception is
     * thrown at a lower level, or if any connection error occurs.
     *
     * @return the {@link Deliverable} received from the remote counterpart.
     * @throws ConnectionException if any exception is thrown at a lower level.
     */
    public Deliverable nextDeliverable() throws ConnectionException {
        if (getMessage() == null)
            throw new ConnectionException("Connection to the client is lost");

        return (Deliverable) getNextMessage().getContent();
    }

    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (!(object instanceof VirtualClient))
            return false;
        return ((VirtualClient) object).getName().equals(this.name);
    }
}
