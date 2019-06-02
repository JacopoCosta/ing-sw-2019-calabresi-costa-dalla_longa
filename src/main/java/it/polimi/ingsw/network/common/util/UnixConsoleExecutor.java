package it.polimi.ingsw.network.common.util;

import java.io.IOException;

class UnixConsoleExecutor implements ConsoleExecutor {

    private void execute(String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
        pb.environment().put("TERM", "xterm");

        Process p = pb.inheritIO().start();
        p.waitFor();
    }

    @Override
    public void clear() {
        try {
            execute("clear");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ANSIPrintln(String ansiMessage) {
        try {
            execute("echo \"" + ansiMessage.replace("\"", "\\\"") + "\"");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ANSIPrint(String ansiMessage) {
        try {
            execute("echo -n \"" + ansiMessage.replace("\"", "\\\"") + "\"");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
