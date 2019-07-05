package it.polimi.ingsw.network.client.executable;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.GraphicsManager;


/**
 * This class represent the {@code Client} application that is executed. Its purpose is to start the client application
 * with the correct configuration retrieved from the {@link ClientLauncher}.
 */

public class Client {
    /**
     * The server host address to connect to.
     */
    private final String hostAddress;

    /**
     * The server port to listen from.
     */
    private final int port;

    /**
     * The communication agent responsible for the interaction with the server side application.
     */
    private final CommunicationHandler.Interface communicationInterface;

    /**
     * The preferred graphical interface to start the client with.
     */
    private final GraphicsManager.Interface graphicalInterface;

    /**
     * This is the only constructor. It creates a new {@code Client} from the given information.
     *
     * @param hostAddress            the server ip address to connect to.
     * @param port                   the server port to listen from.
     * @param communicationInterface the communication agent to interact to.
     * @param graphicalInterface     the preferred graphical interface to start the client with.
     */
    public Client(String hostAddress, int port, CommunicationHandler.Interface communicationInterface, GraphicsManager.Interface graphicalInterface) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.communicationInterface = communicationInterface;
        this.graphicalInterface = graphicalInterface;
    }

    /**
     * Starts the client with the information needed and displays the first stage of the client interface.
     */
    public void start() {
        CommunicationHandler communicationHandler;
        try {
            communicationHandler = new CommunicationHandler(hostAddress, port, communicationInterface);
        } catch (ConnectionException e) {
            //ColorPrinter.err("connection to the server is lost, cause: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
            return;
        }

        GraphicsManager graphicsManager = new GraphicsManager(communicationHandler, graphicalInterface);
        graphicsManager.draw();
    }
}
