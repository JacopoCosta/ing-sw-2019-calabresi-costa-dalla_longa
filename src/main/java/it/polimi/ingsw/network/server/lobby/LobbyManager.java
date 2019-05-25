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

        Lobby lobby = getLobbyByName(lobbyName);

        lobby.remove(player);
        if (lobby.getCurrentPlayers() == 0) {
            synchronized (lobbies) {
                lobbies.remove(lobby);
            }
        }
    }

    public synchronized String getLobbyNameByPlayer(Player player) throws PlayerNotFoundException {
        for (Lobby lobby : lobbies)
            if (lobby.contains(player))
                return lobby.getName();
        throw new PlayerNotFoundException("Player \"" + player.getName() + "\" not found in any Lobby");
    }

    //returns all the Lobbies on the Server as <lobbyName, [number_of_users/MAX_USERS]>
    public Map<String, String> getLobbiesStatus() {
        return lobbies
                .stream()
                .map(Lobby::getStatus)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}