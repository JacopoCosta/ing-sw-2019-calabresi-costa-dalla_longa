package it.polimi.ingsw.network.common.util;

import java.io.IOException;

class WindowsConsoleExecutor implements ConsoleExecutor {

    private void execute(String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);

        Process p = pb.inheritIO().start();
        p.waitFor();
    }

    @Override
    public void clear() {
        try {
            execute("cls");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ANSIPrintln(String ansiMessage) {
        try {
            execute("echo. & <nul set /p =\"" + ansiMessage + "\"");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ANSIPrint(String ansiMessage) {
        try {
            execute("<nul set /p =\"" + ansiMessage + "\"");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
