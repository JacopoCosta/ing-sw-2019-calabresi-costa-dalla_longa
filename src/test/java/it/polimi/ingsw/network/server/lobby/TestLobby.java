package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLobby {

    @Test
    public void add() {
        String lobby1Name = "SampleLobby1Name";
        String lobby1Password = "SampleLobby1Password";
        String wrongPassword = "WrongLobbyPassword";

        Lobby lobby1 = new Lobby(lobby1Name, lobby1Password);

        Player p1 = new Player("Mark");
        Player p2 = new Player("Mark");
        Player p3 = new Player("John");
        Player p4 = new Player("Lisa");
        Player p5 = new Player("Anne");
        Player p6 = new Player("Robert");
        Player p7 = new Player("Rick");

        boolean catchTaken;

        //add regular Users: OK
        try {
            lobby1.add(p1, lobby1Password);
            lobby1.add(p3, lobby1Password);
            lobby1.add(p4, lobby1Password);
            lobby1.add(p5, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | PlayerAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //add a regular Player with wrong password: KO
        try {
            lobby1.add(p6, wrongPassword);
            catchTaken = false;
        } catch (InvalidPasswordException ignored) {
            catchTaken = true;
        } catch (LobbyFullException | PlayerAlreadyAddedException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);

        //add the same pser: KO
        try {
            lobby1.add(p1, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException ignored) {
            catchTaken = false;
        } catch (PlayerAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //add a regular Player with the same name of another Player already added: KO
        try {
            lobby1.add(p2, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException ignored) {
            catchTaken = false;
        } catch (PlayerAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //add the last regular Player: OK
        try {
            lobby1.add(p6, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | PlayerAlreadyAddedException | LobbyFullException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //add another regular Player, but the Lobby is full (max 5 players): KO
        try {
            lobby1.add(p7, lobby1Password);
            catchTaken = false;
        } catch (InvalidPasswordException | PlayerAlreadyAddedException ignored) {
            catchTaken = false;
        } catch (LobbyFullException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //login with a null password: OK
        String lobby2Name = "SampleLobby2Name";

        Lobby lobby2 = new Lobby(lobby2Name, null);

        try {
            lobby2.add(p1, null);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | PlayerAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //login with a blank password: OK
        String lobby3Name = "SampleLobby3Name";
        String lobby3Password = " ";

        Lobby lobby3 = new Lobby(lobby3Name, lobby3Password);

        try {
            lobby3.add(p1, lobby3Password);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | PlayerAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }

    @Test
    public void remove() {
        String lobbyName = "SampleLobbyName";
        String lobbyPassword = "SampleLobbyPassword";

        Lobby lobby = new Lobby(lobbyName, lobbyPassword);

        Player p1 = new Player("Mark");
        Player p2 = new Player("Mark");
        Player p3 = new Player("John");

        boolean catchTaken;

        //remove a player from an empty Lobby: KO
        try {
            lobby.remove(p1);
            catchTaken = false;
        } catch (LobbyEmptyException ignored) {
            catchTaken = true;
        } catch (PlayerNotFoundException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);

        //add a sample Player: OK
        try {
            lobby.add(p1, lobbyPassword);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | PlayerAlreadyAddedException e) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //remove a different Player with a different playername: KO
        try {
            lobby.remove(p3);
            catchTaken = false;
        } catch (LobbyEmptyException ignored) {
            catchTaken = false;
        } catch (PlayerNotFoundException ignored) {
            catchTaken = true;
        }
        assertTrue(catchTaken);

        //remove a different Player with the same playername: OK
        try {
            lobby.remove(p2);
            catchTaken = false;
        } catch (LobbyEmptyException | PlayerNotFoundException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //add a sample Player: OK
        try {
            lobby.add(p1, lobbyPassword);
            catchTaken = false;
        } catch (InvalidPasswordException | LobbyFullException | PlayerAlreadyAddedException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //remove the same Player: OK
        try {
            lobby.remove(p1);
            catchTaken = false;
        } catch (LobbyEmptyException | PlayerNotFoundException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);
    }
}