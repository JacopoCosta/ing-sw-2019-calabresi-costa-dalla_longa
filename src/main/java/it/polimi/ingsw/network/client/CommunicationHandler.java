package it.polimi.ingsw.network.client;

import it.polimi.ingsw.network.client.rmi.RmiServerCommunicationInterface;
import it.polimi.ingsw.network.client.socket.SocketServerCommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.view.virtual.Deliverable;
import it.polimi.ingsw.view.virtual.Message;

import java.util.Map;

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
                try {
                    this.communicationInterface = new SocketServerCommunicationInterface(hostAddress, port);
                } catch (ConnectionException e) {
                    throw new ConnectionException("SocketServerCommunicationInterface configuration failed", e);
                }
                break;
            case RMI_INTERFACE:
                try {
                    this.communicationInterface = new RmiServerCommunicationInterface(hostAddress, port);
                } catch (ConnectionException e) {
                    throw new ConnectionException("RmiServerCommunicationInterface configuration failed", e);
                }
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

    private void sendMessage(it.polimi.ingsw.network.common.message.Message message) throws ConnectionException {
        communicationInterface.sendMessage(message);
    }

    private it.polimi.ingsw.network.common.message.Message nextMessage() throws ConnectionException {
        return communicationInterface.nextMessage();
    }

    public void deliver(Deliverable deliverable) throws ConnectionException {
        it.polimi.ingsw.network.common.message.Message message = it.polimi.ingsw.network.common.message.Message.completeMessage(username, MessageType.CLIENT_MESSAGE, deliverable);
        sendMessage(message);
    }

    public Deliverable nextDeliverable() throws ConnectionException {
        it.polimi.ingsw.network.common.message.Message message;
        do {
            message = nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

        if (!(message.getType().equals(MessageType.CLIENT_MESSAGE)))
            throw new ConnectionException("expected " + MessageType.CLIENT_MESSAGE + " found " + message.getType());
        return (Deliverable) message.getContent();
    }

    public void register(String username) throws ConnectionException, ClientAlreadyRegisteredException {
        it.polimi.ingsw.network.common.message.Message message = it.polimi.ingsw.network.common.message.Message.simpleMessage(username, MessageType.REGISTER_REQUEST);

        sendMessage(message);
        do {
            message = nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

        switch (message.getType()) {
            case REGISTER_SUCCESS:
                this.username = username;
                return;
            case CLIENT_ALREADY_REGISTERED_ERROR:
            default:
                throw new ClientAlreadyRegisteredException("Client \"" + username + "\" already registered");
        }
    }

    public void unregister() throws ConnectionException, ClientNotRegisteredException {
        it.polimi.ingsw.network.common.message.Message message = it.polimi.ingsw.network.common.message.Message.simpleMessage(username, MessageType.UNREGISTER_REQUEST);

        sendMessage(message);
        do {
            message = nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

        switch (message.getType()) {
            case UNREGISTER_SUCCESS:
                this.username = null;
                return;
            case CLIENT_NOT_REGISTERED_ERROR:
            default:
                throw new ClientNotRegisteredException("client not registered, unregistering failed\n");
        }
    }

    public Map<String, String> requestUpdate() throws ConnectionException {
        it.polimi.ingsw.network.common.message.Message message = it.polimi.ingsw.network.common.message.Message.simpleMessage(username, MessageType.LOBBY_LIST_UPDATE_REQUEST);

        sendMessage(message);
        do {
            message = nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

        if (!(message.getType().equals(MessageType.LOBBY_LIST_UPDATE_RESPONSE)))
            throw new ConnectionException("expected " + MessageType.LOBBY_LIST_UPDATE_RESPONSE + " found " + message.getType());
        return (Map<String, String>) message.getContent(); //safe conversion guaranteed by string type
    }

    public void newLobby(String lobbyName, String lobbyPassword) throws ConnectionException, LobbyAlreadyExistsException {
        String[] lobbyInfo = {lobbyName, lobbyPassword};
        it.polimi.ingsw.network.common.message.Message message = it.polimi.ingsw.network.common.message.Message.completeMessage(username, MessageType.LOBBY_CREATE_REQUEST, lobbyInfo);

        sendMessage(message);
        do {
            message = nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

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
        }
    }

    public void login(String lobbyName, String lobbyPassword)
            throws ConnectionException, LobbyNotFoundException, LobbyFullException, InvalidPasswordException {
        String[] lobbyInfo = {lobbyName, lobbyPassword};
        it.polimi.ingsw.network.common.message.Message message = it.polimi.ingsw.network.common.message.Message.completeMessage(username, MessageType.LOBBY_LOGIN_REQUEST, lobbyInfo);

        sendMessage(message);
        do {
            message = nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

        switch (message.getType()) {
            case LOBBY_LOGIN_SUCCESS:
                this.lobbyName = lobbyName;
                return;
            case LOBBY_NOT_FOUND_ERROR:
                throw new LobbyNotFoundException("Lobby \"" + lobbyName + "\" not found");
            case LOBBY_FULL_ERROR:
                throw new LobbyFullException("Lobby \"" + lobbyName + "\" is full");
            case PLAYER_ALREADY_ADDED_ERROR:
                throw new ConnectionException("Player \"" + username + "\" already added to Lobby \"" + lobbyName + "\"");
            case PASSWORD_NOT_VALID_ERROR:
                throw new InvalidPasswordException("Password \"" + lobbyPassword + "\" not valid for Lobby \"" + lobbyName + "\"");
        }
    }

    public void logout() throws ConnectionException {
        it.polimi.ingsw.network.common.message.Message message = it.polimi.ingsw.network.common.message.Message.completeMessage(username, MessageType.LOBBY_LOGOUT_REQUEST, lobbyName);

        sendMessage(message);
        do {
            message = nextMessage();
        } while (message.getType().equals(MessageType.PING_MESSAGE));

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
        }
    }
}
