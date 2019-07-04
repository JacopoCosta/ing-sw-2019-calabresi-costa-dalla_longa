package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.view.remote.cli.CLI;
import it.polimi.ingsw.view.remote.gui.GUI;

/**
 * A {@code GraphicsManager} is a decorator class of the {@link GraphicalInterface} interface. It allow the caller to
 * chose a display method by specifying the one he prefers with the usage of the {@link Interface} enum, without worrying
 * about the initializations needed to start the specific desired graphical process.
 */
public class GraphicsManager {
    /**
     * The available graphical interfaces to chose from.
     */
    public enum Interface {
        CLI_INTERFACE,
        GUI_INTERFACE
    }

    /**
     * The {@link GraphicalInterface} used to display the graphical content.
     */
    private GraphicalInterface graphicalInterface;

    /**
     * This is the only constructor. It creates a new {@code GraphicsManager} from the given {code communicationHandler},
     * used to interact with the remote counterpart, and the given {@code interfaceType}, to allow the proper graphical process
     * to be executed.
     *
     * @param communicationHandler the {@link CommunicationHandler} to interact with.
     * @param interfaceType        the desired graphical interface type.
     */
    public GraphicsManager(CommunicationHandler communicationHandler, Interface interfaceType) {
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

    /**
     * Starts the graphical process with the desired {@link Interface} type.
     */
    public void draw() {
        graphicalInterface.display();
    }
}
