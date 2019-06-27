package it.polimi.ingsw.network.common.util.console;

import java.io.IOException;

/**
 * This is a concrete implementation of the {@link ConsoleExecutor} interface. Its only purpose is to interface with
 * the Microsoft Windows command prompt (CMD) and print any given input onto that specific environment.
 *
 * @see ConsoleExecutor
 */
class WindowsConsoleExecutor implements ConsoleExecutor {

    /**
     * Executes a given {@code command} onto the Microsoft Windows command prompt (CMD). Each call of this method is
     * performed individually by a separate thread.
     *
     * @param command the directive to be executed.
     * @throws IOException          if an {@link IOException} is thrown at a lower level.
     * @throws InterruptedException if an {@link InterruptedException} is thrown at a lower level.
     */
    private void execute(String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);

        Process p = pb.inheritIO().start();
        p.waitFor();
    }

    /**
     * Clears the Windows command prompt by executing the appropriate command.
     */
    @Override
    public void clear() {
        try {
            execute("cls");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the given {@code ansiMessage} to the Windows command prompt, then no newline sequence is printed.
     * This method supports ANSI escape characters.
     *
     * <p>Note that calling {@code ANSIPrint("message\n")} does NOT behaves the same as {@code ANSIPrintln("message")}
     * as this method does not support regular escape characters and every given one will be ignored and printed as plain text.
     *
     * <p>Also note that ANSIPrint() method is more resource consuming and generally less efficient than its
     * counterpart {@link #ANSIPrintln(String)}.
     *
     * @param ansiMessage the ANSI message to be printed.
     * @see #ANSIPrintln(String)
     */
    @Override
    public void ANSIPrint(String ansiMessage) {
        try {
            execute("<nul set /p =\"" + ansiMessage + "\"");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the given {@code ansiMessage} to the Windows command prompt, then a newline sequence is printed.
     * This method supports ANSI escape characters.
     *
     * <p>Note that calling {@code ANSIPrint("message\n")} does NOT behaves the same as {@code ANSIPrintln("message")}
     * as this method does not support regular escape characters and every given one will be ignored and printed as plain text.
     *
     * @param ansiMessage the ANSI message to be printed.
     * @see #ANSIPrintln(String)
     */
    @Override
    public void ANSIPrintln(String ansiMessage) {
        try {
            execute("<nul set /p =\"" + ansiMessage + "\" & echo.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
