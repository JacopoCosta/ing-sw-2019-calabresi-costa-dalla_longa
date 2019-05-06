package it.polimi.ingsw.network.client.networkInterface;

import it.polimi.ingsw.network.common.exceptions.*;

import java.util.Map;

/*
 * A NetworkInterface represents a series of actions the Client can ask the Server to do.
 *
 * */

public interface NetworkInterface {
    void register(String username)
            throws ConnectionLostException, ServerRegistrationFailedException, UserAlreadyAddedException;

    void newLobby(String name, String password)
            throws ConnectionLostException, LobbyCreationFailedException, LobbyAlreadyExistsException;

    void login(String lobbyName, String username, String password)
            throws ConnectionLostException, LobbyLoginFailedException, LobbyNotFoundException, InvalidPasswordException,
            LobbyFullException, UserAlreadyAddedException, UserNotFoundException;

    void logout(String lobbyName, String username)
            throws ConnectionLostException, LobbyLogoutFailedException, LobbyNotFoundException,
            UserNotFoundException, EmptyLobbyException;

    void unregister(String username) throws UserNotFoundException, ServerUnregisteringFailedException, ConnectionLostException;

    Map<String, String> getLobbies() throws ConnectionLostException;
}
