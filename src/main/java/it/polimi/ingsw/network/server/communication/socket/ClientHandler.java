package it.polimi.ingsw.network.server.communication.socket;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.network.server.communication.CommunicationHub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private CommunicationHub communicationHub;

    private final Socket socket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        communicationHub = CommunicationHub.getInstance();
        this.socket = socket;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }

    private void closeConnection() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message refactor(Message message) {
        ClientCommunicationInterface clientInterface = new SocketClientCommunicationInterface(out);
        String playerName = (String) message.getContent();
        Player player = new Player(playerName);
        player.setCommunicationInterface(clientInterface);

        return Message.completeMessage(message.getAuthor(), MessageType.REGISTER_REQUEST, player);
    }

    @Override
    public void run() {
        try {
            Message message;
            do {
                message = (Message) in.readObject();

                if (message.getType().equals(MessageType.REGISTER_REQUEST))
                    message = refactor(message);

                communicationHub.handleMessage(message);
            } while (!message.getType().equals(MessageType.UNREGISTER_REQUEST));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
}
