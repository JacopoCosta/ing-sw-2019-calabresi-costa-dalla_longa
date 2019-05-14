package it.polimi.ingsw.network.server.communicationInterface;

import java.rmi.RemoteException;
import java.util.NoSuchElementException;

public class Client {
    private final String name;
    private final CommunicationInterface communicationInterface;

    public Client(String name, CommunicationInterface communicationInterface) {
        this.name = name;
        this.communicationInterface = communicationInterface;
    }

    public String getName() {
        return name;
    }

    public void sendMessage(String message) throws RemoteException, NoSuchElementException {
        communicationInterface.sendMessage(message);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Client))
            return false;
        return ((Client) obj).getName().equals(name);
    }
}
