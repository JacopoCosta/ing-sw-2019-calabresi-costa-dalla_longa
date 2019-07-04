package it.polimi.ingsw.network.client.communication.rmi;

import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;

/**
 * A {@code ClientController} is responsible for the message reception from an RMI remote server. It continue waiting for
 * the next {@link NetworkMessage} until a new one has been received.
 * By calling the {@link #nextMessage()} method is guaranteed that the {@link NetworkMessage} returned is different
 * from the previous one (if any). To allow the remote {@code Server} to notify for incoming messages, this class
 * implements the {@link RMIController} interface.
 *
 * @see RMIController
 */
public class ClientController implements RMIController {
    /**
     * The last {@link NetworkMessage} received from the remote {@code Server}.
     */
    private NetworkMessage message;

    /**
     * Whether or not the {@link #message} attribute refers to a new message received or still contains an old one.
     */
    private volatile boolean receivedNew;

    /**
     * This is the only constructor. It creates a new {@code ClientController}.
     */
    ClientController() {
        this.message = null;
        this.receivedNew = false;
    }

    /*
     *
     * */

    /**
     * This method is called when a new {@link NetworkMessage} has been received from the remote {@code Server}.
     *
     * @param message the new {@link NetworkMessage} sent to the remote {@code Server}.
     */
    @Override
    public synchronized void notifyMessageReceived(NetworkMessage message) {
        this.message = message;
        this.receivedNew = true;
    }

    /**
     * Return the next available {@link NetworkMessage} to the caller. Note that this is a blocking call, meaning that
     * this method does not return until a new {@link NetworkMessage} different from {@link #message} has been received
     * from the remote {@code Server}.
     *
     * @return the next available {@link NetworkMessage}.
     */
    synchronized NetworkMessage nextMessage() {
        while (!this.receivedNew)
            Thread.onSpinWait();

        this.receivedNew = false;
        return this.message;
    }
}
