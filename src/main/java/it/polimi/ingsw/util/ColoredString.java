package it.polimi.ingsw.util;

public class ColoredString {
    private String mono;
    private String ansiColor;

    public ColoredString(String mono, String ansiColor) {
        this.mono = mono;
        this.ansiColor = ansiColor;
    }

    public String mono() {
        return mono;
    }

    public String color() {
        return ansiColor;
    }
}
