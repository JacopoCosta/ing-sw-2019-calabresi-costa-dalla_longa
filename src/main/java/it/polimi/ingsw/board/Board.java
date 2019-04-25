package it.polimi.ingsw.board;

import it.polimi.ingsw.ammo.AmmoCubes;
import it.polimi.ingsw.ammo.AmmoTile;
import it.polimi.ingsw.board.cell.Cell;
import it.polimi.ingsw.board.cell.SpawnCell;
import it.polimi.ingsw.player.Player;
import it.polimi.ingsw.powerups.PowerUp;
import it.polimi.ingsw.weaponry.Weapon;

import java.util.ArrayList;
import java.util.List;

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

    public static List<Cell> configureCells(int boardType) {
        List<Cell> cells = new ArrayList<>();
        switch(boardType) {
            default:
                break;
        }
        return cells;
    }

    public Cell findSpawnPoint(AmmoCubes ammoCubeColor) {
        for(Cell cell : cells) {
            if(cell.isSpawnPoint() && ((SpawnCell)cell).getAmmoCubeColor().equals(ammoCubeColor))
                return cell;
        }
        return null;
    }
}
