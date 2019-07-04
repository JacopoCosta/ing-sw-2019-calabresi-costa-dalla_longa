package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.util.console.Console;
import it.polimi.ingsw.network.server.lobby.LobbyManager;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A {@code CommunicationHub} represents the intermediate class between a {@code ClientCommunicationInterface} and the
 * more abstract {@link Player}.
 * This class collects all the {@link NetworkMessage}s received from the {@code Server} and process them. Messages that
 * does not compete to it, are forwarded to the corresponding {@link Player}.
 * Also the {@code CommunicationHub} is responsible for the continuous connection check for each client connected. After
 * a disconnection has been detected, the proper procedures of logout and unregistering are performed in order to guarantee
 * the integrity of this ecosystem.
 */
@SuppressWarnings("FieldCanBeLocal")
public class CommunicationHub {
    /**
     * The {@code CommunicationHub} unique instance.
     */
    private static CommunicationHub instance;

    /**
     * A {@code List} of all the {@link Player}s connected to te {@code Server}.
     */
    private final Queue<Player> players;

    /**
     * A reference to the {@link LobbyManager} responsible for the {@code Lobby} handling.
     */
    private final LobbyManager lobbyManager;

    /**
     * The {@link ScheduledExecutorService} responsible for the continuous connection check among all the clients.
     */
    private final ScheduledExecutorService checkConnectionExecutor;

    /**
     * The task to be performed every time a connection check is required.
     */
    private final Runnable connectionCheckTask;

    /**
     * The delay between subsequent connection check tasks.
     */
    private final int CHECK_PERIOD = 5;

    /**
     * The {@link Console} used to print any kind of errors that may occur during the client execution.
     */
    private final Console console = Console.getInstance();

    /**
     * This is the only constructor. It creates a new {@code CommunicationHub} and the relative connection check task.
     */
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

