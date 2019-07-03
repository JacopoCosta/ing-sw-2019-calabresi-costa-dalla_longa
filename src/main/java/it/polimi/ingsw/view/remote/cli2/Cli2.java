package it.polimi.ingsw.view.remote.cli2;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.util.ColoredString;
import it.polimi.ingsw.util.Table;
import it.polimi.ingsw.network.common.util.console.Color;
import it.polimi.ingsw.network.common.util.console.Console;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Cli2 {
    private static final Console console = Console.getInstance();

    private static final String vertical = "\u2551";
    private static final String horizontal = "\u2550";
    private static final String corner1 = "\u255a";
    private static final String corner2 = "\u255d";
    private static final String corner3 = "\u2557";
    private static final String corner4 = "\u2554";

    private static final int cellWidth = 27;
    private static final int cellHeight = 13;
    
    private static final int doorWidth = 9;
    private static final int doorHeight = 5;

    public static void main(String[] args) {
        printBoard(Game.create(false, 0, 2, new ArrayList<>()).getBoard());
    }

    private static void printBoard(Board board) {

        final int totalWidth = cellWidth * board.getWidth();
        final int totalHeight = cellHeight * board.getHeight();

        ColoredString[][] grid = new ColoredString[totalHeight][totalWidth];

        for(Cell cell : board.getCells())
            writeCell(grid, cell);

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
                    accumulator.append(cs.mono());
                }
                else {
                    if(firstColor) {
                        accumulator.append(cs.mono());
                        lastColor = cs.color();
                        firstColor = false;
                    }
                    else {
                        if (accumulator.length() > 0) {
                            line.add(new ColoredString(accumulator.toString(), lastColor));
                        }
                        accumulator = new StringBuilder();
                        accumulator.append(cs.mono());
                        lastColor = cs.color();
                    }
                }
            }

            if(accumulator.length() > 0) {
                line.add(new ColoredString(accumulator.toString(), lastColor));
            }

            lines.add(line);

            System.out.println(Table.list(line.stream()
                    .map(cs -> cs.mono().length())
                    .collect(Collectors.toList())));
        }

        return lines;
    }

    private static void printAccumulatedLines(List<List<ColoredString>> lines) {
        int tally = 0;
        for(List<ColoredString> line : lines) {
            for(ColoredString cs : line) {
                tally ++;
                console.ANSIPrint(cs.color(), cs.mono());
            }
            console.tinyPrintln("");
        }
        System.out.println("called ANSIprint " + tally + " times");
    }

    private enum WallType {
        FULL,
        DOOR,
        OPEN
    }

    private static void writeCell(ColoredString[][] grid, Cell cell) {
        int cellX = cell.getXCoord();
        int cellY = cell.getYCoord();

        Cell northNeighbour = cell.getBoard().getCellByCoordinates(cellX, cellY - 1);
        Cell eastNeighbour = cell.getBoard().getCellByCoordinates(cellX + 1, cellY);
        Cell southNeighbour = cell.getBoard().getCellByCoordinates(cellX, cellY + 1);
        Cell westNeighbour = cell.getBoard().getCellByCoordinates(cellX - 1, cellY);

        List<WallType> walls = Arrays.asList(getWall(cell, southNeighbour), getWall(cell, eastNeighbour),
                                             getWall(cell, northNeighbour), getWall(cell, westNeighbour));

        for(int i = 1; i <= 4; i ++)
            buildWallCounterclockwise(grid, cellX, cellY, i, walls.get(i - 1), cell.getRoom().getColor());

        writeOnCell(grid, cellX, cellY, 1, "Cell " + cell.getId(), null);
    }
    
    private static WallType getWall(Cell cell, Cell neighbour) {
        try {
            
            if (neighbour == null || !neighbour.isAdjacent(cell))
                return WallType.FULL;
            if (!neighbour.getRoom().equals(cell.getRoom()))
                return WallType.DOOR;
            return WallType.OPEN;
            
        } catch (Throwable e) {
            throw new NullPointerException();
        }
    }

    private static void writeOnCell(ColoredString[][] grid, int cellX, int cellY, int row, String string, String ansiColor) {
        if(string.length() > cellWidth - 4)
            string = string.substring(0, cellWidth - 7) + "...";

        for(int i = 0; i < string.length(); i ++)
            grid[cellY * cellHeight + row][cellX * cellWidth + 2 + i] = new ColoredString(string.substring(i, i + 1), ansiColor);
    }

    private static void buildWallCounterclockwise(ColoredString[][] grid, int cellX, int cellY, int cornerId, WallType type, String color) {
        int i = cellY * cellHeight + (cornerId == 1 || cornerId == 2 ? cellHeight - 1 : 0);
        int j = cellX * cellWidth + (cornerId == 2 || cornerId == 3 ? cellWidth - 1 : 0);

        String startingCorner = Arrays.asList(corner1, corner2, corner3, corner4).get(cornerId - 1);
        String full = cornerId % 2 == 0 ? vertical : horizontal;
        String gapOpen = Arrays.asList(corner3, corner4, corner1, corner2).get(cornerId - 1);
        String gapClose = Arrays.asList(corner4, corner1, corner2, corner3).get(cornerId - 1);

        int di = Arrays.asList(0, -1, 0, 1).get(cornerId - 1);
        int dj = Arrays.asList(1, 0, -1, 0).get(cornerId - 1);

        int limit = cornerId % 2 == 0 ? cellHeight : cellWidth;
        int middle = (limit - 1) / 2;
        int doorSize = cornerId % 2 == 0 ? doorHeight : doorWidth;
        int doorHalfSize = (doorSize + 1) / 2;

        String ansiColor = ansifyColor(color);

        grid[i][j] = new ColoredString(startingCorner, ansiColor);
        for(int k = 1; k < limit - 1; k ++) {
            i += di;
            j += dj;

            switch(type) {
                case FULL:
                    grid[i][j] = new ColoredString(full, ansiColor);
                    break;

                case DOOR:
                    int distanceFromMiddle = Math.abs(k - middle);
                    if(distanceFromMiddle == doorHalfSize)
                        grid[i][j] = new ColoredString(k < middle ? gapOpen : gapClose, ansiColor);
                    else if(distanceFromMiddle > doorHalfSize)
                        grid[i][j] = new ColoredString(full, ansiColor);
                    break;

                case OPEN:
                    if(k == 1)
                        grid[i][j] = new ColoredString(gapOpen, ansiColor);
                    else if(k == limit - 2)
                        grid[i][j] = new ColoredString(gapClose, ansiColor);
            }
        }
    }

    private static String ansifyColor(String color) {
        List<String> colors = Arrays.asList("white", "red", "yellow", "green", "blue", "purple");
        List<String> ansiColors = Arrays.asList(Color.ANSI_WHITE, Color.ANSI_RED, Color.ANSI_YELLOW, Color.ANSI_GREEN, Color.ANSI_BLUE, Color.ANSI_PURPLE);

        return ansiColors.get(colors.indexOf(color));
    }
}
