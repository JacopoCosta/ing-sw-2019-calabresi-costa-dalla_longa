package it.polimi.ingsw.network.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class WindowsConsoleExecutor implements ConsoleExecutor {

    private static String execute(String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        Process p = pb.inheritIO().start();
        p.waitFor();

        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = br.readLine()) != null)
            sb.append(s);
        return sb.toString();
    }

    @Override
    public void clear() {
        try {
            String result = execute("cls");

            if (!result.isEmpty())
                System.err.println(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ANSIPrintln(String ansiMessage) {
        try {
            String result = execute("echo " + ansiMessage);

            if (!result.isEmpty())
                System.err.println(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ANSIPrint(String ansiMessage) {
        try {
            String result = execute("<nul set /p =\"" + ansiMessage + "\"");

            if (!result.isEmpty())
                System.err.println(result);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
