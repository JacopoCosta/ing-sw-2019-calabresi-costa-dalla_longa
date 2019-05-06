package it.polimi.ingsw.network.client.networkInterface;

import it.polimi.ingsw.network.client.networkInterface.rmiInterface.RmiNetworkInterface;
import it.polimi.ingsw.network.client.networkInterface.socketInterface.SocketNetworkInterface;
import it.polimi.ingsw.network.common.exceptions.*;

import java.util.Map;

public class NetworkHandler {
    public static final int RMI_NETWORK_INTERFACE = 1;
    public static final int SOCKET_NETWORK_INTERFACE = 2;

    private final NetworkInterface networkInterface;

    public NetworkHandler(String ipAddress, int port, int networkInterface) throws NetworkInterfaceConfigurationException {
        switch (networkInterface) {
            case NetworkHandler.RMI_NETWORK_INTERFACE:
                this.networkInterface = new RmiNetworkInterface(ipAddress);
                break;
            case NetworkHandler.SOCKET_NETWORK_INTERFACE:
            default:
                this.networkInterface = new SocketNetworkInterface(ipAddress, port);
        }
    }

    public void register(String username)
            throws ConnectionLostException, ServerRegistrationFailedException, UserAlreadyAddedException {
        networkInterface.register(username);
    }

    public void initLobby(String lobbyName, String lobbyPassword, String username)
            throws ConnectionLostException, LobbyCreationFailedException, LobbyLoginFailedException,
            LobbyAlreadyExistsException, LobbyNotFoundException, InvalidPasswordException, UserAlreadyAddedException,
            LobbyFullException, UserNotFoundException {
        networkInterface.newLobby(lobbyName, lobbyPassword);
        login(lobbyName, username, lobbyPassword);
    }

    public void login(String lobbyName, String username, String lobbyPassword)
            throws ConnectionLostException, LobbyLoginFailedException, LobbyNotFoundException,
            InvalidPasswordException, UserNotFoundException, LobbyFullException, UserAlreadyAddedException {
        networkInterface.login(lobbyName, username, lobbyPassword);
    }

    public void logout(String lobbyName, String username)
            throws ConnectionLostException, LobbyLogoutFailedException, LobbyNotFoundException,
            UserNotFoundException, EmptyLobbyException {
        networkInterface.logout(lobbyName, username);
    }

    public void unregister(String username)
            throws ServerUnregisteringFailedException, UserNotFoundException, ConnectionLostException {
        networkInterface.unregister(username);
    }

    public Map<String, String> getLobbies() throws ConnectionLostException {
        return networkInterface.getLobbies();
    }
}
