package it.polimi.ingsw.network.common.util;

public class ConsoleController {
    private final ConsoleExecutor executor;

    public ConsoleController() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (isWindowsOS(osName))
            executor = new WindowsConsoleExecutor();
        else if (isUnixOS(osName) || isMacOS(osName))
            executor = new UnixConsoleExecutor();
        else
            throw new RuntimeException("invalid OS: unable to execute console commands");
    }

    private boolean isWindowsOS(String osName) {
        return (osName.contains("win"));
    }

    private boolean isUnixOS(String osName) {
        return (osName.contains("nix") || osName.contains("nux") || osName.indexOf("aix") > 0);
    }

    private boolean isMacOS(String osName) {
        return (osName.contains("mac"));
    }

    public void startRmiRegistry() {
        executor.startRmiRegistry();
    }

    public void stopRmiRegistry() {
        executor.stopRmiRegistry();
    }

    public void clearConsole() {
        executor.clearConsole();
    }

    public static void out(String message) {
        System.out.print(message);
    }

    public static void mex(String message) {
        System.out.println("MESSAGE: " + message);
    }

    public static void log(String message) {
        System.out.println("LOG: " + message);
    }

    public static void stat(String message) {
        System.out.println("STATUS: " + message);
    }

    public static void err(String message) {
        System.err.println("ERROR: " + message);
    }
}
