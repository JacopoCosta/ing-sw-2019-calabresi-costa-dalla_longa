package it.polimi.ingsw.network.common.util.console;

public class Console {
    private final String osName;
    private final ConsoleExecutor executor;

    private static Console instance = null;

    private Console() {
        osName = System.getProperty("os.name").toLowerCase();

        if (isWindowsOS())
            executor = new WindowsConsoleExecutor();
        else if (isUnixOS() || isMacOS())
            executor = new UnixConsoleExecutor();
        else
            throw new RuntimeException("invalid OS: unable to execute console commands");
    }

    public static Console getInstance() {
        if (instance == null)
            return new Console();
        return instance;
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

    //prints a single line including escape characters (\n, \t...) and then a new line at the end. Does not support ANSI escape characters
    public synchronized void tinyPrintln(String message) {
        System.out.println(message);
    }

    //prints a single line including escape characters (\n, \t...) without a new line at the end. Does not support ANSI escape characters
    public synchronized void tinyPrint(String message) {
        System.out.print(message);
    }

    //prints a single line ignoring escape characters (\n, \t...) without a new line at the end. Support ANSI escape characters
    public synchronized void ANSIPrint(String message) {
        executor.ANSIPrint(message);
    }

    //prints a single line ignoring escape characters (\n, \t...) and then a new line at the end. Supports ANSI escape characters
    public synchronized void ANSIPrintln(String message) {
        executor.ANSIPrintln(message);
    }

    public synchronized void mexS(String message) {
        ANSIPrintln(Color.ANSI_CYAN + "[MESSAGE] " + message + Color.ANSI_RESET);
    }

    public synchronized void mexC(String message) {
        ANSIPrintln(Color.ANSI_YELLOW + "[MESSAGE] " + message + Color.ANSI_RESET);
    }

    public synchronized void log(String message) {
        ANSIPrintln("[LOG] " + message);
    }

    public synchronized void stat(String message) {
        ANSIPrintln(Color.ANSI_GREEN + "[STATUS] " + message + Color.ANSI_RESET);
    }

    public synchronized void err(String message) {
        ANSIPrintln(Color.ANSI_RED + "[ERROR] " + message + Color.ANSI_RESET);
    }

    public synchronized void clear() {
        executor.clear();
    }
}
