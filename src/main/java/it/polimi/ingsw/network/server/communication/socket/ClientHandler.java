package it.polimi.ingsw.network.server.communication.socket;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.network.server.communication.CommunicationHub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {
    private CommunicationHub communicationHub;

    private final Socket socket;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Console console;

    public ClientHandler(Socket socket) {
        communicationHub = CommunicationHub.getInstance();
        this.socket = socket;

        console = new Console();

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            //e.printStackTrace(); //never thrown before
            console.err(e.getClass() + ": " + e.getMessage());
            closeConnection();
        }
    }

    private void closeConnection() {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                //e.printStackTrace(); //never thrown before
                console.err(e.getClass() + ": " + e.getMessage());
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                //e.printStackTrace(); //never thrown before
                console.err(e.getClass() + ": " + e.getMessage());
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            //e.printStackTrace(); //never thrown before
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private NetworkMessage refactor(NetworkMessage message) {
        ClientCommunicationInterface clientInterface = new SocketClientCommunicationInterface(out);
        String playerName = message.getAuthor();
        Player player = new Player(playerName);
        player.setCommunicationInterface(clientInterface);

        return NetworkMessage.completeClientMessage(message.getAuthor(), message.getType(), player);
    }

    @Override
    public void run() {
        NetworkMessage message;
        try {
            do {
                message = (NetworkMessage) in.readObject();

                if (message.getType().equals(MessageType.REGISTER_REQUEST))
                    message = refactor(message);

                communicationHub.handleMessage(message);
            } while (!message.getType().equals(MessageType.UNREGISTER_REQUEST));
        } catch (SocketException ignored) {
            //Client unexpectedly quit: the ClientHandler.connectionChecker will unregister it
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace(); //never thrown before
            console.err(e.getClass() + ": " + e.getMessage());
        } finally {
            closeConnection();
        }
    }
}
