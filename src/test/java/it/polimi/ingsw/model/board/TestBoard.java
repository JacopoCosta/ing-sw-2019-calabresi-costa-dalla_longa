package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.weaponry.Weapon;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class TestBoard {
    private Cell cell00 = new AmmoCell(0,0);
    private Cell cell01 = new AmmoCell(0,1);
    private Cell cell10 = new AmmoCell(1,0);
    private Cell cell20 = new AmmoCell(2,0);
    private Cell cell21 = new AmmoCell(2,1);
    private Cell cell22 = new AmmoCell(2,2);
    private Cell cell31 = new AmmoCell(3,1);
    private Cell cell32 = new AmmoCell(3,2);
    private Cell cell33 = new AmmoCell(3,3);
    private Cell cell34 = new AmmoCell(3,4);
    private Cell cell40 = new AmmoCell(4,0);
    private Cell cell41 = new AmmoCell(4,1);
    private Cell cell43 = new AmmoCell(4,3);

    private List<Cell> listA = new ArrayList<>(); //already sorted along X
    private List<Cell> listB = new ArrayList<>(); //already sorted along Y
    private List<Cell> listC = new ArrayList<>(); //quite randomly sorted
    private List<Cell> listD = new ArrayList<>(); //quite randomly sorted


    private Comparator<Cell> before = (c1, c2) -> {
        if(c1.getYCoord() != c2.getYCoord())
            return c1.getYCoord() - c2.getYCoord();
        return c1.getXCoord() - c2.getXCoord();
    };

    @Before
    public void setUp() {

        listA.add(cell00);
        listA.add(cell10);
        listA.add(cell20);
        listA.add(cell40);
        listA.add(cell01);
        listA.add(cell21);
        listA.add(cell31);
        listA.add(cell41);
        listA.add(cell22);
        listA.add(cell32);
        listA.add(cell33);
        listA.add(cell43);
        listA.add(cell34);
    }


    @Test
    public void sortCells(){
        for(Cell c : listA)
            if(listA.indexOf(c) < listA.size() - 1)
                assertTrue(0 >= before.compare(c, listA.get(listA.indexOf(c)+ 1)));
    }

    @Test
    public void fetchWeapon() {
        Game game = Game.create(false, 1, 1, new ArrayList<>());
        Board board = game.getBoard();

        Weapon lockRifle = board.fetchWeapon("Lock Rifle").orElse(null);
        assertNotNull(lockRifle);

        Weapon plasmaGun = board.fetchWeapon("Plasma Gun").orElse(null);
        assertNotNull(plasmaGun);

        Weapon nonExistentWeapon = board.fetchWeapon("Nyan Cat Launcher").orElse(null);
        assertNull(nonExistentWeapon);

        Weapon lockRifle2 = board.fetchWeapon("Lock Rifle").orElse(null);
        assertNull(lockRifle2);
    }
}
