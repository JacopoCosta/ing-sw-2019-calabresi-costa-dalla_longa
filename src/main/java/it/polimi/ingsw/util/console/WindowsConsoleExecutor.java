package it.polimi.ingsw.util.console;

import java.io.IOException;

/**
 * This is a concrete implementation of the {@link ConsoleExecutor} interface. Its only purpose is to interface with
 * the Microsoft Windows command prompt (CMD) and printOpponents any given input onto that specific environment.
 *
 * @see ConsoleExecutor
 */
class WindowsConsoleExecutor implements ConsoleExecutor {
    private final ProcessBuilder pb;

    WindowsConsoleExecutor() {
        this.pb = new ProcessBuilder();
    }

    /**
     * Executes a given {@code command} onto the Microsoft Windows command prompt (CMD). Each call of this method is
     * performed individually by a separate thread.
     *
     * @param command the directive to be executed.
     */
    private void execute(String command){
        /*try {
            this.pb.command("powershell.exe", "-Command", command).inheritIO().start().waitFor();
        } catch (IOException | InterruptedException ignored) {
        }*/
    }

    /**
     * Clears the Windows command prompt by executing the appropriate command.
     */
    @Override
    public void clear() {
        this.execute("clear");
    }

    /**
     * Writes the given {@code ansiMessage} to the Windows command prompt, then no newline sequence is printed.
     * This method supports ANSI escape characters.
     *
     * <p>Note that calling {@code ANSIPrint("message\n")} does NOT behaves the same as {@code ANSIPrintln("message")}
     * as this method does not support regular escape characters and every given one will be ignored and printed as plain text.
     *
     * @param ansiMessage the ANSI message to be printed.
     */
    @Override
    public void ANSIPrint(String ansiMessage) {
        this.execute("write-host \"" + ansiMessage + "\" -nonewline");
    }
}
