package it.polimi.ingsw.network.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class WindowsConsoleExecutor implements ConsoleExecutor {
    private final String START_RMI_REGISTRY_COMMAND = "cd /d " + RMI_REGISTRY_EXECUTION_PATH + "&&" + "start rmiregistry";
    private final String STOP_RMI_REGISTRY_COMMAND = "taskkill /im rmiregistry.exe";
    private final String CLEAR_CONSOLE_COMMAND = "cls";

    @Override
    public void startRmiRegistry() {
        execute(START_RMI_REGISTRY_COMMAND);
    }

    @Override
    public void stopRmiRegistry() {
        execute(STOP_RMI_REGISTRY_COMMAND);
    }

    @Override
    public void clearConsole() {
        execute(CLEAR_CONSOLE_COMMAND);
    }

    private void execute(String command) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "cmd.exe", "/c", command);
            builder.redirectErrorStream(true);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            Process p = builder.start();
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
            ////e.printStackTrace(); //never thrown before
System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());; //never thrown before
System.err.println("ERROR: " + e.getClass() + ": " + e.getMessage());;
        }
    }
}
