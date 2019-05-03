package it.polimi.ingsw.model.cell;

import it.polimi.ingsw.model.board.Room;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestCell {
    private Cell cell0 = new AmmoCell(0, 0);
    private Cell cell1 = new AmmoCell(1, 0);
    private Cell cell2 = new AmmoCell(2, 0);
    private Cell cell3 = new AmmoCell(0, 1);
    private Cell cell4 = new AmmoCell(1, 1);
    private Cell cell5 = new AmmoCell(2, 1);
    private Cell cell6 = new AmmoCell(0, 2);
    private Cell cell7 = new AmmoCell(1, 2);
    private Cell cell8 = new AmmoCell(0, 3);
    private Cell cell9 = new AmmoCell(1, 3);

    private List<Cell> listA = new ArrayList<>();
    private List<Cell> listB = new ArrayList<>();
    private List<Cell> listC = new ArrayList<>();
    private List<Cell> listD = new ArrayList<>();

    @Before
    public void setUp() {

        try {
            cell0.setAdjacent(cell1);
            cell0.setAdjacent(cell3);
            cell1.setAdjacent(cell2);
            cell2.setAdjacent(cell5);
            cell3.setAdjacent(cell4);
            cell3.setAdjacent(cell6);
            cell4.setAdjacent(cell5);
            cell6.setAdjacent(cell7);
            cell6.setAdjacent(cell8);
            cell7.setAdjacent(cell9);
        } catch (Exception ignored) { /* untakeable */ }

        listA.add(cell0);
        listB.add(cell1);
        listB.add(cell2);
        listC.add(cell3);
        listC.add(cell4);
        listC.add(cell5);
        listD.add(cell6);
        listD.add(cell7);
        listD.add(cell8);
        listD.add(cell9);

        Room roomA = new Room(listA);
        Room roomB = new Room(listB);
        Room roomC = new Room(listC);
        Room roomD = new Room(listD);
    }


    @Test
    public void distance() {
        assertEquals(0, cell3.distance(cell3));
        assertEquals(1, cell5.distance(cell4));
        assertEquals(1, cell3.distance(cell6));
        assertEquals(2, cell7.distance(cell8));
        assertEquals(2, cell0.distance(cell6));
        assertEquals(2, cell2.distance(cell4));
        assertEquals(2, cell3.distance(cell7));
        assertEquals(3, cell1.distance(cell4));
        assertEquals(3, cell3.distance(cell2));
        assertEquals(3, cell4.distance(cell8));
        assertEquals(6, cell2.distance(cell9));
    }

    @Test
    public void canSee() {
        assertTrue(cell7.canSee(cell7));
        assertTrue(cell4.canSee(cell5));
        assertTrue(cell3.canSee(cell6));
        assertTrue(cell3.canSee(cell5));
        assertTrue(cell6.canSee(cell9));
        assertTrue(cell2.canSee(cell3));
        assertFalse(cell3.canSee(cell2));
        assertTrue(cell0.canSee(cell4));
        assertFalse(cell4.canSee(cell0));
        assertFalse(cell4.canSee(cell7));
        assertFalse(cell0.canSee(cell6));
        assertFalse(cell1.canSee(cell3));
        assertFalse(cell1.canSee(cell9));
    }

    @Test
    public void isAligned() {
        assertTrue(cell4.isAligned(cell4));
        assertTrue(cell3.isAligned(cell5));
        assertTrue(cell1.isAligned(cell9));
        assertFalse(cell1.isAligned(cell8));
    }
}