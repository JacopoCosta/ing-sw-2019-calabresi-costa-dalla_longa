package it.polimi.ingsw.network.client.communication.rmi;

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
import java.rmi.server.UnicastRemoteObject;

public class RMIServerCommunicationInterface implements ServerCommunicationInterface {
    private final RMIController serverController;
    private final ClientController clientController;

    public RMIServerCommunicationInterface(String hostAddress, int port) throws ConnectionException {
        try {
            Registry remoteRegistry = LocateRegistry.getRegistry(hostAddress, RMIController.DEFAULT_PORT);
            serverController = (RMIController) remoteRegistry.lookup("rmi://" + hostAddress + ":" + RMIController.DEFAULT_PORT + "/RMIController");

            clientController = new ClientController();
            RMIController controller = (RMIController) UnicastRemoteObject.exportObject(clientController, 0);

            Registry localRegistry = LocateRegistry.createRegistry(port);
            localRegistry.rebind("rmi://" + "127.0.0.1" + ":" + port + "/ClientController", controller);
        } catch (RemoteException | NotBoundException e) {
            throw new ConnectionException(e);
        }
    }

    private void closeConnection() throws ConnectionException {
        try {
            UnicastRemoteObject.unexportObject(clientController, false);
        } catch (NoSuchObjectException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            if (message.getType().equals(MessageType.REGISTER_REQUEST))
                message = NetworkMessage.completeClientMessage(message.getAuthor(), message.getType(), clientController);

            serverController.notifyMessageReceived(message);
        } catch (RemoteException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public NetworkMessage nextMessage() throws ConnectionException {
        NetworkMessage message;

        do message = clientController.getMessage();
        while (message.getType().equals(MessageType.PING_MESSAGE));

        if (message.getType() == MessageType.UNREGISTER_SUCCESS)
            closeConnection();

        return message;
    }
}
