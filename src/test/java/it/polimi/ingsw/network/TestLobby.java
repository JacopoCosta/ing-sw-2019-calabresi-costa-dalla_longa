package it.polimi.ingsw.network;

import it.polimi.ingsw.network.exceptions.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLobby {

    @Test
    public void addUser() {
        String lobby1Name = "SampleLobby1Name";
        String lobby1Password = "SampleLobby1Password";
        String wrongPassword = "WrongLobbyPassword";

        Lobby lobby1 = new Lobby(lobby1Name, lobby1Password);

        User u1 = new User("Mark");
        User u2 = new User("Mark");
        User u3 = new User("John");
        User u4 = new User("Lisa");
        User u5 = new User("Anne");
        User u6 = new User("Robert");
        User u7 = new User("Rick");

        boolean catchTaken;

        //add regular Users: OK
        try {
            lobby1.addUser(u1, lobby1Password);
            lobby1.addUser(u3, lobby1Password);
            lobby1.addUser(u4, lobby1Password);
            lobby1.addUser(u5, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | UserAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //add a regular User with wrong password: KO
        try {
            lobby1.addUser(u6, wrongPassword);
            catchTaken = false;
        } catch (InvalidPasswordException ignored) {
            catchTaken = true;
        } catch (LobbyFullException | UserAlreadyAddedException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);

        //add the same user: KO
        try {
            lobby1.addUser(u1, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException ignored) {
            catchTaken = false;
        } catch (UserAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //add a regular User with the same name of another User already added: KO
        try {
            lobby1.addUser(u2, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException ignored) {
            catchTaken = false;
        } catch (UserAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //add the last regular User: OK
        try {
            lobby1.addUser(u6, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | UserAlreadyAddedException | LobbyFullException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //add another regular User, but the Lobby is full (max 5 users): KO
        try {
            lobby1.addUser(u7, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | UserAlreadyAddedException ignored) {
            catchTaken = false;
        } catch (LobbyFullException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //login with a null password: OK
        String lobby2Name = "SampleLobby2Name";

        Lobby lobby2 = new Lobby(lobby2Name, null);

        try {
            lobby2.addUser(u1, null);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | UserAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //login with a blank password: OK
        String lobby3Name = "SampleLobby3Name";
        String lobby3Password = " ";

        Lobby lobby3 = new Lobby(lobby3Name, lobby3Password);

        try {
            lobby3.addUser(u1, lobby3Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | UserAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }

    @Test
    public void removeUser() {
        String lobbyName = "SampleLobbyName";
        String lobbyPassword = "SampleLobbyPassword";

        Lobby lobby = new Lobby(lobbyName, lobbyPassword);

        User u1 = new User("Mark");
        User u2 = new User("Mark");
        User u3 = new User("John");

        boolean catchTaken;

        //remove a user from an empty Lobby: KO
        try {
            lobby.removeUser(u1);
            catchTaken = false;
        } catch (EmptyLobbyException ignored) {
            catchTaken = true;
        } catch (UserNotFoundException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);

        //add a sample User: OK
        try {
            lobby.addUser(u1, lobbyPassword);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | UserAlreadyAddedException e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //remove a different User with a different username: KO
        try {
            lobby.removeUser(u3);
            catchTaken = false;
        } catch (EmptyLobbyException ignored) {
            catchTaken = false;
        } catch (UserNotFoundException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //remove a different User with the same username: OK
        try {
            lobby.removeUser(u2);
            catchTaken = false;
        } catch (EmptyLobbyException | UserNotFoundException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //add a sample User: OK
        try {
            lobby.addUser(u1, lobbyPassword);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | UserAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //remove the same User: OK
        try {
            lobby.removeUser(u1);
            catchTaken = false;
        } catch (EmptyLobbyException | UserNotFoundException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }
}