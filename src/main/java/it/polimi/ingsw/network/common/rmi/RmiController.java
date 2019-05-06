package it.polimi.ingsw.network.common.rmi;

import it.polimi.ingsw.network.common.exceptions.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface RmiController extends Remote {
    String REMOTE_REFERENCE_NAME = "RMI_REMOTE_REFERENCE_NAME";

    void registerUser(String username) throws RemoteException, UserAlreadyAddedException;

    void createNewLobby(String lobbyName, String lobbyPassword) throws RemoteException, LobbyAlreadyExistsException;

    void loginUser(String lobbyName, String username, String lobbyPassword)
            throws RemoteException, LobbyNotFoundException, InvalidPasswordException, UserNotFoundException,
            LobbyFullException, UserAlreadyAddedException;

    void logoutUser(String lobbyName, String username) throws RemoteException, LobbyNotFoundException,
            UserNotFoundException, EmptyLobbyException;

    void unregisterUser(String username) throws RemoteException, UserNotFoundException;

    Map<String, String> requestUpdate() throws RemoteException;
}
