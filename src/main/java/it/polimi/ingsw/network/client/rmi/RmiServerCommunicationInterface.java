package it.polimi.ingsw.network.client.rmi;

import it.polimi.ingsw.network.client.ServerCommunicationInterface;
import it.polimi.ingsw.network.common.controller.RmiController;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;

import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiServerCommunicationInterface implements ServerCommunicationInterface {
    private final RmiController serverController;
    private final ClientController clientController;

    public RmiServerCommunicationInterface(String hostAddress, int port) throws ConnectionException {
        try {
            Registry registry = LocateRegistry.getRegistry(hostAddress);
            serverController = (RmiController) registry.lookup("rmi://" + hostAddress + ":" + port + "/ServerController");

            clientController = new ClientController();
            RmiController controller = (RmiController) UnicastRemoteObject.exportObject(clientController, 0);

            try {
                registry.bind("rmi://" + hostAddress + ":" + port + "/ClientController", controller);
            } catch (AlreadyBoundException ignored) {
                registry.rebind("rmi://" + hostAddress + ":" + port + "/ClientController", controller);
            }
        } catch (RemoteException | NotBoundException e) {
            throw new ConnectionException("RMI client connection error", e);
        }
    }

    private void closeConnection() throws ConnectionException {
        try {
            UnicastRemoteObject.unexportObject(clientController, false);
        } catch (NoSuchObjectException e) {
            throw new ConnectionException("RMI client connection error", e);
        }
    }

    private Message refactor(Message message) {
        Object[] clientInfo = {message.getContent(), clientController};
        return Message.completeMessage(message.getAuthor(), MessageType.REGISTER_REQUEST, clientInfo);
    }

    @Override
    public void sendMessage(Message message) throws ConnectionException {
        try {
            if (message.getType().equals(MessageType.REGISTER_REQUEST))
                message = refactor(message);

            serverController.notifyMessageReceived(message);
        } catch (RemoteException e) {
            throw new ConnectionException("RMI client connection error", e);
        }
    }

    public Message nextMessage() throws ConnectionException {
        Message message = clientController.getMessage();

        if (message.getType().equals(MessageType.UNREGISTER_REQUEST))
            closeConnection();

        return message;
    }
}
