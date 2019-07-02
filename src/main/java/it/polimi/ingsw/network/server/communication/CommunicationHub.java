package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.network.server.lobby.LobbyManager;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("FieldCanBeLocal")
public class CommunicationHub {
    private static CommunicationHub instance;

    private final Queue<Player> players;
    private final LobbyManager lobbyManager;

    private final ScheduledExecutorService checkConnectionExecutor;
    private final Runnable connectionCheckTask;
    private final int CHECK_PERIOD = 5;

    private final Console console = Console.getInstance();

    private CommunicationHub() {
        this.players = new ConcurrentLinkedQueue<>();
        this.lobbyManager = new LobbyManager();

        this.connectionCheckTask = () -> {
            NetworkMessage ping = NetworkMessage.simpleServerMessage(MessageType.PING_MESSAGE);
            for (Player player : this.players) {
                if (player.isConnected()) {
                    try {
                        this.console.mexS(("message " + ping.getType().toString() + " sent to Client \"" + player.getName() + "\""));
                        player.sendMessage(ping);
                    } catch (ConnectionException ignored) {
                        this.console.err("Client \"" + player.getName() + "\" lost connection, logging out from his lobby...");
                        try {
                            try {
                                String lobbyName = this.lobbyManager.getLobbyNameByPlayer(player);
                                this.lobbyManager.remove(lobbyName, player);
                                this.console.log("Client \"" + player.getName() + "\" successfully logged out from Lobby \"" + lobbyName + "\"");
                            } catch (LobbyNotFoundException e) {
                                this.console.log(e.getMessage());
                            } catch (PlayerNotFoundException | LobbyEmptyException e) {
                                e.printStackTrace();
                                //this.console.err(e.getMessage());
                            }
                            this.console.log("unregistering Client \"" + player.getName() + "\"...");
                            unregister(player);

                        } catch (ClientNotRegisteredException e) {
                            e.printStackTrace();
                            //this.console.err(e.getMessage());
                        }
                        this.console.log("Client \"" + player.getName() + "\" successfully unregistered");
                    }
                }
            }
        };

        this.checkConnectionExecutor = Executors.newSingleThreadScheduledExecutor();
        this.checkConnectionExecutor.scheduleAtFixedRate(this.connectionCheckTask, 0, this.CHECK_PERIOD, TimeUnit.SECONDS);
    }

    public static CommunicationHub getInstance() {
        if (instance == null)
            instance = new CommunicationHub();
        return instance;
    }

    private Player getPlayerByName(String name) throws ClientNotRegisteredException {
        if (name == null)
            throw new NullPointerException("Client name is null");

        for (Player player : this.players)
            if (player.getName().equals(name))
                return player;
        throw new ClientNotRegisteredException("Client \"" + name + "\" not registered");
    }

    //returns true if the player was already found into the server and is has simply rejoined, false if the player is a new one
    private boolean register(Player player) throws ClientAlreadyRegisteredException {
        if (player == null)
            throw new NullPointerException("Client is null");

        Player dormantPlayer;
        try {
            dormantPlayer = getPlayerByName(player.getName());
        } catch (ClientNotRegisteredException ignored) {
            //this is a new Player with a unique name
            this.players.add(player);
            player.notifyConnected();
            return false;
        }

        if (dormantPlayer.isConnected())
            //this is a new Player with the same name of another one
            throw new ClientAlreadyRegisteredException("Client \"" + player.getName() + "\" already registered");

        //this Player lost connection and is trying to reconnect, we need to manually update its status.
        dormantPlayer.setCommunicationInterface(player.getCommunicationInterface());
        dormantPlayer.notifyConnected();
        return true;
    }

    private void unregister(Player player) throws ClientNotRegisteredException {
        if (player == null)
            throw new NullPointerException("Player is null");
        if (!this.players.contains(player))
            throw new ClientNotRegisteredException("Client \"" + player.getName() + "\" not registered");
        this.players.remove(player);
    }

