package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.view.remote.BoardGraph;
import it.polimi.ingsw.view.remote.ContentType;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

public class TestBoardGraph {

    //FIXME: this test has to be moved, as now it's a model test

    private Game game = Game.create(false, 6, 1, new ArrayList<>());
    private Board board = game.getBoard();

    @Test
    public void simpleSeparatorTest() {
        assertEquals(ContentType.VER_VOID, BoardGraph.getWallBetweenCells(board, 1, 0, 2, 0));
        assertEquals(ContentType.HOR_FULL, BoardGraph.getWallBetweenCells(board, 1, 0, 1, 1));
        assertEquals(ContentType.HOR_DOOR, BoardGraph.getWallBetweenCells(board, 0, 0, 0, 1));
    }

    @Test
    public void withVoidCells() {
        assertEquals(ContentType.HOR_FULL, BoardGraph.getWallBetweenCells(board, 3, 0, 3, 1));
        assertEquals(ContentType.HOR_FULL, BoardGraph.getWallBetweenCells(board, 3, 1, 3, 0));
        assertEquals(ContentType.VER_FULL, BoardGraph.getWallBetweenCells(board, 3, 0, 2, 0));
        assertEquals(ContentType.VER_FULL, BoardGraph.getWallBetweenCells(board, 2, 0, 3, 0));

        assertEquals(ContentType.HOR_FULL, BoardGraph.getWallBetweenCells(board, 0, 2, 0, 1));
        assertEquals(ContentType.HOR_FULL, BoardGraph.getWallBetweenCells(board, 0, 1, 0, 2));
        assertEquals(ContentType.VER_FULL, BoardGraph.getWallBetweenCells(board, 0, 2, 1, 2));
        assertEquals(ContentType.VER_FULL, BoardGraph.getWallBetweenCells(board, 1, 2, 0, 2));
    }

    @Test
    public void noneWallType() {
        assertEquals(ContentType.NONE, BoardGraph.getWallBetweenCells(board, 1, 0, 2, 1));
        assertEquals(ContentType.NONE, BoardGraph.getWallBetweenCells(board, 2, 1, 1, 0));
        assertEquals(ContentType.NONE, BoardGraph.getWallBetweenCells(board, 3, 0, 1, 2));
        assertEquals(ContentType.NONE, BoardGraph.getWallBetweenCells(board, 1, 2, 3, 0));
        assertEquals(ContentType.NONE, BoardGraph.getWallBetweenCells(board, 0, 0, 0, 2));
        assertEquals(ContentType.NONE, BoardGraph.getWallBetweenCells(board, 0, 2, 0, 0));
    }

}
