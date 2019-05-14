package it.polimi.ingsw.network.server;

import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.server.communicationInterface.Client;

import java.rmi.RemoteException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CommunicationHub {
    private static CommunicationHub instance;

    private final Queue<Client> clients;

    private CommunicationHub() {
        clients = new ConcurrentLinkedQueue<>();
    }

    public static CommunicationHub getInstance() {
        if (instance == null)
            instance = new CommunicationHub();
        return instance;
    }

    public void register(Client client) throws ClientAlreadyRegisteredException {
        synchronized (clients) {
            if (!clients.contains(client)) {
                clients.add(client);
                return;
            }
            throw new ClientAlreadyRegisteredException("Client \"" + client.getName() + "\" already registered");
        }
    }

    public void unregister(Client client) throws ClientNotFoundException {
        synchronized (clients) {
            if (clients.contains(client)) {
                clients.remove(client);
                return;
            }
            throw new ClientNotFoundException("Client \"" + client.getName() + "\" not found");
        }
    }

    public void send(String playerName, String message) throws RemoteException, ClientNotFoundException {
        synchronized (clients) {
            if (playerName == null)
                throw new NullPointerException("Player is null");

            if (message == null)
                throw new NullPointerException("message is null");

            getClientByName(playerName).sendMessage(message);
        }
    }

    public Client getClientByName(String name) throws ClientNotFoundException {
        for (Client client : clients)
            if (client.getName().equals(name))
                return client;
        throw new ClientNotFoundException("Client \"" + name + "\" not found");
    }
}
