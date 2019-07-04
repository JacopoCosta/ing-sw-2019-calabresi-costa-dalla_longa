package it.polimi.ingsw.network.client.communication.rmi;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.client.communication.ServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.rmi.RMIController;

import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

/**
 * An {@code RMIServerCommunicationInterface} offers a transparent way to send and receive {@link NetworkMessage}s via the
 * {@code RMI} protocol. To achieve such goal, this class implements the {@link ServerCommunicationInterface} interface.
 *
 * @see ServerCommunicationInterface
 */
public class RMIServerCommunicationInterface implements ServerCommunicationInterface {
    /**
     * The {@code Server} skeleton reference needed to deliver {@link NetworkMessage}s.
     */
    private final RMIController serverController;

    /**
     * The {@code Client} stub reference sent to the remote {@code Server} needed to receive {@link NetworkMessage}s.
     */
    private final ClientController clientController;

    /**
     * This is the only constructor. It creates a new {@code RMIServerCommunicationInterface} from the given arguments.
     *
     * @param hostAddress the remote {@code Server} ip address to connect to.
     * @param port        the remote {@code Server} port to listen from.
     * @throws ConnectionException if any lower level RMI exception is thrown by {@code Registry} or controllers.
     */
    public RMIServerCommunicationInterface(String hostAddress, int port) throws ConnectionException {
        try {
            Registry remoteRegistry = LocateRegistry.getRegistry(hostAddress, Registry.REGISTRY_PORT);
            this.serverController = (RMIController) remoteRegistry.lookup("rmi://" + hostAddress + ":" + Registry.REGISTRY_PORT + "/RMIController");

            this.clientController = new ClientController();

            Registry localRegistry;
            boolean registered = false;
            do {
                try {
                    RMIController controller = (RMIController) UnicastRemoteObject.exportObject(this.clientController, port);
                    localRegistry = LocateRegistry.createRegistry(port);
                    localRegistry.rebind("rmi://" + "127.0.0.1" + ":" + port + "/RMIController", controller);
                    registered = true;
                } catch (ExportException ignored) { //port is already in use: local server or other RMi client runs on "port"
                    if (port < CommunicationHandler.UPPERBOUND_PORT)
                        port++;
                    else
                        port = CommunicationHandler.LOWERBOUD_PORT;
                }
            } while (!registered);
        } catch (RemoteException | NotBoundException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Safely closes the RMI connection to the remote {@code Server}.
     *
     * @throws ConnectionException if any lower level RMI exception is thrown.
     */
    private void closeConnection() throws ConnectionException {
        try {
            UnicastRemoteObject.unexportObject(this.clientController, false);
        } catch (NoSuchObjectException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Sends a given {@link NetworkMessage} through the {@code Server} skeleton using the {@code RMI} protocol.
     *
     * @param message the {@link NetworkMessage} to be sent.
     * @throws ConnectionException if any lower level RMI exception is thrown by the {@code Server} skeleton.
     */
    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            if (message.getType().equals(MessageType.REGISTER_REQUEST))
                message = NetworkMessage.completeClientMessage(message.getAuthor(), message.getType(), this.clientController);

            this.serverController.notifyMessageReceived(message);
        } catch (RemoteException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Reads an incoming {@link NetworkMessage} from the {@code Client} stub using the {@code RMI} protocol.
     *
     * @return the incoming {@link NetworkMessage}.
     * @throws ConnectionException if any lower level RMI exception is thrown by the {@code Client} stub.
     */
    @Override
    public NetworkMessage nextMessage() throws ConnectionException {
        NetworkMessage message;

        do message = this.clientController.nextMessage();
        while (message.getType().equals(MessageType.PING_MESSAGE));

        if (message.getType().equals(MessageType.UNREGISTER_SUCCESS))
            this.closeConnection();

        return message;
    }
}
