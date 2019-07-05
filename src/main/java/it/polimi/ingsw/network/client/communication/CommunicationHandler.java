package it.polimi.ingsw.network.client.communication;

import it.polimi.ingsw.network.client.communication.rmi.RMIServerCommunicationInterface;
import it.polimi.ingsw.network.client.communication.socket.SocketServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.deliverable.Deliverable;

import java.util.Map;

/**
 * A {@code CommunicationHandler} offers a transparent way to interact with the remote server-side application.
 * It wraps the {@link ServerCommunicationInterface} and offers simplified primitives to carry information through the
 * network layer to the {@code Server} and retrieve them from remote.
 *
 * @see ServerCommunicationInterface
 */
@SuppressWarnings("unchecked")
public class CommunicationHandler {
    /**
     * The upper bound port assignable to a host.
     */
    public static final int UPPER_BOUND_PORT = 65535;

    /**
     * The lower bound port assignable to a host.
     */
    public static final int LOWER_BOUND_PORT = 1024;

    /**
     * The communication {@code Interface} type available to interact with the remote {@code Server}.
     */
    public enum Interface {
        SOCKET_INTERFACE,
        RMI_INTERFACE
    }

    /**
     * The {@code Client} username with which results to be registered into the remote {@code Server}.
     */
    private String username;

    /**
     * The {@code Lobby} name in which the {@code Client} may be logged into.
     */
    private String lobbyName;

    /**
     * The communication interface used to interact to the remote {@code Server}.
     */
    private final ServerCommunicationInterface communicationInterface;

    /**
     * This is the only constructor. It creates a new {@code CommunicationHandler} from the given parameters.
     *
     * @param hostAddress   the {@code Server} ip address to connect to.
     * @param port          the {@code Server} port to listen from.
     * @param interfaceType the preferred communication method to interact with the remote {@code Server}.
     * @throws ConnectionException if any specific exception is thrown during the communication setup.
     */
    public CommunicationHandler(String hostAddress, int port, Interface interfaceType) throws ConnectionException {
        this.username = null;
        this.lobbyName = null;

        switch (interfaceType) {
            case SOCKET_INTERFACE:
                this.communicationInterface = new SocketServerCommunicationInterface(hostAddress, port);
                break;
            case RMI_INTERFACE:
                this.communicationInterface = new RMIServerCommunicationInterface(hostAddress, port);
                break;
            default:
                throw new ConnectionException("Interface must be of type SOCKET_INTERFACE or RMI_INTERFACE");
        }
    }

    /**
     * Returns the {@code Client} username with which he results to be registered into the {@code Server}.
     *
     * @return the {@code Client} username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the {@code Lobby} name in which the {@code Client} may be logged into.
     *
     * @return the {@code Lobby} name.
     */
    public String getLobbyName() {
        return this.lobbyName;
    }

    /**
     * Sends the given {@link NetworkMessage} to the remote {@code Server} using the desired {@link ServerCommunicationInterface}.
     *
     * @param message the {@link NetworkMessage} to be sent.
     * @throws ConnectionException if any specific exception is thrown during the send process.
     */
    private void sendMessage(NetworkMessage message) throws ConnectionException {
        this.communicationInterface.sendMessage(message);
    }

