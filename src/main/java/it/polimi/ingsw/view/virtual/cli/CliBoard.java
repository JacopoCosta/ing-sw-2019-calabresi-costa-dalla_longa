package it.polimi.ingsw.view.virtual.cli;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.util.printer.Color;
import it.polimi.ingsw.util.printer.ColoredString;
import it.polimi.ingsw.view.remote.cli.CLI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static it.polimi.ingsw.util.UTF.*;
import static it.polimi.ingsw.view.virtual.cli.CliCommon.canvas;

/**
 * This class is responsible of depicting the {@link Board} on the {@link CLI} interface.
 */
public abstract class CliBoard {
    /**
     * The top margin of the top-left corner of this area.
     */
    private static final int top = 3;

    /**
     * The left margin of the top-left corner of this area.
     */
    private static final int left = 0;

    /**
     * The width, in characters, of each cell.
     */
    private static final int cellWidth = 25;

    /**
     * The height, in characters, of each cell.
     */
    private static final int cellHeight = 11;

    /**
     * The width, in characters, of the gap left by a door.
     */
    private static final int doorWidth = 9;

    /**
     * The height, in characters, of the gap left by a door.
     */
    private static final int doorHeight = 3;

    /**
     * Adds the board's area to the {@link CliCommon}'s grid.
     * @param board the {@link Board} to depict.
     */
    public static void build(Board board) {
        for(Cell cell : board.getCells())
            writeCell(cell);
    }

    /**
     * Writes a single {@link Cell} on the grid.
     * @param cell the cell.
     */
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

        List<ColoredString> cellName = Collections.singletonList(new ColoredString("Cell " + cell.getId(), null));
        CliCommon.write(top + cellHeight * cellY + 1, 2 + cellWidth * cellX, cellName);

        if(cell.isSpawnPoint()) {
            List<Weapon> weapons = ((SpawnCell) cell).getWeaponShop();
            int row = 2;
            for(Weapon weapon : weapons) {
                CliCommon.write(top + cellHeight * cellY + row, 2 + cellWidth * cellX, weapon.toColoredStrings());
                row ++;
            }
        }
        else {
            AmmoTile ammoTile = ((AmmoCell) cell).getAmmoTile();
            if(ammoTile != null) {
                List<ColoredString> ammo = ammoTile.toColoredStrings();
                CliCommon.write(top + cellHeight * cellY + 2, 2 + cellWidth * cellX, ammo);
            }
        }

        int row = cellHeight - 2;
        for(Player p : cell.getPlayers()) {
            String playerAnsiColor = CliCommon.toAnsiColor(p);
            List<ColoredString> playerToken = new ArrayList<>();
            playerToken.add(new ColoredString(block + " ", playerAnsiColor));
            playerToken.add(new ColoredString(CliCommon.nameOf(p), Color.RESET));
            CliCommon.write(top + cellHeight * cellY + row, 2 + cellWidth * cellX, playerToken);
            row --;
        }
    }

    /**
     * The possible types of walls separating two {@link Cell}s.
     */
    private enum WallType {
        /**
         * Separating two {@link Cell}s that are not adjacent.
         */
        FULL,

        /**
         * Separating two adjacent {@link Cell}s belonging to different {@link Room}s.
         */
        DOOR,

        /**
         * Separating two adjacent {@link Cell}s in the same {@link Room}.
         */
        OPEN
    }

    /**
     * Returns the type of wall separating a {@link Cell} from its neighbour.
     * @param cell the cell.
     * @param neighbour its neighbouring cell.
     * @return the appropriate {@link WallType}.
     */
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

    /**
     * Builds the border of a {@link Cell}, minding the surroundings.
     * @param cellX the {@link Cell}'s horizontal position.
     * @param cellY the {@link Cell}'s vertical position.
     * @param cornerId a number (1~4) identifying one of the corners. They are numbered in ascending order
     *                 starting at the bottom-left corner and proceeding counter-clockwise.
     * @param type the wall found next to the specified corner.
     * @param color the {@code ANSI} escape for the {@link Cell}'s colour.
     */
    private static void buildWallCounterclockwise(int cellX, int cellY, int cornerId, WallType type, String color) {
        int i = top + cellY * cellHeight + (cornerId == 1 || cornerId == 2 ? cellHeight - 1 : 0);
        int j = left + cellX * cellWidth + (cornerId == 2 || cornerId == 3 ? cellWidth - 1 : 0);

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

        canvas[i][j] = new ColoredString(startingCorner, ansiColor);
        for(int k = 1; k < limit - 1; k ++) {
            i += di;
            j += dj;

            switch(type) {
                case FULL:
                    canvas[i][j] = new ColoredString(full, ansiColor);
                    break;

                case DOOR:
                    int distanceFromMiddle = Math.abs(k - middle);
                    if(distanceFromMiddle == doorHalfSize)
                        canvas[i][j] = new ColoredString(k < middle ? gapOpen : gapClose, ansiColor);
                    else if(distanceFromMiddle > doorHalfSize)
                        canvas[i][j] = new ColoredString(full, ansiColor);
                    break;

                case OPEN:
                    if(k == 1)
                        canvas[i][j] = new ColoredString(gapOpen, ansiColor);
                    else if(k == limit - 2)
                        canvas[i][j] = new ColoredString(gapClose, ansiColor);
            }
        }
    }
}
