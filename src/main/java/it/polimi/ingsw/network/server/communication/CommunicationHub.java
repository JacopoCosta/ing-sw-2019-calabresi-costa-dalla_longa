package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.network.server.lobby.LobbyManager;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CommunicationHub {
    private static CommunicationHub instance;

    private final Queue<Player> players;
    private final LobbyManager lobbyManager;

    private final ScheduledExecutorService checkConnectionExecutor;
    private final ConnectionChecker checkConnectionTask;
    private final int CHECK_PERIOD = 5;

    private final Console console = Console.getInstance();

    private CommunicationHub() {
        players = new ConcurrentLinkedQueue<>();
        lobbyManager = new LobbyManager();

        checkConnectionTask = new ConnectionChecker(players, lobbyManager, instance);

        checkConnectionExecutor = Executors.newSingleThreadScheduledExecutor();
        checkConnectionExecutor.scheduleAtFixedRate(checkConnectionTask, 0, CHECK_PERIOD, TimeUnit.SECONDS);
    }

    public static CommunicationHub getInstance() {
        if (instance == null)
            instance = new CommunicationHub();
        return instance;
    }

    private Player getPlayerByName(String name) throws ClientNotRegisteredException {
        if (name == null)
            throw new NullPointerException("Client name is null");

        for (Player player : players)
            if (player.getName().equals(name))
                return player;
        throw new ClientNotRegisteredException("Client \"" + name + "\" not registered");
    }

    private void register(Player player) throws ClientAlreadyRegisteredException {
        if (player == null)
            throw new NullPointerException("Client is null");
        if (players.contains(player))
            throw new ClientAlreadyRegisteredException("Client \"" + player.getName() + "\" already registered");
        players.add(player);
    }

    void unregister(Player player) throws ClientNotRegisteredException {
        if (player == null)
            throw new NullPointerException("Player is null");
        if (!players.contains(player))
            throw new ClientNotRegisteredException("Client \"" + player.getName() + "\" not registered");
        players.remove(player);
    }

    public synchronized void handleMessage(NetworkMessage message) {
        console.mexC("Message " + message.getType().toString() + " received from Client \"" + message.getAuthor() + "\"");

        switch (message.getType()) {
            case REGISTER_REQUEST:
                handleRegistration(message);
                break;
            case UNREGISTER_REQUEST:
                handleUnregistering(message);
                break;
            case LOBBY_LIST_UPDATE_REQUEST:
                handleUpdateRequest(message);
                break;
            case LOBBY_CREATE_REQUEST:
                handleLobbyCreation(message);
                break;
            case LOBBY_LOGIN_REQUEST:
                handleLobbyLogin(message);
                break;
            case LOBBY_LOGOUT_REQUEST:
                handleLobbyLogout(message);
                break;
            case CLIENT_MESSAGE:
                notifyPlayer(message);
            default:
                console.err("Message " + message.getType() + " received from Client \"" + message.getAuthor() + "\": ignored");
        }
    }

    private void handleRegistration(NetworkMessage message) {
        Player player = (Player) message.getContent();

        console.log("registering Client \"" + player.getName() + "\"...");
        try {
            register(player);
            message = NetworkMessage.simpleServerMessage(MessageType.REGISTER_SUCCESS);
            console.log("Client \"" + player.getName() + "\" successfully registered");
        } catch (ClientAlreadyRegisteredException e) {
            message = NetworkMessage.simpleServerMessage(MessageType.CLIENT_ALREADY_REGISTERED_ERROR);
            console.err(e.getMessage());
        }

        try {
            player.sendMessage(message);
            console.mexS(("message " + message.getType().toString() + " sent to Client \"" + player.getName() + "\""));
        } catch (ConnectionException e) {
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleUnregistering(NetworkMessage message) {
        Player player;

        try {
            player = (getPlayerByName(message.getAuthor()));
        } catch (ClientNotRegisteredException e) {
            console.err(e.getMessage());
            return;
        }

        console.log("unregistering Client \"" + player.getName() + "\"...");
        try {
            unregister(player);
            message = NetworkMessage.simpleServerMessage(MessageType.UNREGISTER_SUCCESS);
            console.log("Client \"" + player.getName() + "\" successfully unregistered");
        } catch (ClientNotRegisteredException e) {
            message = NetworkMessage.simpleServerMessage(MessageType.CLIENT_NOT_REGISTERED_ERROR);
            console.err(e.getMessage());
        }

        try {
            player.sendMessage(message);
            console.mexS("message " + message.getType().toString() + " sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleUpdateRequest(NetworkMessage message) {
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            console.err(e.getMessage());
            return;
        }

        Map<String, String> lobbies = lobbyManager.getLobbiesStatus();
        message = NetworkMessage.completeServerMessage(MessageType.LOBBY_LIST_UPDATE_RESPONSE, lobbies);

        try {
            player.sendMessage(message);
            console.mexS("message " + message.getType() + " sent to client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleLobbyCreation(NetworkMessage message) {
        String[] lobbyInfo = ((String[]) (message.getContent()));
        String lobbyName = lobbyInfo[0];
        String lobbyPassword = lobbyInfo[1];
        Player player;

        try {
            player = (getPlayerByName(message.getAuthor()));
        } catch (ClientNotRegisteredException e) {
            console.err(e.getMessage());
            return;
        }

        try {
            lobbyManager.newLobby(lobbyName, lobbyPassword);
            console.log("Client \"" + message.getAuthor() + "\" created new Lobby \"" + lobbyName + "\" with password \"" + lobbyPassword + "\"");

            try {
                lobbyManager.add(lobbyName, player, lobbyPassword);
                console.log("Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");
                message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_CREATE_SUCCESS);
            } catch (LobbyNotFoundException e) {
                console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR);
            } catch (LobbyFullException e) {
                console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_FULL_ERROR);
            } catch (PlayerAlreadyAddedException e) {
                console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.PLAYER_ALREADY_ADDED_ERROR);
            } catch (InvalidPasswordException e) {
                console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.PASSWORD_NOT_VALID_ERROR);
            }
        } catch (LobbyAlreadyExistsException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_ALREADY_EXISTS_ERROR);
        }

        try {
            player.sendMessage(message);
            console.mexS("message " + message.getType() + " sent to client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleLobbyLogin(NetworkMessage message) {
        String[] lobbyInfo = ((String[]) (message.getContent()));
        String lobbyName = lobbyInfo[0];
        String lobbyPassword = lobbyInfo[1];
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            console.err(e.getMessage());
            return;
        }

        try {
            lobbyManager.add(lobbyName, player, lobbyPassword);
            console.log("Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_LOGIN_SUCCESS);
        } catch (LobbyNotFoundException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR);
        } catch (LobbyFullException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_FULL_ERROR);
        } catch (PlayerAlreadyAddedException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.PLAYER_ALREADY_ADDED_ERROR);
        } catch (InvalidPasswordException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.PASSWORD_NOT_VALID_ERROR);
        }

        try {
            player.sendMessage(message);
            console.mexS("message " + message.getType() + " sent to client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleLobbyLogout(NetworkMessage message) {
        String lobbyName = (String) message.getContent();
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            console.err(e.getMessage());
            return;
        }

        try {
            lobbyManager.remove(lobbyName, player);
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_LOGOUT_SUCCESS);
        } catch (LobbyNotFoundException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR);
        } catch (PlayerNotFoundException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.PLAYER_NOT_FOUND_ERROR);
        } catch (LobbyEmptyException e) {
            console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_EMPTY_ERROR);
        }

        try {
            player.sendMessage(message);
            console.mexS("message " + message.getType() + " sent to client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void notifyPlayer(NetworkMessage message) {
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            console.err(e.getMessage());
            return;
        }

        player.onMessageReceived(message);
        console.mexS("message " + message.getType() + " forwarded to Player \"" + player.getName() + "\"");
    }
}