    /**
     * Returns the next {@link NetworkMessage} received from the remote {@code Server}, ignoring the {@code PING_MESSAGE}
     * messages.
     * Note that tis is a blocking call, meaning that this method returns only when a new {@link NetworkMessage} is available
     * from the underlying communication interface.
     *
     * @return the next {@link NetworkMessage}.
     * @throws ConnectionException if any specific exception is thrown during the receiving process.
     */
    private NetworkMessage nextMessage() throws ConnectionException {
        NetworkMessage message;
        do {
            message = this.communicationInterface.nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

        return message;
    }

    /**
     * Sends a {@link Deliverable} object to the remote server by encapsulating into a lower level {@link NetworkMessage}.
     *
     * @param deliverable the {@link Deliverable} object to be sent.
     * @throws ConnectionException if any specific exception is thrown during the send process.
     */
    public void deliver(Deliverable deliverable) throws ConnectionException {
        NetworkMessage message = NetworkMessage.completeClientMessage(this.username, MessageType.CLIENT_MESSAGE, deliverable);
        this.sendMessage(message);
    }

    /**
     * Returns the next deliverable sent from the remote {@code Server} by extraction from a lower level {@link NetworkMessage}.
     * Note that tis is a blocking call, meaning that this method returns only when a new {@link Deliverable} is available.
     *
     * @return the next {@link Deliverable} from the remote {@code Server}.
     * @throws ConnectionException if any specific exception is thrown during the receiving process.
     */
    public Deliverable nextDeliverable() throws ConnectionException {
        NetworkMessage message = this.nextMessage();

        if (!(message.getType().equals(MessageType.CLIENT_MESSAGE)))
            throw new ConnectionException("expected: " + MessageType.CLIENT_MESSAGE + ", found: " + message.getType());
        return (Deliverable) message.getContent();
    }

    /**
     * Provides a simplified method to perform the registration of a {@code Client} to the remote {@code Server} with the
     * given {@code username}.
     *
     * @param username the identifier the {@code Client} wants to be registered to the {@code Server} with.
     * @throws ConnectionException              if any specific exception is thrown during the receiving process.
     * @throws ClientAlreadyRegisteredException if another {@code Client} with the same {@code username} has already
     *                                          been registered into the remote {@code Server}.
     */
    public void register(String username) throws ConnectionException, ClientAlreadyRegisteredException {
        this.sendMessage(NetworkMessage.simpleClientMessage(username, MessageType.REGISTER_REQUEST));
        NetworkMessage message = this.nextMessage();
        switch (message.getType()) {
            case REGISTER_SUCCESS:
                this.username = username;
                return;
            case CLIENT_ALREADY_REGISTERED_ERROR:
                throw new ClientAlreadyRegisteredException("Client \"" + username + "\" already registered");
            default:
                throw new ConnectionException("expected: " + MessageType.REGISTER_SUCCESS + ", " +
                        MessageType.CLIENT_ALREADY_REGISTERED_ERROR + ", found: " + message.getType());
        }
    }

    /**
     * Provides a simplified method to perform an unregistering operation of a {@code Client} from a remote {@code Server}
     * that corresponds to the {@link #username} provided during the registration step via {@link #register(String)}.
     *
     * @throws ConnectionException          if any specific exception is thrown during the receiving process.
     * @throws ClientNotRegisteredException if no {@code Client} in the {@code Server}can be found that matches
     *                                      the given {@link #username}.
     */
    public void unregister() throws ConnectionException, ClientNotRegisteredException {
        this.sendMessage(NetworkMessage.simpleClientMessage(this.username, MessageType.UNREGISTER_REQUEST));
        NetworkMessage message = this.nextMessage();

        switch (message.getType()) {
            case UNREGISTER_SUCCESS:
                this.username = null;
                return;
            case CLIENT_NOT_REGISTERED_ERROR:
                throw new ClientNotRegisteredException("client not registered, unregistration failed");
            default:
                throw new ConnectionException("expected: " + MessageType.UNREGISTER_SUCCESS + ", " +
                        MessageType.CLIENT_NOT_REGISTERED_ERROR + ", found: " + message.getType());
        }
    }

    /**
     * Provides a simplified method to initiate a new {@code Lobby}. This is done by first creating a new {@code Lobby} with the
     * given {@code lobbyName} and {@code #lobbyPassword} and then perform a login into it, with the the {@link #username}
     * used to previously register to the remote {@code Server}.
     *
     * @param lobbyName     the name of the new {@code Lobby} to be created.
     * @param lobbyPassword the password for the new {@code Lobby} to be created.
     * @throws ConnectionException         if any specific exception is thrown during the receiving process.
     * @throws LobbyAlreadyExistsException if another {@code Lobby} with the same {@code lobbyName} has already been
     *                                     created into the remote {@code Server}.
     */
    public void initLobby(String lobbyName, String lobbyPassword) throws ConnectionException, LobbyAlreadyExistsException {
        String[] lobbyInfo = {lobbyName, lobbyPassword};

        this.sendMessage(NetworkMessage.completeClientMessage(this.username, MessageType.LOBBY_CREATE_REQUEST, lobbyInfo));
        NetworkMessage message = this.nextMessage();

        switch (message.getType()) {
            case LOBBY_CREATE_SUCCESS:
                this.lobbyName = lobbyName;
                return;
            case LOBBY_NOT_FOUND_ERROR:
                throw new ConnectionException("Lobby \"" + lobbyName + "\" not found");
            case LOBBY_FULL_ERROR:
                throw new ConnectionException("Lobby \"" + lobbyName + "\" full");
            case PLAYER_ALREADY_ADDED_ERROR:
                throw new ConnectionException("Player \"" + this.username + "\" already added to Lobby \"" + lobbyName + "\"");
            case PASSWORD_NOT_VALID_ERROR:
                throw new ConnectionException("password \"" + lobbyPassword + "\" not valid for Lobby \"" + lobbyName + "\"");
            case LOBBY_ALREADY_EXISTS_ERROR:
                throw new LobbyAlreadyExistsException("Lobby \"" + lobbyName + "\" already exists");
            default:
                throw new ConnectionException("expected: " + MessageType.LOBBY_CREATE_SUCCESS + ", " +
                        MessageType.LOBBY_NOT_FOUND_ERROR + ", " + MessageType.LOBBY_FULL_ERROR + ", " +
                        MessageType.PLAYER_ALREADY_ADDED_ERROR + ", " + MessageType.PASSWORD_NOT_VALID_ERROR + ", " +
                        MessageType.LOBBY_ALREADY_EXISTS_ERROR + ", found: " + message.getType());
        }
    }

    /**
     * Provides a simplified method to perform a login operation of the previously registered {@code Client} into the selected
     * {@code Lobby} indicated by the given {@code lobbyName}. It is also required a secret {@code lobbyPassword} to access the
     * {@code Lobby} in order to prevent undesired {@code Client}s to access the selected {@code Lobby}.
     *
     * @param lobbyName     the name of the {@code Lobby} to be joined.
     * @param lobbyPassword the secret password.
     * @throws ConnectionException         if any specific exception is thrown during the receiving process.
     * @throws LobbyNotFoundException      if no {@code Lobby} can be found that matches the given {@code lobbyName}.
     * @throws LobbyFullException          if the selected {@code Lobby} has reached its maximum {@code Client} capacity.
     * @throws InvalidPasswordException    if the given {@code lobbyPassword} does not match with the actual {@code Lobby} password.
     * @throws GameAlreadyStartedException if the {@code Game} related to the selected {@code Lobby} has already been started
     *                                     and no other {@code Client} can join the {@code Lobby} anymore.
     * @throws PlayerAlreadyAddedException if another {@code Player} with the same {@link #username} has already
     *                                     been registered into the specified {@code Lobby}.
     */
    public void login(String lobbyName, String lobbyPassword)
            throws ConnectionException, LobbyNotFoundException, LobbyFullException, InvalidPasswordException,
            GameAlreadyStartedException, PlayerAlreadyAddedException {
        String[] lobbyInfo = {lobbyName, lobbyPassword};

        this.sendMessage(NetworkMessage.completeClientMessage(this.username, MessageType.LOBBY_LOGIN_REQUEST, lobbyInfo));
        NetworkMessage message = this.nextMessage();

        switch (message.getType()) {
            case LOBBY_LOGIN_SUCCESS:
                this.lobbyName = lobbyName;
                return;
            case LOBBY_NOT_FOUND_ERROR:
                throw new LobbyNotFoundException("Lobby \"" + lobbyName + "\" not found");
            case LOBBY_FULL_ERROR:
                throw new LobbyFullException("Lobby \"" + lobbyName + "\" is full");
            case PLAYER_ALREADY_ADDED_ERROR:
                throw new PlayerAlreadyAddedException("Player \"" + this.username + "\" already added to Lobby \"" + lobbyName + "\"");
            case PASSWORD_NOT_VALID_ERROR:
                throw new InvalidPasswordException("Password \"" + lobbyPassword + "\" not valid for Lobby \"" + lobbyName + "\"");
            case GAME_ALREADY_STARTED_ERROR:
                throw new GameAlreadyStartedException();
            default:
                throw new ConnectionException("expected: " + MessageType.LOBBY_LOGIN_SUCCESS + ", " +
                        MessageType.LOBBY_NOT_FOUND_ERROR + ", " + MessageType.LOBBY_FULL_ERROR + ", " +
                        MessageType.PLAYER_ALREADY_ADDED_ERROR + ", " + MessageType.PASSWORD_NOT_VALID_ERROR + ", " +
                        MessageType.GAME_ALREADY_STARTED_ERROR + ", found: " + message.getType());
        }
    }

    /**
     * Provides a simplified method to perform alogout operation from the {@code Lobby} he is actually logged into, referenced
     * from {@link #lobbyName}.
     *
     * @throws ConnectionException if any specific exception is thrown during the receiving process.
     */
    public void logout() throws ConnectionException {
        this.sendMessage(NetworkMessage.completeClientMessage(this.username, MessageType.LOBBY_LOGOUT_REQUEST, this.lobbyName));
        NetworkMessage message = this.nextMessage();

        switch (message.getType()) {
            case LOBBY_LOGOUT_SUCCESS:
                this.lobbyName = null;
                return;
            case LOBBY_NOT_FOUND_ERROR:
                throw new ConnectionException("Lobby \"" + this.lobbyName + "\" not found");
            case PLAYER_NOT_FOUND_ERROR:
                throw new ConnectionException("Player \"" + this.username + "\" not found");
            case LOBBY_EMPTY_ERROR:
                throw new ConnectionException("Lobby \"" + this.lobbyName + "\" is empty");
            default:
                throw new ConnectionException("expected: " + MessageType.LOBBY_LOGOUT_SUCCESS + ", " +
                        MessageType.LOBBY_NOT_FOUND_ERROR + ", " + MessageType.PLAYER_NOT_FOUND_ERROR + ", " +
                        MessageType.LOBBY_EMPTY_ERROR + ", found: " + message.getType());
        }
    }

    /**
     * Returns the pre-game information needed to update the list of {@code Player}s connected to the same {@code Lobby}
     * and the time left before the {@code Game} can start.
     *
     * @return the desired pre-game information.
     * @throws ConnectionException if any specific exception is thrown during the receiving process.
     */
    public NetworkMessage getPreGameInfoUpdate() throws ConnectionException {
        NetworkMessage message = this.nextMessage();

        if (!message.getType().equals(MessageType.COUNTDOWN_EXPIRED) && !message.getType().equals(MessageType.COUNTDOWN_STOPPED)
                && !message.getType().equals(MessageType.COUNTDOWN_UPDATE) && !message.getType().equals(MessageType.OPPONENTS_LIST_UPDATE))
            throw new ConnectionException("expected: " + MessageType.COUNTDOWN_EXPIRED + ", " + MessageType.COUNTDOWN_STOPPED + ", "
                    + MessageType.COUNTDOWN_UPDATE + ", " + MessageType.OPPONENTS_LIST_UPDATE + ", found: " + message.getType());
        return message;

    }

    /**
     * Provides a simplified way to request an update of the {@code Lobby} list currently available into the {@code Server}.
     *
     * @return the requested {@code Lobby} list update
     * @throws ConnectionException if any specific exception is thrown during the receiving process.
     */
    public Map<String, String> requestLobbyUpdate() throws ConnectionException {
        this.sendMessage(NetworkMessage.simpleClientMessage(this.username, MessageType.LOBBY_LIST_UPDATE_REQUEST));
        NetworkMessage message = this.nextMessage();

        if (!(message.getType().equals(MessageType.LOBBY_LIST_UPDATE_RESPONSE)))
            throw new ConnectionException("expected: " + MessageType.LOBBY_LIST_UPDATE_RESPONSE + ", found " + message.getType());
        return (Map<String, String>) message.getContent(); //safe conversion guaranteed by string type
    }
}
