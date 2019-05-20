package it.polimi.ingsw.network.server.communication;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.exceptions.PlayerNotRegisteredException;
import it.polimi.ingsw.network.common.exceptions.PlayerAlreadyRegisteredException;
import it.polimi.ingsw.network.common.message.Message;
import it.polimi.ingsw.network.common.message.MessageType;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationHub {
    private static CommunicationHub instance;

    private final Queue<Player> players;

    private CommunicationHub() {
        players = new ConcurrentLinkedQueue<>();
    }

    public static CommunicationHub getInstance() {
        if (instance == null)
            instance = new CommunicationHub();
        return instance;
    }

    private Player getPlayerByName(String name) throws PlayerNotRegisteredException {
        if (name == null)
            throw new NullPointerException("Client name is null");

        for (Player player : players)
            if (player.getName().equals(name))
                return player;
        throw new PlayerNotRegisteredException("Client \"" + name + "\" not registered");
    }

    private void register(Player player) throws PlayerAlreadyRegisteredException {
        if (player == null)
            throw new NullPointerException("Client is null");
        if (players.contains(player))
            throw new PlayerAlreadyRegisteredException("Client \"" + player.getName() + "\" already registered");
        players.add(player);
    }

    private void unregister(Player player) throws PlayerNotRegisteredException {
        if (player == null)
            throw new NullPointerException("Player is null");
        if (!players.contains(player))
            throw new PlayerNotRegisteredException("Client \"" + player.getName() + "\" not registered");
        players.remove(player);
    }

    public synchronized void handleMessage(Message message) {

        System.out.println("LOG: Message " + message.getType().toString() + " received from Client \"" + message.getAuthor());
        switch (message.getType()) {
            case REGISTER_REQUEST:
                handleRegistration(message);
                break;
            case UNREGISTER_REQUEST:
                handleUnregistering(message);
                break;
            case LOBBY_LOGIN_REQUEST:
                handleLogin(message);
                break;
            case LOBBY_LOGOUT_REQUEST:
                handleLogout(message);
                break;
            default:
                notifyPlayer(message);
        }
    }

    private void handleRegistration(Message message) {
        Message response;
        Player player = (Player) message.getContent();

        System.out.println("LOG: registering Client \"" + player.getName() + "\"...");
        try {
            register(player);
            response = Message.simpleMessage(null, MessageType.REGISTER_SUCCESS);
            System.out.println("LOG: Client \"" + player.getName() + "\" successfully registered");
        } catch (PlayerAlreadyRegisteredException e) {
            response = Message.simpleMessage(null, MessageType.PLAYER_ALREADY_REGISTERED_ERROR);
            System.err.println("ERROR: " + e.getMessage());
        }

        sendMessage(player, response);
    }

    private void handleUnregistering(Message message) {
        Message response;
        Player player;

        try {
            String username = (String) message.getContent();
            player = (getPlayerByName(username));
        } catch (PlayerNotRegisteredException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("LOG: unregistering Client \"" + player.getName() + "\"...");
        try {
            unregister(player);
            response = Message.simpleMessage(null, MessageType.UNREGISTER_SUCCESS);
            System.out.println("LOG: Client \"" + player.getName() + "\" successfully unregistered");
        } catch (PlayerNotRegisteredException e) {
            response = Message.simpleMessage(null, MessageType.PLAYER_NOT_REGISTERED_ERROR);
            System.err.println("ERROR: " + e.getMessage());
        }

        sendMessage(player, response);
    }

    private void handleLogin(Message message) {
        //todo: handle lobby login
    }

    private void handleLogout(Message message) {
        //todo: handle lobby logout
    }

    private void notifyPlayer(Message message) {
        try {
            getPlayerByName(message.getAuthor()).onMessageReceived(message);
        } catch (PlayerNotRegisteredException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Player player, Message message) {
        try {
            player.sendMessage(message);
            System.out.println(("MESSAGE: message " + message.getType().toString() + " sent to Client \"" + player.getName() + "\""));
        } catch (ConnectionException e) {
            //System.err.println("ERROR: " + e.getClass() + ": " + e.nextMessage());
            e.printStackTrace();
        }
    }
}
