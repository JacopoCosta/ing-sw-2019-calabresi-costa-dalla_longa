package it.polimi.ingsw.network.common.util.console;

/**
 * This is an utility class used to print different type of {@code String} based information onto a command line console.
 * These operations are done independently from the OS environment this class is invoked from and guarantee an equal result
 * for each one of them.
 */
public class Console {
    /**
     * The Operative System this {@code Console} is running on.
     */
    private final String osName;

    /**
     * The {@link ConsoleExecutor} responsible for the actual writing operation on the appropriate console.
     */
    private final ConsoleExecutor executor;

    /**
     * The {@code Console} {@code instance} shared between the different consumers.
     */
    private static Console instance = null;

    /**
     * This is the only constructor. It creates a new {@code Console} instance to run on the underlying Operative System.
     */
    private Console() {
        osName = System.getProperty("os.name").toLowerCase();

        if (isWindowsOS())
            executor = new WindowsConsoleExecutor();
        else if (isUnixOS() || isMacOS())
            executor = new UnixConsoleExecutor();
        else
            throw new RuntimeException("invalid OS: unable to execute console commands");
    }

    /**
     * Returns a unique {@code Console} instance for all the consumers of this class.
     *
     * @return the unique {@code Console} instance.
     */
    public static Console getInstance() {
        if (instance == null)
            return new Console();
        return instance;
    }

    /**
     * Determines whether or not the underlying environment is some distribution of the Microsoft Windows Operative System.
     *
     * @return {@code true} if and only if the underlying environment is some distribution of the Microsoft Windows
     * Operative System, {@code false} otherwise.
     */
    private boolean isWindowsOS() {
        return (osName.contains("win"));
    }

    /**
     * Determines whether or not the underlying environment is some distribution of the Linux Operative System, or an Unix
     * environment excluding Mac OS.
     *
     * @return {@code true} if and only if the underlying environment is some distribution of the
     * Linux Operative System, or a Unix environment excluding Mac OS, {@code false} otherwise.
     */
    private boolean isUnixOS() {
        return (osName.contains("nix") || osName.contains("nux") || osName.indexOf("aix") > 0);
    }

    /**
     * Determines whether or not the underlying environment is some distribution of the Mac OS Operative System.
     *
     * @return {@code true} if and only if the underlying environment is some distribution of the Mac OS Operative
     * System, {@code false} otherwise.
     */
    private boolean isMacOS() {
        return (osName.contains("mac"));
    }

    /**
     * Returns the name of the Operative System this console instance is running on.
     *
     * @return the name of the Operative System this console instance is running on.
     */
    public String getOsName() {
        return osName;
    }

    /**
     * Writes the given {@code message} on the underlying command line environment on a single line, including escape
     * characters without a new line sequence at the end.
     * Does not support ANSI escape characters.
     *
     * @param message the {@code String} value to be printed.
     */
    public synchronized void tinyPrint(String message) {
        System.out.print(message);
    }

    /**
     * Writes the given {@code message} on the underlying command line environment on a single line, including escape
     * characters and then a new line sequence at the end.
     * Does not support ANSI escape characters.
     *
     * @param message the {@code String} value to be printed.
     */
    public synchronized void tinyPrintln(String message) {
        System.out.println(message);
    }

    /**
     * Writes the given {@code message} on the underlying command line environment on a single line, ignoring escape
     * characters without a new line at the end. This method supports ANSI escape characters.
     *
     * <p>Note that calling {@code ANSIPrint("message\n")} does NOT behaves the same as {@code ANSIPrintln("message")}
     * as this method does not support regular escape characters and every given one will be ignored and printed as plain text.
     *
     * @param message the {@code String} value to be printed.
     * @see #ANSIPrintln(String)
     */
    public synchronized void ANSIPrint(String message) {
        executor.ANSIPrint(message);
    }

    /**
     * Writes the given {@code message} on the underlying command line environment on a single line, ignoring escape
     * characters and then a new line sequence at the end. This method supports ANSI escape characters.
     *
     * <p>Note that calling {@code ANSIPrint("message\n")} does NOT behaves the same as {@code ANSIPrintln("message")}
     * as this method does not support regular escape characters and every given one will be ignored and printed as plain text.
     *
     * @param message the {@code String} value to be printed.
     * @see #ANSIPrint(String)
     */
    public synchronized void ANSIPrintln(String message) {
        executor.ANSIPrintln(message);
    }

    /**
     * Prints the given {@code message} in a cyan color and after a default prefix: {@code [MESSAGE]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public void mexS(String message) {
        ANSIPrintln(Color.ANSI_CYAN + "[MESSAGE] " + message + Color.ANSI_RESET);
    }

    /**
     * Prints the given {@code message} in a yellow color and after a default prefix: {@code [MESSAGE]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public void mexC(String message) {
        ANSIPrintln(Color.ANSI_YELLOW + "[MESSAGE] " + message + Color.ANSI_RESET);
    }

    /**
     * Prints the given {@code message} in the default console color and after a default prefix: {@code [LOG]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public void log(String message) {
        ANSIPrintln("[LOG] " + message);
    }

    /**
     * Prints the given {@code message} in a green color and after a default prefix: {@code [STATUS]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public void stat(String message) {
        ANSIPrintln(Color.ANSI_GREEN + "[STATUS] " + message + Color.ANSI_RESET);
    }

    /**
     * Prints the given {@code message} in a red color and after a default prefix: {@code [ERROR]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public void err(String message) {
        ANSIPrintln(Color.ANSI_RED + "[ERROR] " + message + Color.ANSI_RESET);
    }

    /**
     * Prints the given {@code message} in the default console color and after a default prefix: {@code [GAME]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public void mexG(String message) {
        tinyPrintln("[GAME] " + message);
    }

    /**
     * Clears the underlying command line environment by presenting an empty window with all the text previously displayed
     * being cleared.
     */
    public synchronized void clear() {
        executor.clear();
    }
}
