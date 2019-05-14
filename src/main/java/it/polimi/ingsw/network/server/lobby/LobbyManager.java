package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class LobbyManager {
    private final Queue<Lobby> lobbies; //all Lobbies located on the Server

    public LobbyManager() {
        lobbies = new ConcurrentLinkedQueue<>();
    }

    //returns the Lobby corresponding to the given name, null otherwise
    private Lobby getLobbyByName(String name) throws LobbyNotFoundException {
        for (Lobby lobby : lobbies)
            if (lobby.getName().equals(name))
                return lobby;
        throw new LobbyNotFoundException("Lobby \"" + name + "\" not found");
    }


    //create a new Lobby
    public synchronized void newLobby(String lobbyName, String password) throws LobbyAlreadyExistsException {
        if (lobbyName == null)
            throw new NullPointerException("Lobby name is null");

        try {
            getLobbyByName(lobbyName);
            throw new LobbyAlreadyExistsException("Lobby\"" + lobbyName + "\" already exists");
        } catch (LobbyNotFoundException ignored) {
        }

        lobbies.add(new Lobby(lobbyName, password));
    }

    //add a Player to an existing Lobby
    public synchronized void add(String lobbyName, Player player, String password)
            throws LobbyNotFoundException, LobbyFullException, PlayerAlreadyAddedException, InvalidPasswordException {
        if (lobbyName == null)
            throw new NullPointerException("Lobby name is null");

        getLobbyByName(lobbyName).add(player, password);
    }

    //remove a Player from an existing Lobby
    public synchronized void remove(String lobbyName, Player player)
            throws LobbyNotFoundException, PlayerNotFoundException, LobbyEmptyException {
        if (lobbyName == null)
            throw new NullPointerException("Lobby name is null");

        if (player == null)
            throw new NullPointerException("Player is null");

        getLobbyByName(lobbyName).remove(player);
    }

    //returns all the Lobbies on the Server as <lobbyName, [number_of_users/MAX_USERS]>
    public Map<String, String> getLobbiesStatus() {
        return lobbies
                .stream()
                .map(Lobby::getLobbyStatus)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}