package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.observer.Observer;
import it.polimi.ingsw.network.common.timer.CountDownTimer;
import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.network.server.lobby.LobbyManager;

import java.util.Queue;

public class ConnectionChecker implements Runnable, Observer {
    private final Console console;

    private final Queue<Player> players;
    private final LobbyManager lobbyManager;
    private final CommunicationHub communicationHub;

    private final CountDownTimer timer;
    private final int CLIENT_RESPONSE_TIMEOUT = 3;

    private Player currentPlayer;

    public ConnectionChecker(Queue<Player> players, LobbyManager lobbyManager, CommunicationHub communicationHub) {
        this.console = Console.getInstance();

        this.players = players;
        this.lobbyManager = lobbyManager;
        this.communicationHub = communicationHub;

        this.timer = new CountDownTimer(CLIENT_RESPONSE_TIMEOUT);
        this.timer.addObserver(this);
    }


    @Override
    public void onEvent() {
        //timer has expired without any response from the Client
        removeCurrent();
    }

    @Override
    public void run() {
        NetworkMessage ping = NetworkMessage.simpleServerMessage(MessageType.PING_MESSAGE);

        for (Player player : this.players) {
            this.currentPlayer = player;

            try {
                this.timer.start();
                this.console.mexS(("message " + ping.getType().toString() + " sent to Client \"" + this.currentPlayer.getName() + "\""));
                this.currentPlayer.sendMessage(ping);
            } catch (ConnectionException e) {
                e.printStackTrace();
                removeCurrent();
            } finally {
                this.timer.stop();
            }
        }
    }

    private void removeCurrent() {
        this.console.err("Client \"" + this.currentPlayer.getName() + "\" lost connection, logging out from his lobby...");
        try {
            try {
                String lobbyName = this.lobbyManager.getLobbyNameByPlayer(currentPlayer);
                this.lobbyManager.remove(lobbyName, currentPlayer);
                this.console.log("Player \"" + this.currentPlayer.getName() + "\" successfully logged out from Lobby \"" + lobbyName + "\"");
            } catch (LobbyNotFoundException e) {
                this.console.log(e.getMessage());
            } catch (PlayerNotFoundException | LobbyEmptyException e) {
                this.console.err(e.getMessage());
            }
            this.console.log("unregistering Client \"" + this.currentPlayer.getName() + "\"...");
            this.communicationHub.unregister(currentPlayer);

        } catch (ClientNotRegisteredException e) {
            this.console.err(e.getMessage());
        }
        this.console.log("Client \"" + this.currentPlayer.getName() + "\" successfully unregistered");
    }
}

