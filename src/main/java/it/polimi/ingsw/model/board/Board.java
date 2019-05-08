package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class Board {
    private List<Player> killers;
    private List<Player> doubleKillers;
    private List<Cell> cells;

    private Deck<Weapon> weaponDeck;
    private Deck<AmmoTile> ammoTileDeck;
    private Deck<PowerUp> powerUpDeck;

    private Board() {}

    public static Board generate(int type) {
        Board board = new Board();

        // initialize starting values
        board.killers = new ArrayList<>();
        board.doubleKillers = new ArrayList<>();

        board.weaponDeck = Deck.generateWeapons();
        board.ammoTileDeck = Deck.generateAmmoTiles();
        board.powerUpDeck = Deck.generatePowerUps();

        board.weaponDeck.shuffle();
        board.ammoTileDeck.shuffle();
        board.powerUpDeck.shuffle();

        // type is used to choose one predefined cell configuration
        board.cells = Board.configureCells(type);

        return board;
    }

    protected static List<Cell> configureCells(int boardType) {
        List<Cell> cells = new ArrayList<>();
        switch(boardType) {
            default:
                break;
        }
        return cells;
    }

    public void sortCells(List<Cell> cells) {
        Comparator<Cell> before = (c1, c2) -> {
            if(c1.getYCoord() == c2.getYCoord())
                return c1.getYCoord() - c2.getYCoord();
            return c1.getXCoord() - c2.getXCoord();
        };
        cells = cells.stream()
                    .sorted(before)
                    .collect(Collectors.toList());
    }

    public Cell findSpawnPoint(AmmoCubes ammoCubeColor) {
        for(Cell cell : cells) {
            if(cell.isSpawnPoint() && ((SpawnCell)cell).getAmmoCubeColor().equals(ammoCubeColor))
                return cell;
        }
        return null;
    }

    //tells if the cell of given coordinates exists in the current board configuration
    public boolean isExistingCell(int xCoord, int yCoord) {
        for(Cell cell: cells) {
            if(cell.getXCoord() == xCoord && cell.getYCoord() == yCoord)
                return true;
        }
        return false;
    }

    public void draw() {

        int boardWidth = 0;
        int boardHeight = 0;

        sortCells(this.cells);
        //now the list is sorted

        //calculates boardWidth and boardHeight
        int i;
        for(i=0; i<cells.size(); i++)
            boardWidth = max(boardWidth, cells.get(i).getXCoord());
        boardHeight = cells.get(i-1).getYCoord(); //that's because the list has been sorted

        //displays the board
        for(Integer h=0; h < boardHeight; h++) {

            //printing line 1
            for(Integer w=0; w < boardWidth; w++) {
                if(isExistingCell(w, h)) {
                    System.out.print("┏━━━━━━━━━━━━┓"); //14 characters in total
                }
                else {  //the cell doesn't exists
                    System.out.print("              ");
                }
            }
            System.out.print("\n");

            //printing line 2
            for(Integer w=0; w < boardWidth; w++) {
                if(isExistingCell(w, h)) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 3
            for(Integer w=0; w < boardWidth; w++) {
                if(isExistingCell(w, h)) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 4
            for(Integer w=0; w < boardWidth; w++) {
                if(isExistingCell(w, h)) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 5
            for(Integer w=0; w < boardWidth; w++) {
                if(isExistingCell(w, h)) {
                    System.out.print("┗━━━━━━━━━━━━┛");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");
            /*TODO
                create walls and doors
                make it print all the info about the cell;
                delete the double walls between two adjacent cells
             */
        }

    }
}
