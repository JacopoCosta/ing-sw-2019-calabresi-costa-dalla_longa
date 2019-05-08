package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public void sortCells() {
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

    public int getBoardWidth() {
        return cells.stream()
                .map(Cell::getXCoord)
                .reduce(0, Integer::max);
    }

    public int getBoardHeight() {
        return cells.stream()
                .map(Cell::getYCoord)
                .reduce(0, Integer::max);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public Deck<Weapon> getWeaponDeck() {
        return weaponDeck;
    }

    public Deck<PowerUp> getPowerUpDeck() {
        return powerUpDeck;
    }

    public Deck<AmmoTile> getAmmoTileDeck() {
        return ammoTileDeck;
    }
}