    public synchronized void handleMessage(NetworkMessage message) {
        this.console.mexC("Message " + message.getType().toString() + " received from Client \"" + message.getAuthor() + "\"");

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
                break;
            default:
                this.console.err("Message " + message.getType() + " received from Client \"" + message.getAuthor() + "\": ignored");
        }
    }

    private void handleRegistration(NetworkMessage message) {
        Player player = (Player) message.getContent();

        this.console.log("registering Client \"" + player.getName() + "\"...");
        try {
            if (!register(player))
                this.console.log("Client \"" + player.getName() + "\" successfully registered");
            else
                this.console.log("Client \"" + player.getName() + "\" already found in server, rejoin successful");
            message = NetworkMessage.simpleServerMessage(MessageType.REGISTER_SUCCESS);
        } catch (ClientAlreadyRegisteredException e) {
            message = NetworkMessage.simpleServerMessage(MessageType.CLIENT_ALREADY_REGISTERED_ERROR);
            this.console.err(e.getMessage());
        }

        try {
            player.sendMessage(message);
            this.console.mexS(("message " + message.getType().toString() + " sent to Client \"" + player.getName() + "\""));
        } catch (ConnectionException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleUnregistering(NetworkMessage message) {
        Player player;

        try {
            player = (getPlayerByName(message.getAuthor()));
        } catch (ClientNotRegisteredException e) {
            e.printStackTrace();
            //this.console.err(e.getMessage());
            return;
        }

        this.console.log("unregistering Client \"" + player.getName() + "\"...");
        try {
            unregister(player);
            message = NetworkMessage.simpleServerMessage(MessageType.UNREGISTER_SUCCESS);
            this.console.log("Client \"" + player.getName() + "\" successfully unregistered");
        } catch (ClientNotRegisteredException e) {
            message = NetworkMessage.simpleServerMessage(MessageType.CLIENT_NOT_REGISTERED_ERROR);
            this.console.err(e.getMessage());
        }

        try {
            player.sendMessage(message);
            this.console.mexS("message " + message.getType().toString() + " sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
        }
    }

    private void handleUpdateRequest(NetworkMessage message) {
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            this.console.err(e.getMessage());
            return;
        }

        Map<String, String> lobbies = this.lobbyManager.getLobbiesStatus();
        message = NetworkMessage.completeServerMessage(MessageType.LOBBY_LIST_UPDATE_RESPONSE, lobbies);

        try {
            player.sendMessage(message);
            this.console.mexS("message " + message.getType() + " sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
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
            this.console.err(e.getMessage());
            return;
        }

        try {
            this.lobbyManager.newLobby(lobbyName, lobbyPassword);
            this.console.log("Client \"" + message.getAuthor() + "\" created new Lobby \"" + lobbyName + "\" with password \"" + lobbyPassword + "\"");

            try {
                this.lobbyManager.add(lobbyName, player, lobbyPassword);
                this.console.log("Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");
                message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_CREATE_SUCCESS);
            } catch (LobbyNotFoundException e) {
                this.console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR);
            } catch (LobbyFullException e) {
                this.console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_FULL_ERROR);
            } catch (PlayerAlreadyAddedException e) {
                this.console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.PLAYER_ALREADY_ADDED_ERROR);
            } catch (InvalidPasswordException e) {
                this.console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.PASSWORD_NOT_VALID_ERROR);
            } catch (GameAlreadyStartedException e) {
                this.console.err(e.getMessage());
                message = NetworkMessage.simpleServerMessage(MessageType.GAME_ALREADY_STARTED_ERROR);
            }
        } catch (LobbyAlreadyExistsException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_ALREADY_EXISTS_ERROR);
        }

        try {
            player.sendMessage(message);
            this.console.mexS("message " + message.getType() + " sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
        }
        sendOpponentsUpdate(player);
    }

    private void handleLobbyLogin(NetworkMessage message) {
        String[] lobbyInfo = ((String[]) (message.getContent()));
        String lobbyName = lobbyInfo[0];
        String lobbyPassword = lobbyInfo[1];
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            this.console.err(e.getMessage());
            return;
        }

        try {
            this.lobbyManager.add(lobbyName, player, lobbyPassword);
            this.console.log("Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_LOGIN_SUCCESS);
        } catch (LobbyNotFoundException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR);
        } catch (LobbyFullException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_FULL_ERROR);
        } catch (PlayerAlreadyAddedException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.PLAYER_ALREADY_ADDED_ERROR);
        } catch (InvalidPasswordException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.PASSWORD_NOT_VALID_ERROR);
        } catch (GameAlreadyStartedException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.GAME_ALREADY_STARTED_ERROR);
        }

        try {
            player.sendMessage(message);
            this.console.mexS("message " + message.getType() + " sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
        }
        sendOpponentsUpdate(player);
    }

    private void handleLobbyLogout(NetworkMessage message) {
        String lobbyName = (String) message.getContent();
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            this.console.err(e.getMessage());
            return;
        }
        player.notifyDisconnected(); //to stop the Player from waiting a message while nextMessage() is called.

        this.console.log("logging Client \"" + player.getName() + "\" out of Lobby \"" + lobbyName + "\"...");
        try {
            this.lobbyManager.remove(lobbyName, player);
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_LOGOUT_SUCCESS);
            this.console.log("Client \"" + player.getName() + "\" successfully log out of Lobby \"" + lobbyName + "\"");
        } catch (LobbyNotFoundException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR);
        } catch (PlayerNotFoundException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.PLAYER_NOT_FOUND_ERROR);
        } catch (LobbyEmptyException e) {
            this.console.err(e.getMessage());
            message = NetworkMessage.simpleServerMessage(MessageType.LOBBY_EMPTY_ERROR);
        }

        try {
            player.sendMessage(message);
            this.console.mexS("message " + message.getType() + " sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
        }
        sendOpponentsUpdate(player);
    }

    private void sendOpponentsUpdate(Player player) {
        try {
            lobbyManager.notifyOpponentsUpdate(player);
        } catch (LobbyNotFoundException | PlayerNotFoundException e) {
            //this.console.err(e.getMessage());
            e.printStackTrace();
            return;
        }
        this.console.mexS("message " + MessageType.OPPONENTS_LIST_UPDATE + " sent to Client \"" + player.getName() + "\"");
    }

    private void notifyPlayer(NetworkMessage message) {
        Player player;

        try {
            player = getPlayerByName(message.getAuthor());
        } catch (ClientNotRegisteredException e) {
            e.printStackTrace();
            //this.console.err(e.getMessage());
            return;
        }
        player.notifyReceived(message);
        this.console.mexS("message " + message.getType() + " forwarded to Player \"" + player.getName() + "\"");
    }
}
