package it.polimi.ingsw.network.common.util.console;

/**
 * This interface offers a standardized way to instance an {@code executor} with the purpose of printing strings onto some sort
 * of command line environment. It's up to the specialized class to implement an OS-dependent approach to achieve such goal.
 */
interface ConsoleExecutor {
    /**
     * Clears the command line environment by presenting an empty window with all the text previously displayed being cleared.
     */
    void clear();

    /**
     * Writes a given input {@code String} onto the command line environment. At the end of this operation, no new line
     * sequence should be printed, as it's up to the {@link #ANSIPrintln(String)} method to perform such task.
     *
     * @param message the {@code String} message to be printed.
     */
    void ANSIPrint(String message);

    /**
     * Writes a given input {@code String} onto the command line environment. At the end of this operation, a new line
     * sequence should be printed in order to allow the subsequent call to start printing from a different line.
     *
     * @param message the {@code String} message to be printed.
     */
    void ANSIPrintln(String message);
}
