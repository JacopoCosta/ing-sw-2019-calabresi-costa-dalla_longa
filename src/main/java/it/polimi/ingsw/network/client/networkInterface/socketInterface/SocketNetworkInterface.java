package it.polimi.ingsw.network.client.networkInterface.socketInterface;

import it.polimi.ingsw.network.client.networkInterface.NetworkInterface;
import it.polimi.ingsw.network.common.exceptions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

/*
 * A SocketNetworkInterface communicates to the Server via the Socket protocol
 *
 * */

public class SocketNetworkInterface implements NetworkInterface {

    private final Scanner in;
    private final PrintWriter out;

    public SocketNetworkInterface(String ipAddress, int port) throws NetworkInterfaceConfigurationException {
        Socket socket;
        try {
            socket = new Socket(ipAddress, port);
        } catch (IOException e) {
            throw new NetworkInterfaceConfigurationException(e);
        }

        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            throw new NetworkInterfaceConfigurationException(e);
        }

        try {
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            try {
                in.close();
            } catch (Exception ignored) {
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
            throw new NetworkInterfaceConfigurationException(e);
        }
    }

    @Override
    public void register(String username) throws ConnectionLostException, ServerRegistrationFailedException, UserAlreadyAddedException {
        //socket not ready
    }

    @Override
    public void newLobby(String name, String password) throws ConnectionLostException, LobbyCreationFailedException, LobbyAlreadyExistsException {
        //socket not ready
    }

    @Override
    public void login(String lobbyName, String username, String password) throws ConnectionLostException, LobbyLoginFailedException, LobbyNotFoundException, InvalidPasswordException, LobbyFullException, UserAlreadyAddedException, UserNotFoundException {
        //socket not ready
    }

    @Override
    public void logout(String lobbyName, String username) throws ConnectionLostException, LobbyLogoutFailedException, LobbyNotFoundException, UserNotFoundException, EmptyLobbyException {
        //socket not ready
    }

    @Override
    public void unregister(String username) throws UserNotFoundException, ServerUnregisteringFailedException, ConnectionLostException {
        //socket not ready
    }

    @Override
    public Map<String, String> getLobbies() throws ConnectionLostException {
        return null; //socket not ready
    }
}
