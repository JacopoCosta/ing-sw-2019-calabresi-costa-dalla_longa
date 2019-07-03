package it.polimi.ingsw.view.remote.cli;

import it.polimi.ingsw.util.console.Console;
import it.polimi.ingsw.util.ColoredString;

import java.util.ArrayList;
import java.util.List;

public abstract class ConsoleOptimizer {
    private static Console console = Console.getInstance();

    public static void print(ColoredString[][] grid) {
        printAccumulatedLines(accumulateLines(grid));
    }

    private static List<List<ColoredString>> accumulateLines(ColoredString[][] grid) {
        List<List<ColoredString>> lines = new ArrayList<>();

        for(ColoredString[] row : grid) {
            List<ColoredString> line = new ArrayList<>();
            StringBuilder accumulator = new StringBuilder();
            String lastColor = null;
            boolean firstColor = true;

            for(ColoredString cs : row) {
                if(cs == null) {
                    accumulator.append(" ");
                }
                else if(cs.color() == null || cs.color().equals(lastColor)) {
                    accumulator.append(cs.content());
                }
                else {
                    if(firstColor) {
                        accumulator.append(cs.content());
                        lastColor = cs.color();
                        firstColor = false;
                    }
                    else {
                        if (accumulator.length() > 0) {
                            line.add(new ColoredString(accumulator.toString(), lastColor));
                        }
                        accumulator = new StringBuilder();
                        accumulator.append(cs.content());
                        lastColor = cs.color();
                    }
                }
            }

            if(accumulator.length() > 0) {
                line.add(new ColoredString(accumulator.toString(), lastColor));
            }

            lines.add(line);
        }

        return lines;
    }

    private static void printAccumulatedLines(List<List<ColoredString>> lines) {
        int tally = 0;
        for(List<ColoredString> line : lines) {
            for(ColoredString cs : line) {
                tally ++;
                console.ANSIPrint(cs.color() + cs.content());
            }
            console.tinyPrintln("");
        }
        System.out.println("called ANSIprint " + tally + " times");
    }
}
