package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.view.remote.BoardGraph;
import it.polimi.ingsw.view.remote.WallType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

public class TestBoardGraph {

    private Game game = Game.create(false, 6, 1, new ArrayList<>());
    private Board board = game.getBoard();

    private int x0 = board.getCells().get(0).getXCoord();
    private int x1 = board.getCells().get(1).getXCoord();
    private int x2 = board.getCells().get(2).getXCoord();
    private int x3 = board.getCells().get(3).getXCoord();
    private int x4 = board.getCells().get(4).getXCoord();
    private int x5 = board.getCells().get(5).getXCoord();

    private int y0 = board.getCells().get(0).getYCoord();
    private int y1 = board.getCells().get(1).getYCoord();
    private int y2 = board.getCells().get(2).getYCoord();
    private int y3 = board.getCells().get(3).getYCoord();
    private int y4 = board.getCells().get(4).getYCoord();
    private int y5 = board.getCells().get(5).getYCoord();

    @Test
    public void simpleSeparatorTest() {
        assertEquals(WallType.VER_VOID, BoardGraph.getWallBetweenCells(board, x1, y1, x2, y2));
        assertEquals(WallType.HOR_FULL, BoardGraph.getWallBetweenCells(board, x1, y1, x4, y4));
        assertEquals(WallType.HOR_DOOR, BoardGraph.getWallBetweenCells(board, x0, y0, x3, y3));
    }
}
