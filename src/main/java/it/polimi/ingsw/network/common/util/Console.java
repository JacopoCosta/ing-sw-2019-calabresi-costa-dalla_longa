package it.polimi.ingsw.network.common.util;

import java.io.IOException;

public class Console {
    private final String osName;
    private final ConsoleExecutor executor;

    public Console() {
        osName = System.getProperty("os.name").toLowerCase();

        if (isWindowsOS())
            executor = new WindowsConsoleExecutor();
        else if (isUnixOS() || isMacOS())
            executor = new UnixConsoleExecutor();
        else
            throw new RuntimeException("invalid OS: unable to execute console commands");
    }

    private boolean isWindowsOS() {
        return (osName.contains("win"));
    }

    private boolean isUnixOS() {
        return (osName.contains("nix") || osName.contains("nux") || osName.indexOf("aix") > 0);
    }

    private boolean isMacOS() {
        return (osName.contains("mac"));
    }

    public String getOsName() {
        return osName;
    }

    public void out(String message) {
        System.out.print(message);
    }

    public void mex(String message) {
        out("[MESSAGE] " + message + "\n");
    }

    public void log(String message) {
        out("[LOG] " + message + "\n");
    }

    public void stat(String message) {
        out("[STATUS] " + message + "\n");
    }

    public void err(String message) {
        System.err.println("[ERROR] " + message);
    }

    public void clear() {
        try {
            String result = executor.clear();

            if (result != null && !result.isEmpty())
                err(result);
        } catch (IOException | InterruptedException e) {
            err(e.getMessage());
        }
    }
}
