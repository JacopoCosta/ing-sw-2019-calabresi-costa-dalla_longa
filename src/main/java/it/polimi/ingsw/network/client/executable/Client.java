package it.polimi.ingsw.network.client.executable;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.view.remote.GraphicManager;

public class Client implements Runnable {
    private final String hostAddress;
    private final int port;
    private final CommunicationHandler.Interface communicationInterface;
    private final GraphicManager.Interface graphicalInterface;


    private final Console console;

    public Client(String hostAddress, int port, CommunicationHandler.Interface communicationInterface, GraphicManager.Interface graphicalInterface) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.communicationInterface = communicationInterface;
        this.graphicalInterface = graphicalInterface;

        console = Console.getInstance();
    }

    @Override
    public void run() {

        CommunicationHandler communicationHandler;
        try {
            communicationHandler = new CommunicationHandler(hostAddress, port, communicationInterface);
        } catch (ConnectionException e) {
            console.err("connection to the server is lost, cause: " + e.getMessage());
            System.exit(-1);
            return;
        }

        GraphicManager graphicManager = new GraphicManager(communicationHandler, graphicalInterface);
        graphicManager.draw();
    }
}
