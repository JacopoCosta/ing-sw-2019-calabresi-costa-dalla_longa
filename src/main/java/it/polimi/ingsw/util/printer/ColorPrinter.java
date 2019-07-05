package it.polimi.ingsw.util.printer;

import it.polimi.ingsw.view.remote.cli.CLI;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a collection of console-like methods to print formatted and coloured messages, and the {@link CLI} interface.
 */
public abstract class ColorPrinter {

    /**
     * Prints a message on a new line.
     * @param string the message.
     */
    public static void println(String string) {
        System.out.println(string);
    }

    /**
     * Prints an inline message.
     * @param line the message.
     */
    public static void print(String line) {
        System.out.print(line);
    }

    /**
     * Prints a grid of {@link ColoredString}s.
     * @param grid the grid.
     */
    public static void print(ColoredString[][] grid) {
        printAccumulatedLines(accumulateLines(grid));
    }

    /**
     * This method is used to convert 2-dimensional grid of {@link ColoredString}s into a list of {@code String}s.
     * This is to improve performance and minimize system calls.
     * @param grid the grid.
     * @return the list of {@code String}s.
     */
    private static List<String> accumulateLines(ColoredString[][] grid) {
        List<String> lines = new ArrayList<>();

        for (ColoredString[] row : grid) {
            StringBuilder line = new StringBuilder();
            StringBuilder accumulator = new StringBuilder();
            String lastColor = null;
            boolean firstColor = true;

            for (ColoredString cs : row) {
                if (cs == null) {
                    accumulator.append(" ");
                } else if (cs.color() == null || cs.color().equals(lastColor)) {
                    accumulator.append(cs.content());
                } else {
                    if (firstColor) {
                        accumulator.append(cs.color()).append(cs.content());
                        lastColor = cs.color();
                        firstColor = false;
                    } else {
                        if (accumulator.length() > 0) {
                            line.append(accumulator.toString());
                        }
                        accumulator = new StringBuilder();
                        accumulator.append(cs.color()).append(cs.content());
                        lastColor = cs.color();
                    }
                }
            }

            if (accumulator.length() > 0) {
                line.append(accumulator.toString());
            }

            lines.add(line.toString());
        }

        return lines;
    }

    /**
     * Prints a series of messages, each on its line.
     * @param lines the messages.
     */
    private static void printAccumulatedLines(List<String> lines) {
        lines.forEach(System.out::println);
    }

    /**
     * Clears the underlying command line environment by presenting an empty window with all the text previously displayed
     * being cleared.
     */
    public static void clear() {
        final String ANSI_CLS = "\u001b[2J";
        final String ANSI_HOME = "\u001b[H";

        print(ANSI_CLS + ANSI_HOME);
        System.out.flush();
    }

    /**
     * Prints the given {@code message} in a red color and after a default prefix: {@code [ERROR]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public static void err(String message) {
        println(Color.RED + "[ERROR] " + message + Color.RESET);
    }

    /**
     * Prints the given {@code message} in a green color and after a default prefix: {@code [STATUS]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public static void stat(String message) {
        println(Color.GREEN + "[STATUS] " + message + Color.RESET);
    }

    /**
     * Prints the given {@code message} in the default console color and after a default prefix: {@code [GAME]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public static void mexG(String message) {
        println("[GAME] " + message);
    }

    /**
     * Prints the given {@code message} in a cyan color and after a default prefix: {@code [MESSAGE]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public static void mexS(String message) {
        println(Color.CYAN + "[MESSAGE] " + message + Color.RESET);
    }

    /**
     * Prints the given {@code message} in a yellow color and after a default prefix: {@code [MESSAGE]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public static void mexC(String message) {
        println(Color.YELLOW + "[MESSAGE] " + message + Color.RESET);
    }

    /**
     * Prints the given {@code message} in the default console color and after a default prefix: {@code [LOG]}.
     *
     * @param message the {@code String} value to be printed.
     */
    public static void log(String message) {
        println("[LOG] " + message);
    }
}
