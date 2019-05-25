package it.polimi.ingsw.network.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class UnixConsoleExecutor implements ConsoleExecutor {

    private String RMI_COMMAND_BEFORE_ = "rmic it.polimi.ingsw.network.server.communication.rmi.ServerController";
    private final String START_RMI_REGISTRY = "cd " + RMI_REGISTRY_EXECUTION_PATH + "&&" + RMI_COMMAND_BEFORE_ + "&&" + "rmiregistry 65432 &";
    private final String STOP_RMI_REGISTRY = "pkill -f rmiregistry";
    private final String CLEAR_CONSOLE = "clear";

    @Override
    public void startRmiRegistry() {
        execute(START_RMI_REGISTRY);
    }

    @Override
    public void stopRmiRegistry() {
        execute(STOP_RMI_REGISTRY);
    }

    @Override
    public void clearConsole() {
        execute(CLEAR_CONSOLE);
    }

    private void execute(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "sh", "-c", command);
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process p = builder.start();
            try {
                p.waitFor();
            } catch (InterruptedException e) {
                System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) {
                    break;
                }
                System.err.println(line);
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());
        }
    }
}
