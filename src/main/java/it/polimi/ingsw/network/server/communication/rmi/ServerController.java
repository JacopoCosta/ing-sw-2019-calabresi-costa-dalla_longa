package it.polimi.ingsw.network.server.communication.rmi;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.controller.RmiController;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.network.server.communication.CommunicationHub;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerController extends UnicastRemoteObject implements RmiController {
    private final CommunicationHub communicationHub;

    public ServerController() throws RemoteException {
        communicationHub = CommunicationHub.getInstance();
    }

    private Message refactor(Message message) {
        String playerName = (String) (((Object[]) message.getContent())[0]);
        RmiController clientController = (RmiController) (((Object[]) message.getContent())[1]);

        ClientCommunicationInterface clientInterface = new RmiClientCommunicationInterface(clientController);
        Player player = new Player(playerName);
        player.setCommunicationInterface(clientInterface);

        return Message.completeMessage(message.getAuthor(), MessageType.REGISTER_REQUEST, player);
    }

    /*
     * messages received from the Client
     * */
    @Override
    public void notifyMessageReceived(Message message) {
        if (message.getType().equals(MessageType.REGISTER_REQUEST))
            message = refactor(message);

        communicationHub.handleMessage(message);
    }
}
