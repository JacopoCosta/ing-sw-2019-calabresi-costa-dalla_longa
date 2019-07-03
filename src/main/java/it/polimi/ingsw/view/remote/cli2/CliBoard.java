package it.polimi.ingsw.view.remote.cli2;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.ColoredString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;

public class CliBoard {
    private static final int cellWidth = 27;
    private static final int cellHeight = 13;
    
    private static final int doorWidth = 9;
    private static final int doorHeight = 5;

    private static ColoredString[][] grid;

    public static void print(Board board) {

        final int totalWidth = cellWidth * board.getWidth();
        final int totalHeight = cellHeight * board.getHeight();

        grid = new ColoredString[totalHeight][totalWidth];

        for(Cell cell : board.getCells())
            writeCell(cell);

        ConsoleOptimizer.print(grid);
    }

    private enum WallType {
        FULL,
        DOOR,
        OPEN
    }

    private static void writeCell(Cell cell) {
        int cellX = cell.getXCoord();
        int cellY = cell.getYCoord();

        Cell northNeighbour = cell.getBoard().getCellByCoordinates(cellX, cellY - 1);
        Cell eastNeighbour = cell.getBoard().getCellByCoordinates(cellX + 1, cellY);
        Cell southNeighbour = cell.getBoard().getCellByCoordinates(cellX, cellY + 1);
        Cell westNeighbour = cell.getBoard().getCellByCoordinates(cellX - 1, cellY);

        List<WallType> walls = Arrays.asList(getWall(cell, southNeighbour), getWall(cell, eastNeighbour),
                                             getWall(cell, northNeighbour), getWall(cell, westNeighbour));

        for(int i = 1; i <= 4; i ++)
            buildWallCounterclockwise(cellX, cellY, i, walls.get(i - 1), cell.getRoom().getColor());

        List<ColoredString> cellName = Arrays.asList(new ColoredString("Cell " + cell.getId(), null));
        writeOnCell(cellX, cellY, 1, cellName);

        if(cell.isSpawnPoint()) {
            List<Weapon> weapons = ((SpawnCell) cell).getWeaponShop();
            int row = 2;
            for(Weapon weapon : weapons) {
                writeOnCell(cellX, cellY, row, weapon.toColoredStrings());
                row ++;
            }
        }
        else {
            AmmoTile ammoTile = ((AmmoCell) cell).getAmmoTile();
            if(ammoTile != null) {
                List<ColoredString> ammo = ammoTile.toColoredStrings();
                writeOnCell(cellX, cellY, 2, ammo);
            }
        }

        int row = cellHeight - 3;
        for(Player p : cell.getPlayers()) {
            String playerAnsiColor = CliCommon.toAnsiColor(p);
            List<ColoredString> playerToken = new ArrayList<>();
            playerToken.add(new ColoredString(block + " ", playerAnsiColor));
            playerToken.add(new ColoredString(CliCommon.nameOf(p), Color.ANSI_RESET));
            writeOnCell(cellX, cellY, row, playerToken);
            row --;
        }
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

    private static void writeOnCell(int cellX, int cellY, int row, List<ColoredString> coloredStrings) {
        int caret = 0;

        for(ColoredString cs : coloredStrings) {
            for (int i = 0; i < cs.content().length(); i++) {
                grid[cellY * cellHeight + row][cellX * cellWidth + 2 + caret] = new ColoredString(cs.content().substring(i, i + 1), cs.color());
                caret ++;
            }
        }
    }

    private static void buildWallCounterclockwise(int cellX, int cellY, int cornerId, WallType type, String color) {
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

        String ansiColor = Color.toAnsi(color);

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
}
