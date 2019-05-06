package it.polimi.ingsw.network.server.rmi;

import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.rmi.RmiController;
import it.polimi.ingsw.network.server.StreamReceiver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RmiControllerImpl extends UnicastRemoteObject implements RmiController {
    private StreamReceiver streamReceiver;

    public RmiControllerImpl() throws RemoteException {
        streamReceiver = StreamReceiver.getInstance();
    }

    @Override
    public void registerUser(String username) throws UserAlreadyAddedException {
        streamReceiver.registerUser(username);
    }

    @Override
    public void createNewLobby(String lobbyName, String lobbyPassword) throws LobbyAlreadyExistsException {
        streamReceiver.createNewLobby(lobbyName, lobbyPassword);
    }

    @Override
    public void loginUser(String lobbyName, String username, String lobbyPassword)
            throws LobbyNotFoundException, InvalidPasswordException, UserNotFoundException,
            LobbyFullException, UserAlreadyAddedException {
        streamReceiver.loginUser(lobbyName, username, lobbyPassword);
    }

    @Override
    public void logoutUser(String lobbyName, String username)
            throws LobbyNotFoundException, UserNotFoundException, EmptyLobbyException {
        streamReceiver.logoutUser(lobbyName, username);
    }

    @Override
    public void unregisterUser(String username) throws UserNotFoundException {
        streamReceiver.unregisterUser(username);
    }

    @Override
    public Map<String, String> requestUpdate() {
        return streamReceiver.requestUpdate();
    }
}

