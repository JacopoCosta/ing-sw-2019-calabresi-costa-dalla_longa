package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.client.communicationInterface.CommunicationInterface;
import it.polimi.ingsw.network.client.communicationInterface.rmi.RmiCommunicationInterface;
import it.polimi.ingsw.network.client.communicationInterface.socket.SocketCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.common.exceptions.ConfigurationException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

class CommunicationHandler {

    private final CommunicationInterface communicationInterface;

    public enum Interface {
        SOCKET_INTERFACE,
        RMI_INTERFACE
    }

    CommunicationHandler(String hostAddress, int port, Interface interfaceType) throws ConfigurationException {
        switch (interfaceType) {
            case SOCKET_INTERFACE:
                try {
                    this.communicationInterface = new SocketCommunicationInterface(hostAddress, port);
                } catch (IOException | NoSuchElementException e) {
                    throw new ConfigurationException("SocketCommunicationInterface configuration failed", e);
                }
                break;
            case RMI_INTERFACE:
                try {
                    this.communicationInterface = new RmiCommunicationInterface(hostAddress, port);
                } catch (RemoteException | NotBoundException e) {
                    throw new ConfigurationException("RmiCommunicationInterface configuration failed", e);
                }
                break;
            default:
                throw new ConfigurationException("Interface must be of type SOCKET_INTERFACE or RMI_INTERFACE");
        }
    }

    void register(String username) throws ConnectionException, ClientAlreadyRegisteredException {
        try {
            communicationInterface.register(username);
        } catch (RemoteException | NoSuchElementException e) {
            throw new ConnectionException("connection error", e);
        }
    }

    void unregister(String username) throws ConnectionException, ClientNotFoundException {
        try {
            communicationInterface.unregister(username);
        } catch (RemoteException | NoSuchElementException e) {
            throw new ConnectionException("connection error", e);
        }
    }
}
