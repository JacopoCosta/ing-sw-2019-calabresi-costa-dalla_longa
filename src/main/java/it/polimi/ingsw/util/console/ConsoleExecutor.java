package it.polimi.ingsw.util.console;

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
     * sequence is.
     *
     * @param message the {@code String} message to be printed.
     */
    void ANSIPrint(String message);
}
