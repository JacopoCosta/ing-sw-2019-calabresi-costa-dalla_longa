package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.remote.CLI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
    private static final int MAX_WEAPONS_PER_SPAWNPOINT = 3;

    private Game game;

    private List<Player> killers;
    private List<Player> doubleKillers;
    private List<Cell> cells;

    private Deck<Weapon> weaponDeck;
    private Deck<AmmoTile> ammoTileDeck;
    private Deck<PowerUp> powerUpDeck;

    private Board() {}

    public static Board generate(Game game, int type) {
        Board board = new Board();

        board.game = game;

        // initialize starting values
        board.killers = new ArrayList<>();
        board.doubleKillers = new ArrayList<>();

        board.weaponDeck = Deck.generateWeapons();
        board.ammoTileDeck = Deck.generateAmmoTiles();
        board.powerUpDeck = Deck.generatePowerUps();

    //    board.weaponDeck.shuffle();
        board.ammoTileDeck.shuffle();
        board.powerUpDeck.shuffle();

        // type is used to choose one predefined cell configuration
        board.cells = Board.configureCells(type);
        board.cells.forEach(c -> c.setBoard(board));

        return board;
    }

    protected static List<Cell> configureCells(int boardType) {
        List<Cell> cells = new ArrayList<>();
        switch(boardType) {
            case 1:
                cells.add(new AmmoCell(0, 0));
                cells.add(new AmmoCell(1, 0));
                cells.add(new SpawnCell(2, 0, new AmmoCubes(0, 0, 1)));
                cells.add(new SpawnCell(0, 1, new AmmoCubes(1, 0, 0)));
                cells.add(new AmmoCell(1, 1));
                cells.add(new AmmoCell(2, 1));
                cells.add(new AmmoCell(3, 1));
                cells.add(new AmmoCell(1, 2));
                cells.add(new AmmoCell(2, 2));
                cells.add(new SpawnCell(3, 2, new AmmoCubes(0, 1, 0)));

                cells.get(0).setAdjacent(cells.get(1));
                cells.get(0).setAdjacent(cells.get(3));
                cells.get(1).setAdjacent(cells.get(2));
                cells.get(2).setAdjacent(cells.get(5));
                cells.get(3).setAdjacent(cells.get(4));
                cells.get(4).setAdjacent(cells.get(5));
                cells.get(4).setAdjacent(cells.get(7));
                cells.get(5).setAdjacent(cells.get(6));
                cells.get(6).setAdjacent(cells.get(9));
                cells.get(7).setAdjacent(cells.get(8));
                cells.get(8).setAdjacent(cells.get(9));

                List<Cell> list10 = new ArrayList<>();
                List<Cell> list11 = new ArrayList<>();
                List<Cell> list12 = new ArrayList<>();
                List<Cell> list13 = new ArrayList<>();
                list10.add(cells.get(0));
                list10.add(cells.get(1));
                list10.add(cells.get(2));
                list11.add(cells.get(3));
                list10.add(cells.get(4));
                list10.add(cells.get(5));
                list12.add(cells.get(6));
                list13.add(cells.get(7));
                list13.add(cells.get(8));
                list12.add(cells.get(9));
                new Room(list10);
                new Room(list11);
                new Room(list12);
                new Room(list13);

                break;
            case 2:
                cells.add(new AmmoCell(0, 0));
                cells.add(new AmmoCell(1, 0));
                cells.add(new SpawnCell(2, 0, new AmmoCubes(0, 0, 1)));
                cells.add(new AmmoCell(3, 0));
                cells.add(new SpawnCell(0, 1, new AmmoCubes(1, 0, 0)));
                cells.add(new AmmoCell(1, 1));
                cells.add(new AmmoCell(2, 1));
                cells.add(new AmmoCell(3, 1));
                cells.add(new AmmoCell(1, 2));
                cells.add(new AmmoCell(2, 2));
                cells.add(new SpawnCell(3, 2, new AmmoCubes(0, 1, 0)));

                cells.get(0).setAdjacent(cells.get(1));
                cells.get(0).setAdjacent(cells.get(4));
                cells.get(1).setAdjacent(cells.get(2));
                cells.get(2).setAdjacent(cells.get(3));
                cells.get(2).setAdjacent(cells.get(6));
                cells.get(3).setAdjacent(cells.get(7));
                cells.get(4).setAdjacent(cells.get(5));
                cells.get(5).setAdjacent(cells.get(8));
                cells.get(6).setAdjacent(cells.get(7));
                cells.get(6).setAdjacent(cells.get(9));
                cells.get(7).setAdjacent(cells.get(10));
                cells.get(8).setAdjacent(cells.get(9));
                cells.get(9).setAdjacent(cells.get(10));

                List<Cell> list20 = new ArrayList<>();
                List<Cell> list21 = new ArrayList<>();
                List<Cell> list22 = new ArrayList<>();
                List<Cell> list23 = new ArrayList<>();
                List<Cell> list24 = new ArrayList<>();
                list20.add(cells.get(0));
                list20.add(cells.get(1));
                list20.add(cells.get(2));
                list21.add(cells.get(3));
                list22.add(cells.get(4));
                list22.add(cells.get(5));
                list23.add(cells.get(6));
                list23.add(cells.get(7));
                list24.add(cells.get(8));
                list23.add(cells.get(9));
                list23.add(cells.get(10));
                new Room(list20);
                new Room(list21);
                new Room(list22);
                new Room(list23);
                new Room(list24);

                break;
            case 3:
                break;
            case 4:
                cells.add(new AmmoCell(0, 0));
                cells.add(new AmmoCell(1, 0));
                cells.add(new SpawnCell(2, 0, new AmmoCubes(0, 0, 1)));
                cells.add(new AmmoCell(3, 0));
                cells.add(new SpawnCell(0, 1, new AmmoCubes(1, 0, 0)));
                cells.add(new AmmoCell(1, 1));
                cells.add(new AmmoCell(2, 1));
                cells.add(new AmmoCell(3, 1));
                cells.add(new AmmoCell(0, 2));
                cells.add(new AmmoCell(1, 2));
                cells.add(new AmmoCell(2, 2));
                cells.add(new SpawnCell(3, 2, new AmmoCubes(0, 1, 0)));

                break;
            default:
                break;
        }
        return cells;
    }

    /**
     * This method sorts {@code List<Cell> cells}, first along the horizontal coordinate, then along the vertical coordinates, returning a
     * cell configuration that can be sequentially read in a natural visualization order (i.e. from left ro right and from above to the bottom).
     * This method invocation is necessary for the correct printing of the game board when using the CLI-visualization view.
     * @see CLI#printBoard()
     */

    public Cell findSpawnPoint(AmmoCubes ammoCubeColor) {
        for(Cell cell : cells) {
            if(cell.isSpawnPoint() && ((SpawnCell)cell).getAmmoCubeColor().equals(ammoCubeColor))
                return cell;
        }
        return null;
    }

    public Cell getCellByCoordinates(int xCoord, int yCoord) {
        for(Cell cell: cells) {
            if(cell.getXCoord() == xCoord && cell.getYCoord() == yCoord)
                return cell;
        }
        return null;
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

    public List<Player> getKillers() {
        return this.killers;
    }

    public List<Player> getDoubleKillers() {
        return this.doubleKillers;
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

    public Game getGame() {
        return this.game;
    }

    public void spreadAmmo() {
        cells.stream()
                .filter(c -> !c.isSpawnPoint())
                .forEach(
                        c -> {
                            AmmoTile at = ammoTileDeck.smartDraw(true).orElse(null);
                            ((AmmoCell) c).setAmmoTile(at);
                        }
                );
    }

    public void spreadWeapons() {
        cells.stream()
                .filter(Cell::isSpawnPoint)
                .forEach(
                        c -> {
                            List<Weapon> weapons = new ArrayList<>();

                            // draw a fixed amount of times -- if the deck is empty, do nothing
                            for(int i = 0; i < MAX_WEAPONS_PER_SPAWNPOINT; i ++) {
                                try {
                                    weapons.add(weaponDeck.draw());
                                } catch (EmptyDeckException ignored) { }
                            }

                            // add all drawn weapons to the weapon shop
                            weapons.forEach(((SpawnCell) c)::addToWeaponShop);
                        }
                );
    }

    @Override
    public String toString() {
        return Table.create(
                cells.stream().map(c -> "Cell").collect(Collectors.toList()),
                cells.stream().map(c -> "#" + c.getId() + "  ").collect(Collectors.toList()),
                cells.stream().map(Cell::getRoom).map(Room::toString).collect(Collectors.toList()),
                cells.stream().map(c -> "x:" + c.getXCoord()).collect(Collectors.toList()),
                cells.stream().map(c -> "y:" + c.getYCoord()).collect(Collectors.toList()),
                cells.stream().map(c -> "| adjacent to: " + Table.list(c.getAdjacentCells().stream()
                        .map(Cell::getId)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList()),
                cells.stream().map(c -> "contains: " + (c.isSpawnPoint() ?
                        (
                                Table.list(((SpawnCell) c).getWeaponShop()
                                        .stream()
                                        .map(Weapon::getName)
                                        .collect(Collectors.toList())
                                )
                        ) :
                        (
                                ((AmmoCell) c).getAmmoTile() == null ?
                                    "empty" :
                                    ((AmmoCell) c).getAmmoTile().toString()
                        )
                    )
                ).collect(Collectors.toList()),
                cells.stream().map(c -> c.isSpawnPoint() ? ("| this is the " + ((SpawnCell) c).getAmmoCubeColor().toStringAsColor() + " spawnpoint") : "").collect(Collectors.toList())
        );
    }
}
