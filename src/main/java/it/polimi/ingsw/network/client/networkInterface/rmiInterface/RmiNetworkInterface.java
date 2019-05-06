package it.polimi.ingsw.network.client.networkInterface.rmiInterface;

import it.polimi.ingsw.network.client.networkInterface.NetworkInterface;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.rmi.RmiController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

/*
 * A RmiNetworkInterface communicates to the Server via the RMI protocol
 *
 * */

public class RmiNetworkInterface implements NetworkInterface {
    private final RmiController controller;

    public RmiNetworkInterface(String ipAddress) throws NetworkInterfaceConfigurationException {
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(ipAddress);
        } catch (RemoteException e) {
            throw new NetworkInterfaceConfigurationException(e);
        }
        try {
            controller = (RmiController) registry.lookup(RmiController.REMOTE_REFERENCE_NAME);
        } catch (RemoteException | NotBoundException e) {
            throw new NetworkInterfaceConfigurationException(e);
        }
    }

    @Override
    public void register(String username) throws UserAlreadyAddedException, ConnectionLostException {
        try {
            controller.registerUser(username);
        } catch (RemoteException e) {
            throw new ConnectionLostException(e);
        }
    }

    @Override
    public void newLobby(String name, String password) throws LobbyAlreadyExistsException, ConnectionLostException {
        try {
            controller.createNewLobby(name, password);
        } catch (RemoteException e) {
            throw new ConnectionLostException(e);
        }
    }

    @Override
    public void login(String lobbyName, String username, String password) throws LobbyNotFoundException,
            InvalidPasswordException, LobbyFullException, UserAlreadyAddedException, UserNotFoundException,
            ConnectionLostException {
        try {
            controller.loginUser(lobbyName, username, password);
        } catch (RemoteException e) {
            throw new ConnectionLostException(e);
        }
    }

    @Override
    public void logout(String lobbyName, String username) throws LobbyNotFoundException,
            UserNotFoundException, EmptyLobbyException, ConnectionLostException {
        try {
            controller.logoutUser(lobbyName, username);
        } catch (RemoteException e) {
            throw new ConnectionLostException(e);
        }
    }

    @Override
    public void unregister(String username) throws UserNotFoundException, ConnectionLostException {
        try {
            controller.unregisterUser(username);
        } catch (RemoteException e) {
            throw new ConnectionLostException(e);
        }
    }

    @Override
    public Map<String, String> getLobbies() throws ConnectionLostException {
        try {
            return controller.requestUpdate();
        } catch (RemoteException e) {
            throw new ConnectionLostException(e);
        }
    }
}
