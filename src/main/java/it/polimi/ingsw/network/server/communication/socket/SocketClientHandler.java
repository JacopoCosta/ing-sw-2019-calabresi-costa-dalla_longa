package it.polimi.ingsw.network.server.communication.socket;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.server.communication.ClientCommunicationInterface;
import it.polimi.ingsw.network.server.communication.CommunicationHub;
import it.polimi.ingsw.util.printer.ColorPrinter;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

/**
 * A {@code SocketClientHandler} serves the purpose of interfacing with a specific client-side application through a
 * {@code Socket} communication protocol.
 * Virtually a {@code SocketClientHandler} intercepts all the {@link NetworkMessage}s sent from the client-side application and
 * forwards them to the {@link CommunicationHub}; they are then interpreted decisions can be made according to the
 * message content.
 *
 * <p>To achieve this goal a bi-directional channel is needed in order to make
 * the communication possible.
 * At the beginning of the communication process an {@link ObjectOutputStream} is created to send {@link NetworkMessage}
 * to the remote client-side application.
 * An {@link ObjectInputStream} is then created to allow messages to be received.
 *
 * <p>Before the communication can end, a call to {@link #closeConnection()} must be performed in order to safely interrupt
 * the connection and notify the client-side application in the proper way.
 */
public class SocketClientHandler implements Runnable {
    /**
     * The {@link CommunicationHub} instance to which messages will be forwarded.
     */
    private CommunicationHub communicationHub;

    /**
     * The {@link Socket} reference used to instantiate the bi-directional communication to the client-side application.
     */
    private final Socket socket;

    /**
     * The output channel used to send {@link NetworkMessage} to the client-side application.
     */
    private ObjectOutputStream out;
    /**
     * The input channel used to receive {@link NetworkMessage} from the client-side application.
     */
    private ObjectInputStream in;

    /**
     * This is the only constructor used to create a new {@code SocketClientHandler} with the given {@code socket} argument.
     * This is used to create the input and output channels to instantiate a bi-directional communication to the
     * client-side application.
     *
     * @param socket the {@link Socket} object needed to instantiate the bi-directional communication.
     */
    public SocketClientHandler(Socket socket) {
        this.communicationHub = CommunicationHub.getInstance();
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
            this.closeConnection();
        }
    }

    /**
     * Safely closes all the opened connections.
     * This method should be called only when the communication is terminated and no more {@link NetworkMessage} are desired
     * to be sent and/or received, or if an unexpected behavior has occurred and a safe way to truncate the communication
     * to the client-side application is needed.
     */
    private void closeConnection() {
        try {
            this.in.close();
        } catch (IOException ignored) {
        } finally {
            try {
                this.out.close();
            } catch (IOException ignored) {
            } finally {
                try {
                    this.socket.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * Executes the {@code SocketClientHandler} core logic: forwards every {@link NetworkMessage} received from the client-side
     * application to the {@link CommunicationHub} for proper handling until an {@link MessageType#UNREGISTER_REQUEST}
     * type of {@link NetworkMessage} is received or if any exception is throw at a lower level.
     * Finally a {@link #closeConnection()} call is performed to safely close the communication before terminating.
     */
    @Override
    public void run() {
        NetworkMessage message;
        try {
            do {
                message = (NetworkMessage) this.in.readObject();

                if (message.getType().equals(MessageType.REGISTER_REQUEST)) {
                    //reformat the message to add the proper information before forwarding it to the communicationHub
                    ClientCommunicationInterface clientInterface = new SocketClientCommunicationInterface(this.out);
                    String playerName = message.getAuthor();

                    ColorPrinter.log("Client \"" + playerName + "\" connected via Socket protocol");

                    Player player = new Player(playerName);
                    player.setCommunicationInterface(clientInterface);

                    message = NetworkMessage.completeClientMessage(message.getAuthor(), message.getType(), player);
                }

                this.communicationHub.handleMessage(message);
            } while (!message.getType().equals(MessageType.UNREGISTER_REQUEST));
        } catch (SocketException | EOFException e) {
            //Client unexpectedly quit: the CommunicationHub will unregister it
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //ColorPrinter.err(e.getClass() + ": " + e.getMessage());
        } finally {
            this.closeConnection();
        }
    }
}
