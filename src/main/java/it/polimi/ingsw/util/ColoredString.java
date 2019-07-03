package it.polimi.ingsw.util;

public class ColoredString {
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
