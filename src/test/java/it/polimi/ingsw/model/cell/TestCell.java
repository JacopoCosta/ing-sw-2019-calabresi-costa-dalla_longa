package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.exceptions.DistanceFromNullException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TestCell {
    private Cell cell1;
    private Cell cell2;
    private Cell cell3;
    private Cell cell4;
    private Cell cell5;
    private Cell cell6;
    private Cell cell7;
    private Cell cell8;
    private Cell cell9;
    private Cell cell10;
    private Cell cell11;

    @Before
    public void setUp() {
        Game game = Game.create(false, 1, 2, new ArrayList<>());
        Board board = game.getBoard();
        cell1 = board.getCells().get(0);
        cell2 = board.getCells().get(1);
        cell3 = board.getCells().get(2);
        cell4 = board.getCells().get(3);
        cell5 = board.getCells().get(4);
        cell6 = board.getCells().get(5);
        cell7 = board.getCells().get(6);
        cell8 = board.getCells().get(7);
        cell9 = board.getCells().get(8);
        cell10 = board.getCells().get(9);
        cell11 = board.getCells().get(10);
    }


    @Test
    public void distance() throws DistanceFromNullException {
        assertEquals(0, cell8.distance(cell8));
        assertEquals(1, cell8.distance(cell7));
        assertEquals(1, cell4.distance(cell8));
        assertEquals(2, cell8.distance(cell10));
        assertEquals(2, cell9.distance(cell5));
        assertEquals(2, cell1.distance(cell3));
        assertEquals(2, cell3.distance(cell10));
        assertEquals(3, cell1.distance(cell4));
        assertEquals(3, cell5.distance(cell10));
        assertEquals(3, cell11.distance(cell3));
        assertEquals(5, cell11.distance(cell1));
    }

    @Test
    public void canSee() {
        assertTrue(cell7.canSee(cell7));
        assertTrue(cell7.canSee(cell3));
        assertTrue(cell7.canSee(cell11));
        assertTrue(cell8.canSee(cell4));
        assertTrue(cell4.canSee(cell1));
        assertTrue(cell1.canSee(cell6));
        assertTrue(cell9.canSee(cell8));
        assertFalse(cell8.canSee(cell9));
        assertFalse(cell8.canSee(cell3));
        assertFalse(cell8.canSee(cell6));
        assertFalse(cell1.canSee(cell4));
        assertFalse(cell1.canSee(cell7));
        assertFalse(cell1.canSee(cell9));
    }

    @Test
    public void isAligned() {
        assertTrue(cell4.isAligned(cell4));
        assertTrue(cell1.isAligned(cell5));
        assertTrue(cell5.isAligned(cell8));
        assertFalse(cell1.isAligned(cell8));
    }

    @Test
    public void isBetween() {
        assertTrue(cell3.isBetween(cell3, cell3));
        assertTrue(cell2.isBetween(cell3, cell2));
        assertTrue(cell6.isBetween(cell5, cell7));
        assertTrue(cell7.isBetween(cell10, cell3));
        assertTrue(cell11.isBetween(cell9, cell11));
        assertFalse(cell9.isBetween(cell11, cell11));
        assertFalse(cell9.isBetween(cell2, cell4));
        assertFalse(cell9.isBetween(cell6, cell10));
        assertFalse(cell1.isBetween(cell4, cell2));
        assertFalse(cell5.isBetween(cell9, cell2));
    }
}
