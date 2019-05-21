package it.polimi.ingsw.network.common.util;

import it.polimi.ingsw.network.server.communication.rmi.ServerController;

interface ConsoleExecutor {
    String RMI_REGISTRY_EXECUTION_PATH = ServerController.class
            .getProtectionDomain()
            .getCodeSource()
            .getLocation()
            .toString()
            .substring(6)
            .replaceAll("%20", " ");

    void startRmiRegistry();

    void stopRmiRegistry();

    void clearConsole();
}
