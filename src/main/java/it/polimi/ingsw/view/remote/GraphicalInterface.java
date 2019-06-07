package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;

public interface GraphicalInterface {
    void setCommunicationHandler(CommunicationHandler communicationHandler);
    void display();
}
