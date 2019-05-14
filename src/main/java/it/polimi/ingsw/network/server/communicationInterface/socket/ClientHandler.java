package it.polimi.ingsw.network.server.communicationInterface.socket;

import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.socket.SocketMessage;
import it.polimi.ingsw.network.server.CommunicationHub;
import it.polimi.ingsw.network.server.communicationInterface.Client;
import it.polimi.ingsw.network.server.communicationInterface.CommunicationInterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private CommunicationHub communicationHub;
    private Client client;

    private final Socket socket;
    private String clientAddress;
    private int port;

    private PrintWriter out;
    private Scanner in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    private void registerClient() throws ConnectionException {
        try {
            String username = in.nextLine();

            CommunicationInterface clientInterface = new SocketCommunicationInterface(out);
            client = new Client(username, clientInterface);
            communicationHub.register(client);

            out.println(SocketMessage.REGISTRATION_SUCCESS);
        } catch (NoSuchElementException e) {
            out.println(SocketMessage.REGISTRATION_FAILED_ERROR);
            throw new ConnectionException(e.getClass().toString() + " in Client " + clientAddress + ":" + port, e);
        } catch (ClientAlreadyRegisteredException ignored) {
            out.println(SocketMessage.CLIENT_ALREADY_REGISTERED_ERROR);
        }
    }

    private void unregisterClient() throws ConnectionException {
        try {
            communicationHub.unregister(client);
            out.println(SocketMessage.UNREGISTERING_SUCCESS);
        } catch (NoSuchElementException e) {
            out.println(SocketMessage.UNREGISTERING_FAILED_ERROR);
            throw new ConnectionException(e.getClass().toString() + " in Client " + clientAddress + ":" + port, e);
        } catch (ClientNotFoundException ignored) {
            out.println(SocketMessage.CLIENT_NOT_FOUND_ERROR);
        }
    }

    @Override
    public void run() {
        communicationHub = CommunicationHub.getInstance();

        clientAddress = socket.getInetAddress().getHostAddress();
        port = socket.getPort();
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new Scanner(socket.getInputStream());
        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            return;
        }

        try {
            SocketMessage message;
            do {
                message = SocketMessage.valueOf(in.nextLine());

                switch (message) {
                    case REGISTER_CLIENT_REQUEST:
                        try {
                            registerClient();
                        } catch (ConnectionException e) {
                            e.printStackTrace();
                            return;
                        }
                        break;
                    case UNREGISTER_CLIENT_REQUEST:
                    default:
                }
            } while (!message.equals(SocketMessage.UNREGISTER_CLIENT_REQUEST));
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } finally {
            try {
                unregisterClient();
            } catch (ConnectionException e) {
                e.printStackTrace();
            }

            in.close();
            out.close();
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
