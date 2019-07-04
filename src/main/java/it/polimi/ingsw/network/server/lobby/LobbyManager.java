package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.util.console.Console;
import it.polimi.ingsw.network.common.util.property.GameProperty;
import it.polimi.ingsw.network.common.util.property.GamePropertyLoader;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * A wrapper for the {@link Lobby} class. It manages the creation, modification and deletion of all the {@link Lobby}.
 * A {@link Lobby} should only be created by instantiating a {@code LobbyManager} ad calling it's methods.
 * Different {@link Lobby} should be distinguishable throughout a unique value and should never be possible to have
 * more than one {@link Lobby} with the same distinctive value at the same time.
 *
 * @see Lobby
 */

public class LobbyManager {
    /**
     * The {@link Queue} containing all the {@link Lobby}.
     */
    private final Queue<Lobby> lobbies; //all Lobbies located on the Server

    /**
     * The properties used by all the {@code Lobby}.
     * These properties are loaded once from a configuration file by the {@link GamePropertyLoader} and will be the
     * same for all of the {@code Lobby}.
     */
    private GameProperty gameProperty;

    /**
     * This is the only constructor. It creates a {@code LobbyManager} to handle the {@link Lobby} lifecycle.
     */
    public LobbyManager() {
        this.lobbies = new ConcurrentLinkedQueue<>();

        GamePropertyLoader loader = new GamePropertyLoader();
        try {
            this.gameProperty = loader.readGameProperties();
        } catch (InvalidPropertyException e) {
            Console console = Console.getInstance();
            console.err(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Returns the {@link Lobby} corresponding to the given {@code name}, if present into the {@link #lobbies} list.
     *
     * @param name the name corresponding to the {@link Lobby} whose presence in {@link #lobbies} is to be tested.
     * @return the {@link Lobby} associated with the given {@code name}.
     * @throws LobbyNotFoundException if no {@link Lobby} can be found with the given {@code lobbyName}.
     */
    private Lobby getLobbyByName(String name) throws LobbyNotFoundException {
        for (Lobby lobby : this.lobbies)
            if (lobby.getName().equals(name))
                return lobby;
        throw new LobbyNotFoundException("Lobby \"" + name + "\" not found");
    }


    /**
     * Creates a new {@link Lobby} with name {@code lobbyName} and password {@code password}, only if does not exists any
     * other {@link Lobby} having the given {@code lobbyName}.
     *
     * @param lobbyName the new {@link Lobby} name.
     * @param password  the new {@link Lobby} password.
     * @throws LobbyAlreadyExistsException if another {@link Lobby} having the same {@code lobbyName} is already present
     *                                     into {@link #lobbies} list.
     */
    public synchronized void newLobby(String lobbyName, String password) throws LobbyAlreadyExistsException {
        if (lobbyName == null)
            throw new NullPointerException("Lobby name is null");

        try {
            this.getLobbyByName(lobbyName);
            throw new LobbyAlreadyExistsException("Lobby\"" + lobbyName + "\" already exists");
        } catch (LobbyNotFoundException ignored) {
        }

        Lobby lobby = new Lobby(lobbyName, password);
        lobby.setGameProperty(this.gameProperty);
        this.lobbies.add(lobby);
    }

    /**
     * Adds a given {@link Player} to an existing {@link Lobby} if the given {@code lobbyName} refers to a valid {@link Lobby}.
     *
     * @param lobbyName the name corresponding to the {@link Lobby} the {@link Player} wants to be added to.
     * @param player    the {@link Player} to be added to the chosen {@link Lobby}.
     * @param password  the password corresponding to the {@link Lobby} the {@link Player} wants to be added to.
     * @throws LobbyNotFoundException      if no {@link Lobby} can be found with the given {@code lobbyName}.
     * @throws LobbyFullException          if the {@link Lobby} corresponding to the given {@code lobbyName} is full.
     * @throws PlayerAlreadyAddedException if another instance of {@link Player} has been already added to the chosen {@link Lobby}.
     * @throws InvalidPasswordException    if the given {@code password} does not correspond to the password for the chosen {@link Lobby}.
     */
    public synchronized void add(String lobbyName, Player player, String password)
            throws LobbyNotFoundException, LobbyFullException, PlayerAlreadyAddedException, InvalidPasswordException, GameAlreadyStartedException {
        if (lobbyName == null)
            throw new NullPointerException("Lobby name is null");

        this.getLobbyByName(lobbyName).add(player, password);
    }

    /**
     * Removes the given {@link Player} from an existing {@link Lobby} if exists. Then deletes the given {@link Lobby}
     * if no other {@link Player}s are left into it.
     *
     * @param lobbyName the {@link Lobby} name from which the {@link Player} has to be removed.
     * @param player    the {@link Player} to remove from the chosen {@link Lobby}.
     * @throws LobbyNotFoundException  if no {@link Lobby} can be found with the given {@code lobbyName}.
     * @throws PlayerNotFoundException if the given {@link Player} can't be found into the chosen {@link Lobby}.
     * @throws LobbyEmptyException     if an attempt to remove a {@link Player} from an empty {@link Lobby} has been made.
     */
    public synchronized void remove(String lobbyName, Player player)
            throws LobbyNotFoundException, PlayerNotFoundException, LobbyEmptyException {
        if (lobbyName == null)
            throw new NullPointerException("Lobby name is null");

        if (player == null)
            throw new NullPointerException("Player is null");

        Lobby lobby = this.getLobbyByName(lobbyName);

        lobby.remove(player);
        if (lobby.getCurrentPlayers() == 0) {
            synchronized (this.lobbies) {
                this.lobbies.remove(lobby);
            }
        }
    }

    /**
     * Returns a {@link String} representing the name of the {@link Lobby} in which the given {@link Player} is logged.
     *
     * @param player the {@link Player} whom {@link Lobby} name has to be found.
     * @return the name of the {@link Lobby} containing the given {@link Player}.
     * @throws PlayerNotFoundException if the given {@link Player} can't be found into the chosen {@link Lobby}.
     */
    public synchronized String getLobbyNameByPlayer(Player player) throws PlayerNotFoundException {
        for (Lobby lobby : this.lobbies)
            if (lobby.contains(player))
                return lobby.getName();
        throw new PlayerNotFoundException("Player \"" + player.getName() + "\" not found in any Lobby");
    }

    /**
     * Returns a {@link Map} containing, for each {@link Lobby}, a {@link Map.Entry} representing the status
     * of each {@link Lobby}. A {@link Lobby} status is retrieved via {@link Lobby#getStatus()}.
     *
     * @return the status for each {@link Lobby}.
     * @see Lobby#getStatus()
     */
    public Map<String, String> getLobbiesStatus() {
        return this.lobbies
                .stream()
                .map(Lobby::getStatus)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Notifies the {@link Player}s in the {@link Lobby} corresponding to the given {@code lobbyName} about the update in his opponents list.
     *
     * @param lobbyName the {@link Lobby} whose {@link Player}s have to be notified.
     * @throws LobbyNotFoundException  if no {@link Lobby} containing the given {@code player }can be found.
     */
    public void notifyOpponentsUpdate(String lobbyName) throws LobbyNotFoundException {
        this.getLobbyByName(lobbyName).notifyOpponentUpdate();
    }
}