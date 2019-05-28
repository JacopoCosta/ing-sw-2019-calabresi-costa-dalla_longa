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
    private final RMIController rmiController;
    private final ClientController clientController;

    private static Registry registry;

    public RMIServerCommunicationInterface(String hostAddress, int port) throws ConnectionException {
        try {
            registry = LocateRegistry.getRegistry(hostAddress, RMIController.DEFAULT_PORT);
            rmiController = (RMIController) registry.lookup("rmi://" + hostAddress + ":" + RMIController.DEFAULT_PORT + "/RMIController");

            clientController = new ClientController();
            RMIController controller = (RMIController) UnicastRemoteObject.exportObject(clientController, 0);

            registry.rebind("rmi://" + hostAddress + ":" + port + "/ClientController", controller);
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            throw new ConnectionException("RMI connection error to the server", e);
        }
    }

    private void closeConnection() throws ConnectionException {
        try {
            UnicastRemoteObject.unexportObject(clientController, false);
        } catch (NoSuchObjectException e) {
            throw new ConnectionException("RMI connection error to the server", e);
        }
    }

    @Override
    public void sendMessage(NetworkMessage message) throws ConnectionException {
        try {
            if (message.getType().equals(MessageType.REGISTER_REQUEST))
                message = NetworkMessage.completeClientMessage(message.getAuthor(), message.getType(), clientController);

            rmiController.notifyMessageReceived(message);
        } catch (RemoteException e) {
            throw new ConnectionException("RMI connection error to the server", e);
        }
    }

    public NetworkMessage nextMessage() throws ConnectionException {
        NetworkMessage message;

        do message = clientController.getMessage();
        while (message.getType().equals(MessageType.PING_MESSAGE));

        if (message.getType() == MessageType.UNREGISTER_SUCCESS)
            closeConnection();

        return message;
    }
}
