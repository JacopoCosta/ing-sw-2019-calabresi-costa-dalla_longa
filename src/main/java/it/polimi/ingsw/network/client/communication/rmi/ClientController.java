package it.polimi.ingsw.network.client.communication.rmi;

import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private final Queue<NetworkMessage> messages;

    /**
     * This is the only constructor. It creates a new {@code ClientController}.
     */
    ClientController() {
        messages = new ConcurrentLinkedQueue<>();
    }

    /**
     * This method is called when a new {@link NetworkMessage} has been received from the remote {@code Server}.
     *
     * @param message the new {@link NetworkMessage} sent to the remote {@code Server}.
     */
    @Override
    public void notifyMessageReceived(NetworkMessage message) {
        System.out.println("Received: " + message.getType());

        synchronized (messages) {
            messages.add(message);
        }
    }

    /**
     * Return the next available {@link NetworkMessage} to the caller. Note that this is a blocking call, meaning that
     * this method does not return until a new {@link NetworkMessage} different from has been received
     * from the remote {@code Server}.
     *
     * @return the next available {@link NetworkMessage}.
     */
    NetworkMessage nextMessage() {
        while (messages.size() <= 0)
            Thread.onSpinWait();

        synchronized (messages) {
            return messages.remove();
        }
    }
}
