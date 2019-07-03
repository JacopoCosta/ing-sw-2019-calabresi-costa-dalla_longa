package it.polimi.ingsw.util.console;

import java.io.IOException;

/**
 * This is a concrete implementation of the {@link ConsoleExecutor} interface. Its only purpose is to interface with
 * the Linux shell (or any Unix base command shells) and print any given input onto that specific environment.
 *
 * @see ConsoleExecutor
 */
class UnixConsoleExecutor implements ConsoleExecutor {

    /**
     * Executes a given {@code command} onto the Linux (or Unix based) shell. Each call of this method is
     * performed individually by a separate thread.
     *
     * @param command the directive to be executed.
     * @throws IOException          if an {@link IOException} is thrown at a lower level.
     * @throws InterruptedException if an {@link InterruptedException} is thrown at a lower level.
     */
    private void execute(String command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", command);
        pb.environment().put("TERM", "xterm");

        Process p = pb.inheritIO().start();
        p.waitFor();
    }

    /**
     * Clears the Linux (or Unix based) shell by executing the appropriate command.
     */

    @Override
    public void clear() {
        try {
            execute("clear");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the given {@code ansiMessage} to the Linux (or Unix based) shell, then no newline sequence is printed.
     * This method supports ANSI escape characters.
     *
     * <p>Note that calling {@code ANSIPrint("message\n")} does NOT behaves the same as {@code ANSIPrintln("message")}
     * as this method does not support regular escape characters and every given one will be ignored and printed as plain text.
     *
     * @param ansiMessage the ANSI message to be printed.
     */
    @Override
    public void ANSIPrint(String ansiMessage) {
        try {
            execute("echo -n \"" + ansiMessage.replace("\"", "\\\"") + "\"");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
