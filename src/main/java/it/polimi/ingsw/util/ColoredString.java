package it.polimi.ingsw.util;

import java.io.Serializable;

public class ColoredString implements Serializable {
    private String content;
    private String ansiColor;

    public ColoredString(String content, String ansiColor) {
        this.content = content;
        this.ansiColor = ansiColor;
    }

    public String content() {
        return content;
    }

    public String color() {
        return ansiColor;
    }
}