                                this.lobbyManager.notifyOpponentsUpdate(lobbyName);
                            } catch (LobbyNotFoundException e) {
                                this.console.log(e.getMessage());
                            } catch (PlayerNotFoundException | LobbyEmptyException e) {
                                e.printStackTrace();
                                //this.console.err(e.getMessage());
                            }
                            this.console.log("unregistering Client \"" + player.getName() + "\"...");
                            this.unregister(player);

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

    /**
     * Return the {@code CommunicationHub} unique instance.
     *
     * @return the {@code CommunicationHub} unique instance.
     */
    public static CommunicationHub getInstance() {
        if (instance == null)
            instance = new CommunicationHub();
        return instance;
    }

    /**
     * Returns a {@link Player} that corresponds to the given {@code name}, if any.
     *
     * @param name the name of the {@link Player} to find.
     * @return the {@link Player} whose name corresponds to the given {@code name}.
     * @throws PlayerNotFoundException if no {@link Player} can be found whose name matches the given {@code name};
     */
    private Player getPlayerByName(String name) throws PlayerNotFoundException {
        if (name == null)
            throw new NullPointerException("Client name is null");

        for (Player player : this.players)
            if (player.getName().equals(name))
                return player;
        throw new PlayerNotFoundException("Client \"" + name + "\" not registered");
    }

    /**
     * Register the given {@link Player} into the {@code Server}.
     *
     * @param player the {@link Player} to be registered.
     * @return {@code true} if the {@link Player} was already found into the {@code Server} and is has simply rejoined,
     * {@code false} if the {@link Player} is a new one.
     * @throws ClientAlreadyRegisteredException if another {@link Player} with the same name of the {@link Player} given
     *                                          has already been registered to the {@code Server}.
     */
    private boolean register(Player player) throws ClientAlreadyRegisteredException {
        if (player == null)
            throw new NullPointerException("Client is null");

        Player dormantPlayer;
        try {
            dormantPlayer = this.getPlayerByName(player.getName());
        } catch (PlayerNotFoundException ignored) {
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

    /**
     * Unregister the given {@link Player} from the {@code Server}.
     *
     * @param player the {@link Player} to be unregistered.
     * @throws ClientNotRegisteredException if no {@link Player} whose name equals the name of the given {@link Player}
     *                                      has been previously registered into the {@code Server}.
     */
    private void unregister(Player player) throws ClientNotRegisteredException {
        if (player == null)
            throw new NullPointerException("Player is null");
        if (!this.players.contains(player))
            throw new ClientNotRegisteredException("Client \"" + player.getName() + "\" not registered");
        this.players.remove(player);
    }

    /**
     * This method is the core of all the {@code CommunicationHub} logic. it is called every time the underlying network
     * level receives a {@link NetworkMessage} from the remote {@code Client}. Its only task is to classify the message
     * received and forward it to the corresponding handler method.
     *
     * @param message the {@link NetworkMessage} to be classified.
     * @see MessageType
     */
    public synchronized void handleMessage(NetworkMessage message) {
        this.console.mexC("Message " + message.getType().toString() + " received from Client \"" + message.getAuthor() + "\"");

        switch (message.getType()) {
            case REGISTER_REQUEST:
                this.handleRegistration(message);
                break;
            case UNREGISTER_REQUEST:
                this.handleUnregistering(message);
                break;
            case LOBBY_LIST_UPDATE_REQUEST:
                this.handleUpdateRequest(message);
                break;
            case LOBBY_CREATE_REQUEST:
                this.handleLobbyCreation(message);
                break;
            case LOBBY_LOGIN_REQUEST:
                this.handleLobbyLogin(message);
                break;
            case LOBBY_LOGOUT_REQUEST:
                this.handleLobbyLogout(message);
                break;
            case CLIENT_MESSAGE:
                this.notifyPlayer(message);
                break;
            default:
                this.console.err("Message " + message.getType() + " received from Client \"" + message.getAuthor() + "\": ignored");
        }
    }

    /**
     * This method handles the {@link NetworkMessage}s of type {@code REGISTER_REQUEST}. It is responsible for the proper
     * registering procedure of the {@link NetworkMessage} author into the {@code Server}.
     *
     * @param message the {@link NetworkMessage} to be handled.
     * @see MessageType
     */
    private void handleRegistration(NetworkMessage message) {
        Player player = (Player) message.getContent();

        this.console.log("registering Client \"" + player.getName() + "\"...");
        try {
            if (!this.register(player))
                this.console.log("Client \"" + player.getName() + "\" successfully registered");
            else
                this.console.log("Client \"" + player.getName() + "\" already found in server, rejoin successful");
            message = NetworkMessage.simpleServerMessage(MessageType.REGISTER_SUCCESS);
        } catch (ClientAlreadyRegisteredException e) {
            message = NetworkMessage.simpleServerMessage(MessageType.CLIENT_ALREADY_REGISTERED_ERROR);
            this.console.err(e.getMessage());
        }
        sendMessage(player, message);
    }

    /**
     * This method handles the {@link NetworkMessage}s of type {@code UNREGISTER_REQUEST}. It is responsible for the proper
     * unregistering procedure of the {@link NetworkMessage} author out of the {@code Server}.
     *
     * @param message the {@link NetworkMessage} to be handled.
     * @see MessageType
     */
    private void handleUnregistering(NetworkMessage message) {
        Player player;

        try {
            player = (this.getPlayerByName(message.getAuthor()));
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
            //this.console.err(e.getMessage());
            return;
        }

        this.console.log("unregistering Client \"" + player.getName() + "\"...");
        try {
            this.unregister(player);
            message = NetworkMessage.simpleServerMessage(MessageType.UNREGISTER_SUCCESS);
            this.console.log("Client \"" + player.getName() + "\" successfully unregistered");
        } catch (ClientNotRegisteredException e) {
            message = NetworkMessage.simpleServerMessage(MessageType.CLIENT_NOT_REGISTERED_ERROR);
            this.console.err(e.getMessage());
        }
        sendMessage(player, message);
    }

    /**
     * This method handles the {@link NetworkMessage}s of type {@code LOBBY_UPDATE_REQUEST}. It is responsible for the correct
     * send of the updated {@code Lobby} list to the {@link NetworkMessage} author.
     *
     * @param message the {@link NetworkMessage} to be handled.
     * @see MessageType
     */
    private void handleUpdateRequest(NetworkMessage message) {
        try {
            Player player = this.getPlayerByName(message.getAuthor());
            Map<String, String> lobbies = this.lobbyManager.getLobbiesStatus();

            sendMessage(player, NetworkMessage.completeServerMessage(MessageType.LOBBY_LIST_UPDATE_RESPONSE, lobbies));
        } catch (PlayerNotFoundException e) {
            this.console.err(e.getMessage());
        }
    }

    /**
     * This method handles the {@link NetworkMessage}s of type {@code LOBBY_CREATE_REQUEST}. It is responsible for the
     * proper creation of a new {@code Lobby}. All the information needed are stored into the message {@code content} field.
     *
     * @param message the {@link NetworkMessage} to be handled.
     * @see MessageType
     */
    private void handleLobbyCreation(NetworkMessage message) {
        String[] lobbyInfo = ((String[]) (message.getContent()));
        String lobbyName = lobbyInfo[0];
        String lobbyPassword = lobbyInfo[1];
        Player player;

        try {
            player = (this.getPlayerByName(message.getAuthor()));
        } catch (PlayerNotFoundException e) {
            this.console.err(e.getMessage());
            return;
        }

        try {
            this.lobbyManager.newLobby(lobbyName, lobbyPassword);
            this.console.log("Client \"" + message.getAuthor() + "\" created new Lobby \"" + lobbyName + "\" with password \"" + lobbyPassword + "\"");

            try {
                this.lobbyManager.add(lobbyName, player, lobbyPassword);

                this.console.log("Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");
                sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_CREATE_SUCCESS));

                this.sendOpponentsUpdate(player);
            } catch (LobbyNotFoundException e) {
                this.console.err(e.getMessage());
                sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR));
            } catch (LobbyFullException e) {
                this.console.err(e.getMessage());
                sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_FULL_ERROR));
            } catch (PlayerAlreadyAddedException e) {
                this.console.err(e.getMessage());
                sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.PLAYER_ALREADY_ADDED_ERROR));
            } catch (InvalidPasswordException e) {
                this.console.err(e.getMessage());
                sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.PASSWORD_NOT_VALID_ERROR));
            } catch (GameAlreadyStartedException e) {
                this.console.err(e.getMessage());
                sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.GAME_ALREADY_STARTED_ERROR));
            }
        } catch (LobbyAlreadyExistsException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_ALREADY_EXISTS_ERROR));
        }
    }

    /**
     * This method handles the {@link NetworkMessage}s of type {@code LOBBY_LOGIN_REQUEST}. It is responsible for the
     * proper login of the {@link NetworkMessage} author into the {@code Lobby} whose name can be found into the given message.
     *
     * @param message the {@link NetworkMessage} to be handled.
     * @see MessageType
     */
    private void handleLobbyLogin(NetworkMessage message) {
        String[] lobbyInfo = ((String[]) (message.getContent()));
        String lobbyName = lobbyInfo[0];
        String lobbyPassword = lobbyInfo[1];
        Player player;

        try {
            player = this.getPlayerByName(message.getAuthor());
        } catch (PlayerNotFoundException e) {
            this.console.err(e.getMessage());
            return;
        }

        try {
            this.lobbyManager.add(lobbyName, player, lobbyPassword);
            this.console.log("Client \"" + message.getAuthor() + "\" logged into Lobby \"" + lobbyName + "\"");

            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_LOGIN_SUCCESS));

            this.sendOpponentsUpdate(player);
            this.sendTimerUpdate(player);
        } catch (LobbyNotFoundException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR));
        } catch (LobbyFullException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_FULL_ERROR));
        } catch (PlayerAlreadyAddedException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.PLAYER_ALREADY_ADDED_ERROR));
        } catch (InvalidPasswordException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.PASSWORD_NOT_VALID_ERROR));
        } catch (GameAlreadyStartedException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.GAME_ALREADY_STARTED_ERROR));
        }
    }

    /**
     * This method handles the {@link NetworkMessage}s of type {@code LOBBY_LOGOUT_REQUEST}. It is responsible for the
     * proper logout procedure of the {@link NetworkMessage} author.
     *
     * @param message the {@link NetworkMessage} to be handled.
     * @see MessageType
     */
    private void handleLobbyLogout(NetworkMessage message) {
        String lobbyName = (String) message.getContent();
        Player player;

        try {
            player = this.getPlayerByName(message.getAuthor());
        } catch (PlayerNotFoundException e) {
            this.console.err(e.getMessage());
            return;
        }
        player.notifyDisconnected(); //to stop the Player from waiting a message while nextMessage() is called.

        this.console.log("logging Client \"" + player.getName() + "\" out of Lobby \"" + lobbyName + "\"...");
        try {
            this.lobbyManager.remove(lobbyName, player);

            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_LOGOUT_SUCCESS));
            this.console.log("Client \"" + player.getName() + "\" successfully log out of Lobby \"" + lobbyName + "\"");

            this.sendOpponentsUpdate(player);
        } catch (LobbyNotFoundException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_NOT_FOUND_ERROR));
        } catch (PlayerNotFoundException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.PLAYER_NOT_FOUND_ERROR));
        } catch (LobbyEmptyException e) {
            this.console.err(e.getMessage());
            sendMessage(player, NetworkMessage.simpleServerMessage(MessageType.LOBBY_EMPTY_ERROR));
        }
    }

    /**
     * This method is responsible for the proper send of the updated list of {@link Player}s in the same {@code lobby}
     * of the given {@code player}.
     *
     * @param player the {@link Player} to send the update to.
     */
    private void sendOpponentsUpdate(Player player) {
        try {
            this.lobbyManager.notifyOpponentsUpdate(this.lobbyManager.getLobbyNameByPlayer(player));
        } catch (LobbyNotFoundException | PlayerNotFoundException e) {
            //this.console.err(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendTimerUpdate(Player player) {
        try {
            this.lobbyManager.notifyTimeUpdate(this.lobbyManager.getLobbyNameByPlayer(player));
        } catch (LobbyNotFoundException | PlayerNotFoundException e) {
            //this.console.err(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method handles the {@link NetworkMessage}s of type {@code CLIENT_MESSAGE}. It is responsible for the correct
     * forward of the given {@link NetworkMessage} to the {@link Player} counterpart of the message author.
     *
     * @param message the {@link NetworkMessage} to be handled.
     * @see MessageType
     */
    private void notifyPlayer(NetworkMessage message) {
        Player player;

        try {
            player = this.getPlayerByName(message.getAuthor());
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
            //this.console.err(e.getMessage());
            return;
        }
        player.notifyReceived(message);
        this.console.mexS("message " + message.getType() + " forwarded to Player \"" + player.getName() + "\"");
    }

    private void sendMessage(Player player, NetworkMessage message) {
        try {
            player.sendMessage(message);
            this.console.mexS("message " + message.getType() + " sent to Client \"" + player.getName() + "\"");
        } catch (ConnectionException e) {
            e.printStackTrace();
            //this.console.err(e.getClass() + ": " + e.getMessage());
        }
    }
}
