package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.network.common.exceptions.*;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class LobbyManager {
    private Queue<Lobby> lobbies; //all Lobbies located on the Server

    public LobbyManager() {
        lobbies = new ConcurrentLinkedQueue<>();
    }

    //returns the Lobby corresponding to the given name, null otherwise
    private Lobby getLobbyByName(String name) throws LobbyNotFoundException {
        for (Lobby lobby : lobbies)
            if (lobby.getName().equals(name))
                return lobby;
        throw new LobbyNotFoundException("Lobby \"" + name + "\" not found into lobbies list");
    }

    //create a new Lobby
    public void newLobby(String lobbyName, String lobbyPassword) throws LobbyAlreadyExistsException {
        if (exists(lobbyName))
            throw new LobbyAlreadyExistsException("Lobby \"" + lobbyName + "\" already exists into lobbies list");

        Lobby lobby = new Lobby(lobbyName, lobbyPassword);
        lobbies.add(lobby);
    }

    //add a User to an existing Lobby
    public void add(String lobbyName, User user, String lobbyPassword)
            throws LobbyNotFoundException, LobbyFullException, InvalidPasswordException, UserAlreadyAddedException {
        getLobbyByName(lobbyName).addUser(user, lobbyPassword);
    }

    //remove a User from an existing Lobby
    public void remove(String lobbyName, User user)
            throws LobbyNotFoundException, UserNotFoundException, EmptyLobbyException {
        Lobby lobby = getLobbyByName(lobbyName);
        lobby.removeUser(user);

        //if empty, the Lobby is removed from the global Lobbies list
        if (lobby.isEmpty())
            lobbies.remove(lobby);
    }

    //whether or not the given Lobby is empty
    public boolean isEmpty(String lobbyName) throws LobbyNotFoundException {
        return getLobbyByName(lobbyName).isEmpty();
    }

    //whether or not the given Lobby is full
    public boolean isFull(String lobbyName) throws LobbyNotFoundException {
        return getLobbyByName(lobbyName).isFull();
    }

    //returns the number of Users currently present into the given Lobby
    public int getCurrentUsers(String lobbyName) throws LobbyNotFoundException {
        return getLobbyByName(lobbyName).getCurrentUsers();
    }

    //returns the maximum number of Users that can be added into the given Lobby
    public int getMaxUsers(String lobbyName) throws LobbyNotFoundException {
        return getLobbyByName(lobbyName).getMaxUses();
    }

    //returns all the Lobbies on the Server as <lobbyName, [number_of_users/LOBBY_SIZE]>
    public Map<String, String> getLobbies() {
        return lobbies.stream().collect(Collectors.toMap(Lobby::getName,
                l -> "[" + l.getCurrentUsers() + "/" + l.getMaxUses() + "]"));
    }

    //whether or not lobbyName refers to an existing Lobby
    public boolean exists(String lobbyName) {
        try {
            getLobbyByName(lobbyName);
            return true;
        } catch (LobbyNotFoundException ignored) {
        }
        return false;
    }
}