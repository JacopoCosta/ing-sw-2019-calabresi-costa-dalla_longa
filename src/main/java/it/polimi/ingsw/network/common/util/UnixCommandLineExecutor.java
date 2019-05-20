package it.polimi.ingsw.network.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class UnixCommandLineExecutor implements CommandLineExecutor {
    private final String START_RMI_REGISTRY = "cd " + RMI_REGISTRY_EXECUTION_PATH + "&&" + "rmiregistry &";
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
            e.printStackTrace();
        }
    }
}
