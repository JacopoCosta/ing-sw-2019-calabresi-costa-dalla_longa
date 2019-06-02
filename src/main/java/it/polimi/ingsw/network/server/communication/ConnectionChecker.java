package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.*;
import it.polimi.ingsw.network.common.message.MessageType;
import it.polimi.ingsw.network.common.message.NetworkMessage;
import it.polimi.ingsw.network.common.observer.Observer;
import it.polimi.ingsw.network.common.timer.CountDownTimer;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.network.server.lobby.LobbyManager;

import java.util.Queue;

public class ConnectionChecker implements Runnable, Observer {
    private final Console console;

    private final Queue<Player> players;
    private final LobbyManager lobbyManager;
    private final CommunicationHub communicationHub;

    private final CountDownTimer timer;
    private final int CLIENT_RESPONSE_TIMEOUT = 3;

    private Player current;

    public ConnectionChecker(Queue<Player> players, LobbyManager lobbyManager, CommunicationHub communicationHub) {
        console = Console.getInstance();

        this.players = players;
        this.lobbyManager = lobbyManager;
        this.communicationHub = communicationHub;

        timer = new CountDownTimer(CLIENT_RESPONSE_TIMEOUT);
        timer.addObserver(this);
    }


    @Override
    public void onEvent() {
        //timer has expired without any response from the Client
        removeCurrent();
    }

    @Override
    public void run() {
        NetworkMessage ping = NetworkMessage.simpleServerMessage(MessageType.PING_MESSAGE);

        for (Player player : players) {
            current = player;

            try {
                timer.start();
                console.mexS(("message " + ping.getType().toString() + " sent to Client \"" + current.getName() + "\""));
                current.sendMessage(ping);
            } catch (ConnectionException e) {
                e.printStackTrace();
                removeCurrent();
            } finally {
                timer.stop();
            }
        }
    }

    private void removeCurrent() {
        console.err("Client \"" + current.getName() + "\" lost connection, logging out from his lobby...");
        try {
            try {
                String lobbyName = lobbyManager.getLobbyNameByPlayer(current);
                lobbyManager.remove(lobbyName, current);
                console.log("Player \"" + current.getName() + "\" successfully logged out from Lobby \"" + lobbyName + "\"");
            } catch (LobbyNotFoundException e) {
                console.log(e.getMessage());
            } catch (PlayerNotFoundException | LobbyEmptyException e) {
                console.err(e.getMessage());
            }
            console.log("unregistering Client \"" + current.getName() + "\"...");
            communicationHub.unregister(current);

        } catch (ClientNotRegisteredException e) {
            console.err(e.getMessage());
        }
        console.log("Client \"" + current.getName() + "\" successfully unregistered");
    }
}

