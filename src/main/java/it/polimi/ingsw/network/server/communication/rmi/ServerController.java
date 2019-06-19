package it.polimi.ingsw.network.server.communication.rmi;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.network.server.communication.CommunicationHub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * A {@code ServerController} is a concrete implementation of the {@link RMIController} interface.
 * It waits for {@link NetworkMessage}s from an RMI client-side application and forwards the received message to a
 * {@link CommunicationHub} to handle it properly.
 *
 * @see RMIController
 */
public class ServerController extends UnicastRemoteObject implements RMIController {
    /**
     * The {@link CommunicationHub} instance to which messages will be forwarded.
     */
    private final CommunicationHub communicationHub;

    /**
     * This is the only constructor. It creates a new {@code ServerController} to starts listening for {@link NetworkMessage}s
     * from a remote client-side RMI interface.
     *
     * @throws RemoteException if any exception is thrown at a lower level.
     */
    public ServerController() throws RemoteException {
        this.communicationHub = CommunicationHub.getInstance();
    }

    /**
     * Notifies the extender class about the presence of a new {@link NetworkMessage} that needs to be forwarded to a
     * {@link CommunicationHub} to be handled properly.
     *
     * @param message the new {@link NetworkMessage} sent to the extender class.
     */
    @Override
    public void notifyMessageReceived(NetworkMessage message) {
        if (message.getType().equals(MessageType.REGISTER_REQUEST)) {
            //reformat the message to add the proper information before forwarding it to the communicationHub
            String playerName = message.getAuthor();
            RMIController clientController = (RMIController) message.getContent();

            ClientCommunicationInterface clientInterface = new RMIClientCommunicationInterface(clientController);
            Player player = new Player(playerName);
            player.setCommunicationInterface(clientInterface);

            message = NetworkMessage.completeClientMessage(message.getAuthor(), MessageType.REGISTER_REQUEST, player);
        }

        this.communicationHub.handleMessage(message);
    }
}
