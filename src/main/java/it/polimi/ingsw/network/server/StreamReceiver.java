package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.server.lobby.LobbyManager;
import it.polimi.ingsw.network.server.lobby.User;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/*
 * A StreamReceiver processes inputs from different communication protocols (Socket and RMI) and performs a standardized
 * request to the logical layer of the Server
 *
 * */

public class StreamReceiver {
    private static StreamReceiver instance;
    //list of all users connected to the Server
    private static Queue<User> users;
    private LobbyManager manager;

    private StreamReceiver() {
        users = new ConcurrentLinkedQueue<>();
        manager = new LobbyManager();
    }

    public static StreamReceiver getInstance() {
        if (instance == null)
            instance = new StreamReceiver();
        return instance;
    }

    private User getUserByName(String name) throws UserNotFoundException {
        for (User user : users)
            if (user.getName().equals(name))
                return user;
        throw new UserNotFoundException("User \"" + name + "\" not found into this Server");
    }

    public void registerUser(String username) throws UserAlreadyAddedException {
        try {
            getUserByName(username);
            throw new UserAlreadyAddedException("User \"" + username + "\" already registered into this Server");
        } catch (UserNotFoundException ignored) {
            users.add(new User(username));
        }
    }

    public void unregisterUser(String username) throws UserNotFoundException {
        users.remove(getUserByName(username));
    }

    public void createNewLobby(String lobbyName, String lobbyPassword)
            throws LobbyAlreadyExistsException {
        manager.newLobby(lobbyName, lobbyPassword);
    }

    public void loginUser(String lobbyName, String username, String lobbyPassword)
            throws LobbyNotFoundException, InvalidPasswordException, LobbyFullException, UserAlreadyAddedException, UserNotFoundException {
        manager.add(lobbyName, getUserByName(username), lobbyPassword);
    }

    public void logoutUser(String lobbyName, String username)
            throws LobbyNotFoundException, UserNotFoundException, EmptyLobbyException {
        manager.remove(lobbyName, getUserByName(username));
    }

    public Map<String, String> requestUpdate() {
        return manager.getLobbies();
    }

    public boolean isLobbyEmpty(String lobbyName) throws LobbyNotFoundException {
        return manager.isEmpty(lobbyName);
    }

    public boolean isLobbyFull(String lobbyName) throws LobbyNotFoundException {
        return manager.isFull(lobbyName);
    }

    public int getLobbyCurrentUsers(String lobbyName) throws LobbyNotFoundException {
        return manager.getCurrentUsers(lobbyName);
    }

    public int getLobbyMaxUsers(String lobbyName) throws LobbyNotFoundException {
        return manager.getMaxUsers(lobbyName);
    }

    public boolean existsLobby(String lobbyName) {
        return manager.exists(lobbyName);
    }
}
