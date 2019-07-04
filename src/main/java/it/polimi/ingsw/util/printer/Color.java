package it.polimi.ingsw.util.printer;

import java.util.Arrays;
import java.util.List;

/**
 * This class contains all the possible text colors available via the ANSI escape characters.
 */
public class Color {
    /**
     * The reset ANSI escape {@code String}.
     */
    public static final String RESET = "\u001B[0m";

    /**
     * The black text ANSI escape {@code String}.
     */
    public static final String BLACK = "\u001B[30m";

    /**
     * The red text ANSI escape {@code String}.
     */
    public static final String RED = "\u001B[31m";

    /**
     * The green text ANSI escape {@code String}.
     */
    public static final String GREEN = "\u001B[32m";

    /**
     * The yellow text ANSI escape {@code String}.
     */
    public static final String YELLOW = "\u001B[33m";

    /**
     * The blue text ANSI escape {@code String}.
     */
    public static final String BLUE = "\u001B[34m";

    /**
     * The purple text ANSI escape {@code String}.
     */
    public static final String PURPLE = "\u001B[35m";

    /**
     * The cyan text ANSI escape {@code String}.
     */
    public static final String CYAN = "\u001B[36m";

    /**
     * The white text ANSI escape {@code String}.
     */
    public static final String WHITE = "\u001B[37m";

    /**
     * Converts a color's name into an ANSI escape {@code String}.
     * @param color the color's name.
     * @return the ANSI escape.
     */
    public static String toAnsi(String color) {
        List<String> colors = Arrays.asList("white", "red", "yellow", "green", "blue", "purple");
        List<String> ansiColors = Arrays.asList(Color.WHITE, Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE);

        return ansiColors.get(colors.indexOf(color));
    }
}
