package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.view.remote.cli.CLI;
import it.polimi.ingsw.view.remote.gui.GUI;

public class GraphicManager {
    public enum Interface {
        CLI_INTERFACE,
        GUI_INTERFACE
    }

    private final CommunicationHandler communicationHandler;
    private GraphicalInterface graphicalInterface;

    public GraphicManager(CommunicationHandler communicationHandler, Interface interfaceType) {
        this.communicationHandler = communicationHandler;

        switch (interfaceType) {
            case CLI_INTERFACE:
                graphicalInterface = new CLI();
                break;
            case GUI_INTERFACE:
                graphicalInterface = new GUI();
                break;
        }
        graphicalInterface.setCommunicationHandler(communicationHandler);
    }

    public void draw() {
        graphicalInterface.display();
    }
}
