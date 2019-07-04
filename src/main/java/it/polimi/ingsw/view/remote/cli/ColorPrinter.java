package it.polimi.ingsw.view.remote.cli;

import it.polimi.ingsw.util.ColoredString;

import java.util.ArrayList;
import java.util.List;

public abstract class ColorPrinter {

    public static void print(String string) {
        System.out.println(string);
    }

    public static void print(ColoredString[][] grid) {
        printAccumulatedLines(accumulateLines(grid));
    }

    private static List<String> accumulateLines(ColoredString[][] grid) {
        List<String> lines = new ArrayList<>();

        for(ColoredString[] row : grid) {
            StringBuilder line = new StringBuilder();
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
                        accumulator.append(cs.color()).append(cs.content());
                        lastColor = cs.color();
                        firstColor = false;
                    }
                    else {
                        if (accumulator.length() > 0) {
                            line.append(accumulator.toString());
                        }
                        accumulator = new StringBuilder();
                        accumulator.append(cs.color()).append(cs.content());
                        lastColor = cs.color();
                    }
                }
            }

            if(accumulator.length() > 0) {
                line.append(accumulator.toString());
            }

            lines.add(line.toString());
        }

        return lines;
    }

    private static void printAccumulatedLines(List<String> lines) {
        lines.forEach(System.out::println);
    }
}
