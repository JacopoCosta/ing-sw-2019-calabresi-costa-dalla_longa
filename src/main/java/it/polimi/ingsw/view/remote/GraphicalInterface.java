package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;

/**
 * A {@code GraphicalInterface} is an interface used to abstract the user interface layer from the way it is displayed.
 * This also serves the purpose to offer a unified way to display the graphics independently by its specifications
 * or the subsystem it will run on.
 */
public interface GraphicalInterface {
    /**
     * Sets the given {@link CommunicationHandler} to the current {@code GraphicalInterface}, to allow the interaction
     * with the remote counterpart.
     *
     * @param communicationHandler the new {@link CommunicationHandler} of this graphical interface.
     */
    void setCommunicationHandler(CommunicationHandler communicationHandler);

    /**
     * Starts the graphical application. This method should call the start method of the implemented graphical interface
     * and start the display routine.
     */
    void display();
}
