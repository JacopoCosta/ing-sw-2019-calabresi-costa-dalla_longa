package it.polimi.ingsw.network.client.communicationInterface.socket;

import it.polimi.ingsw.network.client.communicationInterface.CommunicationInterface;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotFoundException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.socket.SocketMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SocketCommunicationInterface implements CommunicationInterface {
    private final PrintWriter out;
    private final Scanner in;


    public SocketCommunicationInterface(String hostAddress, int port) throws IOException, NoSuchElementException {
        Socket socket = new Socket(hostAddress, port);

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new Scanner(socket.getInputStream());
    }

    @Override
    public void register(String username) throws NoSuchElementException, ConnectionException, ClientAlreadyRegisteredException {
        out.println(SocketMessage.REGISTER_CLIENT_REQUEST);
        out.println(username);

        SocketMessage response = SocketMessage.valueOf(in.nextLine());

        switch (response) {
            case CLIENT_ALREADY_REGISTERED_ERROR:
                throw new ClientAlreadyRegisteredException("Client \"" + username + "\" already registered");
            case REGISTRATION_FAILED_ERROR:
                throw new ConnectionException("Connection error to the server");
            case REGISTRATION_SUCCESS:
        }
    }

    @Override
    public void unregister(String username) throws NoSuchElementException, ConnectionException, ClientNotFoundException {
        out.println(SocketMessage.UNREGISTER_CLIENT_REQUEST);

        SocketMessage response = SocketMessage.valueOf(in.nextLine());

        switch (response) {
            case CLIENT_NOT_FOUND_ERROR:
                throw new ClientNotFoundException("Client \"" + username + "\" not found");
            case UNREGISTERING_FAILED_ERROR:
                throw new ConnectionException("Connection error to the server");
            case UNREGISTERING_SUCCESS:
        }
    }
}
