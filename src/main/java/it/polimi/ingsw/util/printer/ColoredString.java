package it.polimi.ingsw.util.printer;

import java.io.Serializable;

/**
 * A {@code ColoredString} is like a {@code String} but with a colour associated with it.
 */
public class ColoredString implements Serializable {
    /**
     * The readable string.
     */
    private String content;

    /**
     * The escape code of the {@code ANSI} colour associated to the string.
     */
    private String ansiColor;

    /**
     * This is the only constructor.
     * @param content the {@link #content}.
     * @param ansiColor the {@link #ansiColor}.
     */
    public ColoredString(String content, String ansiColor) {
        this.content = content;
        this.ansiColor = ansiColor;
    }

    /**
     * Gets the {@link #content} attribute.
     * @return the {@link #content}.
     */
    public String content() {
        return content;
    }

    /**
     * Gets the {@link #ansiColor} attribute.
     * @return the {@link #ansiColor}
     */
    public String color() {
        return ansiColor;
    }
}
