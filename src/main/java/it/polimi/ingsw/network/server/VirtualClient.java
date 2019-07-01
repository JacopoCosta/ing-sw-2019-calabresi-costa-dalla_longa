package it.polimi.ingsw.network.server;

import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.MessageStatus;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.network.common.deliverable.Deliverable;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@code VirtualClient} represents the communication layer of a {@link Player}. The goal of this class is to
 * offer a communication {@code API} which is fully transparent to the {@link Player}, so that he can maintain all of
 * the logical functions separated from the network aspect, while being able to easily communicate with his remote
 * counterpart.
 *
 * @see Player
 */
public abstract class VirtualClient {
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
     * The status in which a {@link NetworkMessage} can be found. This flag is used to synchronize multiple
     * calls on the same {@code VirtualClient}'s {@link #nextMessage} attribute, that can be modified through a multi-thread
     * call to {@link #notifyReceived(NetworkMessage)} and {@link #nextMessage()} methods.
     */
    private volatile MessageStatus messageStatus;

    /**
     * The last {@link NetworkMessage} received from the remote-client counterpart.
     */
    private NetworkMessage nextMessage;

    /**
     * The {@code Lock} object used to synchronize access to {@link ClientCommunicationInterface} instance, so that
     * only a blocking write at a time can be performed.
     */
    private final Object messageSentLock;

    /**
     * The {@code Lock} object used to synchronize access to {@link #nextMessage} object, so that
     * only a blocking read at a time can be performed.
     */
    private final Object messageReceivedLock;

    /**
     * Whether or not the {@code VirtualClient} resulted to be connected to the {@code Server}.
     */
    private AtomicBoolean connected;

    /**
     * This is the only constructor. It creates a new {@code VirtualClient} with the given {@code name}.
     *
     * @param name The new {@code VirtualClient} name.
     */
    public VirtualClient(String name) {
        super();
        this.name = name;
        this.communicationInterface = null;
        this.messageSentLock = new Object();
        this.messageReceivedLock = new Object();

        this.messageStatus = MessageStatus.WAITING;
        this.connected = new AtomicBoolean(false); //need to call notifyConnected() manually to connect the VirtualClient
    }

    /**
     * Reports the {@code VirtualClient}'s connection status (online or offline).
     *
     * @return {@code true} if and only if the {@code VirtualClient} is online.
     */
    public boolean isConnected() {
        return this.connected.get();
    }

    /**
     * Sets the {@code VirtualClient} {@link ClientCommunicationInterface} with the given one. Any method call involving
     * sending and receiving any {@link NetworkMessage} performed before this method, will result in a thrown {@link NullPointerException}.
     *
     * @param communicationInterface the interface used to connect to the remote counterpart.
     */
    public void setCommunicationInterface(ClientCommunicationInterface communicationInterface) {
        synchronized (this.messageSentLock) {
            this.communicationInterface = communicationInterface;
        }
    }

    /**
     * Returns the {@code VirtualClient}'s current {@link ClientCommunicationInterface}.
     *
     * @return the {@code VirtualClient}'s current {@link ClientCommunicationInterface}.
     */
    public ClientCommunicationInterface getCommunicationInterface() {
        synchronized (this.messageSentLock) {
            return this.communicationInterface;
        }
    }

    /**
     * Returns the current {@code name} of this {@code VirtualClient}.
     *
     * @return the {@code VirtualClient} name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Forwards a {@link NetworkMessage} to the lower level trough a {@link ClientCommunicationInterface}.
     *
     * @param message the {@link NetworkMessage} to be forwarded.
     * @throws ConnectionException if any exception is thrown at a lower level.
     */
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        synchronized (this.messageSentLock) {
            if (this.communicationInterface == null)
                throw new ConnectionException("ClientCommunicationInterface is null");

            this.communicationInterface.sendMessage(message);
        }
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
     * Returns to the caller a {@link NetworkMessage} received from the {@code VirtualClient} remote counterpart.
     * This is a blocking call, meaning the caller will wait until a new {@link NetworkMessage} is available or an exception is thrown.
     * Note that calling this method subsequently guarantees that every time a different {@link NetworkMessage} is returned.
     *
     * @return the {@link NetworkMessage} received from the remote counterpart.
     * @throws ConnectionException if the {@link NetworkMessage} remote sender encounters a network issue at any lower level.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    private NetworkMessage nextMessage() throws ConnectionException {

        while (this.messageStatus.equals(MessageStatus.WAITING));

        synchronized (this.messageReceivedLock) {
            if (this.messageStatus.equals(MessageStatus.UNAVAILABLE))
                throw new ConnectionException("Client disconnected");

            this.messageStatus = MessageStatus.WAITING;
            return this.nextMessage;
        }
    }

    /**
     * Returns to the caller a {@link Deliverable} received from the {@code VirtualClient} remote counterpart.
     * This is a blocking call, meaning the caller will wait until a new {@link Deliverable} is available or an exception is thrown.
     * Note that calling this method subsequently guarantees that every time a different {@link Deliverable} is returned.
     *
     * @return the {@link Deliverable} received from the remote counterpart.
     * @throws ConnectionException if any exception is thrown at a lower level.
     */
    public Deliverable nextDeliverable() throws ConnectionException {
        return (Deliverable) nextMessage().getContent();
    }

    /**
     * Notifies the {@code VirtualClient} that a new {@link NetworkMessage} has been received from its remote counterpart.
     * The received {@link NetworkMessage} is stored in the local object {@link #nextMessage} and the appropriated flag is
     * set in order to end any blocking call to the {@link #nextMessage()} method.
     *
     * @param message the new {@link NetworkMessage} received.
     */
    public void notifyReceived(NetworkMessage message) {
        synchronized (this.messageReceivedLock) {
            this.nextMessage = message;
            this.messageStatus = MessageStatus.AVAILABLE;
        }
    }

    /**
     * Notifies the {@code VirtualClient} that he is now connected to the server and can officially start sending and/or
     * receiving {@link NetworkMessage}s from his remote client counterpart.
     */
    public void notifyConnected() {
        this.connected.set(true);
    }

    /**
     * Notifies the {@code VirtualClient} that he is no longer logged into the server and set the {@link MessageStatus}
     * accordingly. This is done to end any wait action by other {@code Thread}s listening on the {@link #nextMessage()}
     * method.
     */
    public void notifyDisconnected() {
        synchronized (this.messageReceivedLock) {
            this.messageStatus = MessageStatus.UNAVAILABLE;
            this.connected.set(false);
        }
    }

    /**
     * Determines whether or not two {@code VirtualClient}s are equals. This is {@code true} if and oly if thy both
     * share the same {@link #name}, such that {@code equals()} method called between the two {@link #name} attributes
     * returns {@code true}.
     *
     * @param object the {@code VirtualClient} object which with to compare.
     * @return {@code true} if and oly if the two {@code VirtualClient}'s {@link #name}s are {@code equals()}, {@code false otherwise.
     * @see String#equals(Object)
     */
    @Override
    public boolean equals(Object object) {
        if (object == null)
            return false;
        if (!(object instanceof VirtualClient))
            return false;
        return ((VirtualClient) object).getName().equals(this.name);
    }
}
