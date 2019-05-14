package it.polimi.ingsw.network.server.lobby;

import it.polimi.ingsw.model.player.Player;
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
    public void add() {
        String lobby1Name = "SampleLobby1Name";
        String lobby2Name = "SampleLobby2Name";
        String lobby2Password = "SampleLobby2Password";

        String playername = "SamplePlayername";
        Player player = new Player(playername);

        LobbyManager manager = new LobbyManager();

        boolean catchTaken;

        //create a new Lobby
        try {
            manager.newLobby(lobby1Name, null);
        } catch (LobbyAlreadyExistsException ignored) {
        }

        //add a Player to an existing Lobby: OK
        try {
            manager.add(lobby1Name, player, null);
            catchTaken = false;
        } catch (LobbyNotFoundException | InvalidPasswordException | PlayerAlreadyAddedException | LobbyFullException e) {
            catchTaken = true;
            e.printStackTrace();
        }
        assertFalse(catchTaken);

        //add a Player to a non existing Lobby: KO
        try {
            manager.add(lobby2Name, player, lobby2Password);
            catchTaken = false;
        } catch (LobbyNotFoundException ignored) {
            catchTaken = true;
        } catch (LobbyFullException | InvalidPasswordException | PlayerAlreadyAddedException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);
    }

    @Test
    public void remove() {
        String lobby1Name = "SampleLobby3Name";
        String lobby2Name = "SampleLobby4Name";

        String playername = "SamplePlayername";
        Player player = new Player(playername);

        LobbyManager manager = new LobbyManager();

        boolean catchTaken;

        //create a new Lobby
        try {
            manager.newLobby(lobby1Name, null);
        } catch (LobbyAlreadyExistsException ignored) {
        }

        //add a Player to an existing Lobby
        try {
            manager.add(lobby1Name, player, null);
        } catch (LobbyNotFoundException | InvalidPasswordException | PlayerAlreadyAddedException | LobbyFullException ignored) {
        }

        //remove a player from an existing Lobby: OK
        try {
            manager.remove(lobby1Name, player);
            catchTaken = false;
        } catch (LobbyNotFoundException | PlayerNotFoundException | LobbyEmptyException ignored) {
            catchTaken = true;
        }
        assertFalse(catchTaken);

        //remove a Player from a non existing Lobby: KO
        try {
            manager.remove(lobby2Name, player);
            catchTaken = false;
        } catch (LobbyNotFoundException ignored) {
            catchTaken = true;
        } catch (PlayerNotFoundException | LobbyEmptyException ignored) {
            catchTaken = false;
        }
        assertTrue(catchTaken);
    }
}