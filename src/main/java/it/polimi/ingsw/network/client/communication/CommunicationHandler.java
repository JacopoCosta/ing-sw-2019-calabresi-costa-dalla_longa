package it.polimi.ingsw.network.client.communication;

import it.polimi.ingsw.network.client.communication.rmi.RMIServerCommunicationInterface;
import it.polimi.ingsw.network.client.communication.socket.SocketServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.deliverable.Deliverable;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class CommunicationHandler {
    public enum Interface {
        SOCKET_INTERFACE,
        RMI_INTERFACE
    }

    private String username;
    private String lobbyName;

    private final ServerCommunicationInterface communicationInterface;

    public CommunicationHandler(String hostAddress, int port, Interface interfaceType) throws ConnectionException {
        username = null;
        lobbyName = null;

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

    public String getUsername() {
        return username;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    private void sendMessage(NetworkMessage message) throws ConnectionException {
        communicationInterface.sendMessage(message);
    }

    private NetworkMessage nextMessage() throws ConnectionException {
        NetworkMessage message;
        do {
            message = communicationInterface.nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

        return message;
    }

    public void deliver(Deliverable deliverable) throws ConnectionException {
        NetworkMessage message = NetworkMessage.completeClientMessage(username, MessageType.CLIENT_MESSAGE, deliverable);
        sendMessage(message);
    }

    public Deliverable nextDeliverable() throws ConnectionException {
        NetworkMessage message = nextMessage();

        if (!(message.getType().equals(MessageType.CLIENT_MESSAGE)))
            throw new ConnectionException("expected: " + MessageType.CLIENT_MESSAGE + ", found: " + message.getType());
        return (Deliverable) message.getContent();
    }

    public void register(String username) throws ConnectionException, ClientAlreadyRegisteredException {
        sendMessage(NetworkMessage.simpleClientMessage(username, MessageType.REGISTER_REQUEST));
        NetworkMessage message = nextMessage();

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

    public void unregister() throws ConnectionException, ClientNotRegisteredException {
        sendMessage(NetworkMessage.simpleClientMessage(username, MessageType.UNREGISTER_REQUEST));
        NetworkMessage message = nextMessage();

        switch (message.getType()) {
            case UNREGISTER_SUCCESS:
                this.username = null;
                return;
            case CLIENT_NOT_REGISTERED_ERROR:
                throw new ClientNotRegisteredException("client not registered, unregistering failed");
            default:
                throw new ConnectionException("expected: " + MessageType.UNREGISTER_SUCCESS + ", " +
                        MessageType.CLIENT_NOT_REGISTERED_ERROR + ", found: " + message.getType());
        }
    }

    public void initLobby(String lobbyName, String lobbyPassword) throws ConnectionException, LobbyAlreadyExistsException {
        String[] lobbyInfo = {lobbyName, lobbyPassword};

        sendMessage(NetworkMessage.completeClientMessage(username, MessageType.LOBBY_CREATE_REQUEST, lobbyInfo));
        NetworkMessage message = nextMessage();

        switch (message.getType()) {
            case LOBBY_CREATE_SUCCESS:
                this.lobbyName = lobbyName;
                return;
            case LOBBY_NOT_FOUND_ERROR:
                throw new ConnectionException("Lobby \"" + lobbyName + "\" not found");
            case LOBBY_FULL_ERROR:
                throw new ConnectionException("Lobby \"" + lobbyName + "\" full");
            case PLAYER_ALREADY_ADDED_ERROR:
                throw new ConnectionException("Player \"" + username + "\" already added to Lobby \"" + lobbyName + "\"");
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

    public void login(String lobbyName, String lobbyPassword)
            throws ConnectionException, LobbyNotFoundException, LobbyFullException, InvalidPasswordException,
            GameAlreadyStartedException, PlayerAlreadyAddedException {
        String[] lobbyInfo = {lobbyName, lobbyPassword};

        sendMessage(NetworkMessage.completeClientMessage(username, MessageType.LOBBY_LOGIN_REQUEST, lobbyInfo));
        NetworkMessage message = nextMessage();

        switch (message.getType()) {
            case LOBBY_LOGIN_SUCCESS:
                this.lobbyName = lobbyName;
                return;
            case LOBBY_NOT_FOUND_ERROR:
                throw new LobbyNotFoundException("Lobby \"" + lobbyName + "\" not found");
            case LOBBY_FULL_ERROR:
                throw new LobbyFullException("Lobby \"" + lobbyName + "\" is full");
            case PLAYER_ALREADY_ADDED_ERROR:
                throw new PlayerAlreadyAddedException("Player \"" + username + "\" already added to Lobby \"" + lobbyName + "\"");
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

    public void logout() throws ConnectionException {
        sendMessage(NetworkMessage.completeClientMessage(username, MessageType.LOBBY_LOGOUT_REQUEST, lobbyName));
        NetworkMessage message = nextMessage();

        switch (message.getType()) {
            case LOBBY_LOGOUT_SUCCESS:
                this.lobbyName = null;
                return;
            case LOBBY_NOT_FOUND_ERROR:
                throw new ConnectionException("Lobby \"" + lobbyName + "\" not found");
            case PLAYER_NOT_FOUND_ERROR:
                throw new ConnectionException("Player \"" + username + "\" not found");
            case LOBBY_EMPTY_ERROR:
                throw new ConnectionException("Lobby \"" + lobbyName + "\" is empty");
            default:
                throw new ConnectionException("expected: " + MessageType.LOBBY_LOGOUT_SUCCESS + ", " +
                        MessageType.LOBBY_NOT_FOUND_ERROR + ", " + MessageType.PLAYER_NOT_FOUND_ERROR + ", " +
                        MessageType.LOBBY_EMPTY_ERROR + ", found: " + message.getType());
        }
    }

    public NetworkMessage getPreGameInfoUpdate() throws ConnectionException {
        NetworkMessage message = nextMessage();

        if (!message.getType().equals(MessageType.COUNTDOWN_EXPIRED) && !message.getType().equals(MessageType.COUNTDOWN_STOPPED)
                && !message.getType().equals(MessageType.COUNTDOWN_UPDATE) && !message.getType().equals(MessageType.OPPONENTS_LIST_UPDATE))
            throw new ConnectionException("expected: " + MessageType.COUNTDOWN_EXPIRED + ", " + MessageType.COUNTDOWN_STOPPED + ", "
                    + MessageType.COUNTDOWN_UPDATE + ", " + MessageType.OPPONENTS_LIST_UPDATE + ", found: " + message.getType());
        return message;

    }

    public Map<String, String> requestLobbyUpdate() throws ConnectionException {
        sendMessage(NetworkMessage.simpleClientMessage(username, MessageType.LOBBY_LIST_UPDATE_REQUEST));
        NetworkMessage message = nextMessage();

        if (!(message.getType().equals(MessageType.LOBBY_LIST_UPDATE_RESPONSE)))
            throw new ConnectionException("expected: " + MessageType.LOBBY_LIST_UPDATE_RESPONSE + ", found " + message.getType());
        return (Map<String, String>) message.getContent(); //safe conversion guaranteed by string type
    }
}
