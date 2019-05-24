package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;
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

    private final ScheduledExecutorService connectionChecker;
    private final Runnable checkConnectionTask;
    private final int CONNECTION_CHECK_PERIOD = 5;

    private CommunicationHub() {
        players = new ConcurrentLinkedQueue<>();
        lobbyManager = new LobbyManager();

        connectionChecker = Executors.newSingleThreadScheduledExecutor();
        checkConnectionTask = () -> {
            Message ping = Message.simpleMessage(null, MessageType.PING_MESSAGE);

            for (Player player : players)
                try {
                    player.sendMessage(ping);
                    System.out.println(("MESSAGE: string " + ping.getType().toString() + " sent to Client \"" + player.getName() + "\""));
                } catch (ConnectionException ignored) {
                    System.err.println("ERROR: Client \"" + player.getName() + "\" lost connection, unregistering...");
                    try {
                        unregister(player);
                    } catch (ClientNotRegisteredException e) {
                        //e.printStackTrace(); //never thrown before
                        System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
                    }
                    System.out.println("LOG: Client \"" + player.getName() + "\" successfully unregistered");
                }
        };
        connectionChecker.scheduleAtFixedRate(checkConnectionTask, 0, CONNECTION_CHECK_PERIOD, TimeUnit.SECONDS);
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

    private void unregister(Player player) throws ClientNotRegisteredException {
        if (player == null)
            throw new NullPointerException("Player is null");
        if (!players.contains(player))
            throw new ClientNotRegisteredException("Client \"" + player.getName() + "\" not registered");
        players.remove(player);
    }

    public synchronized void handleMessage(Message message) {
        System.out.println("LOG: Message " + message.getType().toString() + " received from Client \"" + message.getAuthor() + "\"");

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
                System.err.println("ERROR: Message " + message.getType() + " received from Client \"" + message.getAuthor() + "\": ignored");
        }
    }

    private void handleRegistration(Message message) {
        Player player = (Player) message.getContent();

        System.out.println("LOG: registering Client \"" + player.getName() + "\"...");
        try {
            register(player);
            message = Message.simpleMessage(null, MessageType.REGISTER_SUCCESS);
            System.out.println("LOG: Client \"" + player.getName() + "\" successfully registered");
        } catch (ClientAlreadyRegisteredException e) {
            message = Message.simpleMessage(null, MessageType.CLIENT_ALREADY_REGISTERED_ERROR);
            System.err.println("ERROR: " + e.getMessage());
        }

        try {
            player.sendMessage(message);
            System.out.println(("MESSAGE: string " + message.getType().toString() + " sent to Client \"" + player.getName() + "\""));
        } catch (ConnectionException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleUnregistering(Message message) {
        Player player;

        try {
            player = (getPlayerByName(message.getAuthor()));
        } catch (ClientNotRegisteredException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            return;
        }

        System.out.println("LOG: unregistering Client \"" + player.getName() + "\"...");
        try {
            unregister(player);
            message = Message.simpleMessage(null, MessageType.UNREGISTER_SUCCESS);
            System.out.println("LOG: Client \"" + player.getName() + "\" successfully unregistered");
        } catch (ClientNotRegisteredException e) {
            message = Message.simpleMessage(null, MessageType.CLIENT_NOT_REGISTERED_ERROR);
            System.err.println("ERROR: " + e.getMessage());
        }

        try {
            player.sendMessage(message);
            System.out.println(("MESSAGE: string " + message.getType().toString() + " sent to Client \"" + player.getName() + "\""));
        } catch (ConnectionException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleUpdateRequest(Message message) {
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            return;
        }

        Map<String, String> lobbies = lobbyManager.getLobbiesStatus();
        message = Message.completeMessage(null, MessageType.LOBBY_LIST_UPDATE_RESPONSE, lobbies);

        try {
            player.sendMessage(message);
            System.out.println("LOG: update sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleLobbyCreation(Message message) {
        String[] lobbyInfo = ((String[]) (message.getContent()));
        String lobbyName = lobbyInfo[0];
        String lobbyPassword = lobbyInfo[1];
        Player player;

        try {
            player = (getPlayerByName(message.getAuthor()));
        } catch (ClientNotRegisteredException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            return;
        }

        try {
            lobbyManager.newLobby(lobbyName, lobbyPassword);
            System.out.println("LOG: Client \"" + message.getAuthor() + "\" created new Lobby \"" + lobbyName + "\" with password \"" + lobbyPassword + "\"");

            try {
                lobbyManager.add(lobbyName, player, lobbyPassword);
                System.out.println("LOG: Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");
                message = Message.simpleMessage(null, MessageType.LOBBY_CREATE_SUCCESS);
            } catch (LobbyNotFoundException e) {
                System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
                message = Message.simpleMessage(null, MessageType.LOBBY_NOT_FOUND_ERROR);
            } catch (LobbyFullException e) {
                System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
                message = Message.simpleMessage(null, MessageType.LOBBY_FULL_ERROR);
            } catch (PlayerAlreadyAddedException e) {
                System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
                message = Message.simpleMessage(null, MessageType.PLAYER_ALREADY_ADDED_ERROR);
            } catch (InvalidPasswordException e) {
                System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
                message = Message.simpleMessage(null, MessageType.PASSWORD_NOT_VALID_ERROR);
            }
        } catch (LobbyAlreadyExistsException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.LOBBY_ALREADY_EXISTS_ERROR);
        }

        try {
            player.sendMessage(message);
        } catch (ConnectionException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleLobbyLogin(Message message) {
        String[] lobbyInfo = ((String[]) (message.getContent()));
        String lobbyName = lobbyInfo[0];
        String lobbyPassword = lobbyInfo[1];
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            return;
        }

        try {
            lobbyManager.add(lobbyName, player, lobbyPassword);
            System.out.println("LOG: Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");
            message = Message.simpleMessage(null, MessageType.LOBBY_LOGIN_SUCCESS);
        } catch (LobbyNotFoundException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.LOBBY_NOT_FOUND_ERROR);
        } catch (LobbyFullException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.LOBBY_FULL_ERROR);
        } catch (PlayerAlreadyAddedException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.PLAYER_ALREADY_ADDED_ERROR);
        } catch (InvalidPasswordException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.PASSWORD_NOT_VALID_ERROR);
        }

        try {
            player.sendMessage(message);
        } catch (ConnectionException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleLobbyLogout(Message message) {
        String lobbyName = (String) message.getContent();
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            return;
        }

        try {
            lobbyManager.remove(lobbyName, player);
            message = Message.simpleMessage(null, MessageType.LOBBY_LOGOUT_SUCCESS);
        } catch (LobbyNotFoundException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.LOBBY_NOT_FOUND_ERROR);
        } catch (PlayerNotFoundException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.PLAYER_NOT_FOUND_ERROR);
        } catch (LobbyEmptyException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            message = Message.simpleMessage(null, MessageType.LOBBY_EMPTY_ERROR);
        }

        try {
            player.sendMessage(message);
        } catch (ConnectionException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }

    private void notifyPlayer(Message message) {
        try {
            getPlayerByName(message.getAuthor()).onMessageReceived(message);
        } catch (ClientNotRegisteredException e) {
            //e.printStackTrace(); //never thrown before
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }
}
