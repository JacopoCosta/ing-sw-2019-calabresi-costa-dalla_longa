package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.network.common.exceptions.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLobbyManager {

    @Test
    public void newLobby() {
        String lobbyName = "SampleLobby0Name";
        String lobbyPassword = "SampleLobby0Password";
        LobbyManager manager = new LobbyManager();

        boolean catchTaken;

        //create a new Lobby: OK
        try {
            manager.newLobby(lobbyName, lobbyPassword);
            catchTaken = false;
        } catch (LobbyAlreadyExistsException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //create the same Lobby again: KO
        try {
            manager.newLobby(lobbyName, lobbyPassword);
            catchTaken = false;
        } catch (LobbyAlreadyExistsException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);
    }

    @Test
    public void addToLobby() {
        String lobby1Name = "SampleLobby1Name";
        String lobby2Name = "SampleLobby2Name";
        String lobby2Password = "SampleLobby2Password";

        String username = "SampleUsername";
        User user = new User(username);

        LobbyManager manager = new LobbyManager();

        boolean catchTaken;

        //create a new Lobby
        try {
            manager.newLobby(lobby1Name, null);
        } catch (LobbyAlreadyExistsException ignored) {
        }

        //add a User to an existing Lobby: OK
        try {
            manager.add(lobby1Name, user, null);
            catchTaken = false;
        } catch (LobbyNotFoundException | InvalidPasswordException | UserAlreadyAddedException | LobbyFullException e) {
            catchTaken = true;
            e.printStackTrace();
        }
        assertFalse(catchTaken);

        //add a User to a non existing Lobby: KO
        try {
            manager.add(lobby2Name, user, lobby2Password);
            catchTaken = false;
        } catch (LobbyNotFoundException ignored) {
            catchTaken = true;
        } catch (LobbyFullException | InvalidPasswordException | UserAlreadyAddedException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);
    }

    @Test
    public void removeFromLobby() {
        String lobby1Name = "SampleLobby3Name";
        String lobby2Name = "SampleLobby4Name";

        String username = "SampleUsername";
        User user = new User(username);

        LobbyManager manager = new LobbyManager();

        boolean catchTaken;

        //create a new Lobby
        try {
            manager.newLobby(lobby1Name, null);
        } catch (LobbyAlreadyExistsException ignored) {
        }

        //add a User to an existing Lobby
        try {
            manager.add(lobby1Name, user, null);
        } catch (LobbyNotFoundException | InvalidPasswordException | UserAlreadyAddedException | LobbyFullException ignored) {
        }

        //remove a user from an existing Lobby: OK
        try {
            manager.remove(lobby1Name, user);
            catchTaken = false;
        } catch (LobbyNotFoundException | UserNotFoundException | EmptyLobbyException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //remove a User from a non existing Lobby: KO
        try {
            manager.remove(lobby2Name, user);
            catchTaken = false;
        } catch (LobbyNotFoundException ignored) {
            catchTaken = true;
        } catch (UserNotFoundException | EmptyLobbyException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);
    }

    @Test
    public void exists() {
        String lobby1Name = "SampleLobby7Name";
        String lobby2Name = "SampleLobby8Name";

        String lobby1Password = "SampleLobby7Password";

        LobbyManager manager = new LobbyManager();

        //create a new Lobby
        try {
            manager.newLobby(lobby1Name, lobby1Password);
        } catch (LobbyAlreadyExistsException ignored) {
        }

        assertTrue(manager.exists(lobby1Name));
        assertFalse(manager.exists(lobby2Name));
    }
}