package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.CannotDiscardFirstCardOfDeckException;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ScoreList;
import it.polimi.ingsw.model.powerups.*;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Board {
    private static final int MAX_WEAPONS_PER_SPAWNPOINT = 3;

    private Game game;

    private List<Player> turnKillers;
    private List<Player> killers;
    private List<Player> doubleKillers;
    private List<Cell> cells;

    private Deck<Weapon> weaponDeck;
    private Deck<PowerUp> powerUpDeck;
    private Deck<AmmoTile> ammoTileDeck;

    private Board() {}

    public static Board generate(Game game, int type) {
        Board board = new Board();

        board.game = game;

        // initialize starting values
        board.turnKillers = new ArrayList<>();
        board.killers = new ArrayList<>();
        board.doubleKillers = new ArrayList<>();

        board.weaponDeck = Deck.generateWeapons();
        board.powerUpDeck = Deck.generatePowerUps();
        board.ammoTileDeck = Deck.generateAmmoTiles();

        board.weaponDeck.shuffle();
        board.ammoTileDeck.shuffle();
        board.powerUpDeck.shuffle();

        // type is used to choose one predefined cell configuration
        board.cells = Board.configureCells(type);
        board.cells.forEach(c -> c.setBoard(board));

        return board;
    }

    private static List<Cell> configureCells(int boardType) {
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
                list11.add(cells.get(4));
                list11.add(cells.get(5));
                list12.add(cells.get(6));
                list13.add(cells.get(7));
                list13.add(cells.get(8));
                list12.add(cells.get(9));
                new Room("blue", list10);
                new Room("red", list11);
                new Room("yellow", list12);
                new Room("white", list13);
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
                new Room("blue", list20);
                new Room("green", list21);
                new Room("red", list22);
                new Room("yellow", list23);
                new Room("white", list24);
                break;

            case 3:
                cells.add(new AmmoCell(0, 0));
                cells.add(new AmmoCell(1, 0));
                cells.add(new SpawnCell(2, 0, new AmmoCubes(0, 0, 1)));

                cells.add(new SpawnCell(0, 1, new AmmoCubes(1, 0, 0)));
                cells.add(new AmmoCell(1, 1));
                cells.add(new AmmoCell(2, 1));
                cells.add(new AmmoCell(3, 1));

                cells.add(new AmmoCell(0, 2));
                cells.add(new AmmoCell(1, 2));
                cells.add(new AmmoCell(2, 2));
                cells.add(new SpawnCell(3, 2, new AmmoCubes(0, 1, 0)));

                cells.get(0).setAdjacent(cells.get(1));
                cells.get(0).setAdjacent(cells.get(3));
                cells.get(1).setAdjacent(cells.get(2));
                cells.get(1).setAdjacent(cells.get(4));
                cells.get(2).setAdjacent(cells.get(5));
                cells.get(3).setAdjacent(cells.get(7));
                cells.get(4).setAdjacent(cells.get(5));
                cells.get(4).setAdjacent(cells.get(8));
                cells.get(5).setAdjacent(cells.get(6));
                cells.get(6).setAdjacent(cells.get(10));
                cells.get(7).setAdjacent(cells.get(8));
                cells.get(8).setAdjacent(cells.get(9));
                cells.get(9).setAdjacent(cells.get(10));

                List<Cell> list30 = new ArrayList<>();
                List<Cell> list31 = new ArrayList<>();
                List<Cell> list32 = new ArrayList<>();
                List<Cell> list33 = new ArrayList<>();
                List<Cell> list34 = new ArrayList<>();
                list30.add(cells.get(0));
                list31.add(cells.get(1));
                list31.add(cells.get(2));
                list30.add(cells.get(3));
                list32.add(cells.get(4));
                list32.add(cells.get(5));
                list33.add(cells.get(6));
                list34.add(cells.get(7));
                list34.add(cells.get(8));
                list34.add(cells.get(9));
                list33.add(cells.get(10));
                new Room("red", list30);
                new Room("blue", list31);
                new Room("purple", list32);
                new Room("yellow", list33);
                new Room("white", list34);
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


                cells.get(0).setAdjacent(cells.get(1));
                cells.get(0).setAdjacent(cells.get(4));
                cells.get(1).setAdjacent(cells.get(2));
                cells.get(1).setAdjacent(cells.get(5));
                cells.get(2).setAdjacent(cells.get(3));
                cells.get(2).setAdjacent(cells.get(6));
                cells.get(3).setAdjacent(cells.get(7));
                cells.get(4).setAdjacent(cells.get(8));
                cells.get(5).setAdjacent(cells.get(9));
                cells.get(6).setAdjacent(cells.get(7));
                cells.get(6).setAdjacent(cells.get(10));
                cells.get(7).setAdjacent(cells.get(11));
                cells.get(8).setAdjacent(cells.get(9));
                cells.get(9).setAdjacent(cells.get(10));
                cells.get(10).setAdjacent(cells.get(11));
                
                List<Cell> list40 = new ArrayList<>();
                List<Cell> list41 = new ArrayList<>();
                List<Cell> list42 = new ArrayList<>();
                List<Cell> list43 = new ArrayList<>();
                List<Cell> list44 = new ArrayList<>();
                List<Cell> list45 = new ArrayList<>();
                list40.add(cells.get(0));
                list41.add(cells.get(1));
                list41.add(cells.get(2));
                list42.add(cells.get(3));
                list40.add(cells.get(4));
                list43.add(cells.get(5));
                list44.add(cells.get(6));
                list44.add(cells.get(7));
                list45.add(cells.get(8));
                list45.add(cells.get(9));
                list44.add(cells.get(10));
                list44.add(cells.get(11));
                new Room("red", list40);
                new Room("blue", list41);
                new Room("green", list42);
                new Room("purple", list43);
                new Room("yellow", list44);
                new Room("white", list45);
                break;

            default:
                throw new Error("Board initialization error.");
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

    public Cell getCellByCoordinates(int xCoord, int yCoord) {
        for(Cell cell: cells) {
            if(cell.getXCoord() == xCoord && cell.getYCoord() == yCoord)
                return cell;
        }
        return null;
    }

    public int getWidth() {
        return cells.stream()
                .map(Cell::getXCoord)
                .reduce(0, Integer::max);
    }

    public int getHeight() {
        return cells.stream()
                .map(Cell::getYCoord)
                .reduce(0, Integer::max);
    }

    public List<Cell> getCells() {
        return cells;
    }

    public void promoteDoubleKillers() {
        game.getParticipants()
                .stream()
                .filter(p -> {
                    if(p == null)
                        return false;
                    return turnKillers.indexOf(p) < turnKillers.lastIndexOf(p);
                })
                .forEach(doubleKillers::add);

        this.turnKillers.clear();
    }

    public void addKiller(Player killer) {
        turnKillers.add(killer);
        killers.add(killer);
    }

    public List<Player> getKillers() {
        return this.killers;
    }

    public void setKillers(List<Player> killers) {
        this.killers = killers;
    }

    public List<Player> getDoubleKillers() {
        return this.doubleKillers;
    }

    public void setDoubleKillers(List<Player> doubleKillers) {
        this.doubleKillers = doubleKillers;
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
                .map(c -> (AmmoCell) c)
                .filter(ac -> ac.getAmmoTile() == null)
                .forEach(ac -> ammoTileDeck.smartDraw(true).ifPresent(ac::setAmmoTile));
    }

    public void spreadWeapons() {
        cells.stream()
                .filter(Cell::isSpawnPoint)
                .map(c -> (SpawnCell) c)
                .forEach(
                        sc -> {
                            for(int i = sc.getWeaponShop().size(); i < MAX_WEAPONS_PER_SPAWNPOINT; i ++)
                                weaponDeck.smartDraw(false).ifPresent(sc::addToWeaponShop);
                        }
                );
    }

    public void scoreUponGameOver() {
        Comparator<Player> better = (p1, p2) -> {
            int kills1 = countKills(p1);
            int kills2 = countKills(p2);
            if(kills1 != kills2)
                return kills2 - kills1; // if p1 has more kills, this expression evaluates to a negative
            return killers.indexOf(p1) - killers.indexOf(p2); // if p1 was earlier, their index is lower and a negative is returned
        };
        Predicate<Player> atLeastOneKill = p -> killers.indexOf(p) != -1;

        List<Player> trueKillers = game.getParticipants()
                .stream()
                .filter(atLeastOneKill)
                .sorted(better)
                .collect(Collectors.toList());

        for(int i = 0; i < trueKillers.size(); i ++) {
            int points = ScoreList.get(i, false);
            trueKillers.get(i).giveScore(points); // give scores in descending order to the players sorted best to worst
            game.getVirtualView().announceScore(null, trueKillers.get(i), points, false);
        }

        doubleKillers.forEach(p -> {
            p.giveScore(1);
            game.getVirtualView().announceScore(null, p, 1, true);
        }); // one extra point for each double kill scored
     }

    private int countKills(Player author) {
        int count = 0;
        Player lastKiller = null;
        for(Player p : killers) {
            if(p != null)
                lastKiller = p;
            if(lastKiller == author)
                count ++;
        }
        return count;
    }

    public Optional<Weapon> fetchWeapon(String name) {
        Weapon weapon = null;
        try {
            do {
                try {
                    weaponDeck.discard(weapon);
                } catch (CannotDiscardFirstCardOfDeckException ignored) { }
                weapon = weaponDeck.draw();
            } while(!weapon.getName().equals(name));
        } catch (EmptyDeckException e) {
            return Optional.empty();
        } finally {
            weaponDeck.regenerate();
        }
        return Optional.of(weapon);
    }

    public Optional<PowerUp> fetchPowerUp(String type, String color) {
        AmmoCubes ammoCubeColor;
        switch (color) {
            case "red":
                ammoCubeColor = AmmoCubes.red();
                break;
            case "yellow":
                ammoCubeColor = AmmoCubes.yellow();
                break;
            case "blue":
                ammoCubeColor = AmmoCubes.blue();
                break;
            default:
                throw new IllegalArgumentException(color + " is not an ammo cube color.");
        }
        PowerUp comparisonPowerUp;
        switch (type) {
            case "grenade":
                comparisonPowerUp = new Grenade(ammoCubeColor);
                break;
            case "newton":
                comparisonPowerUp = new Newton(ammoCubeColor);
                break;
            case "scope":
                comparisonPowerUp = new Scope(ammoCubeColor);
                break;
            case "teleport":
                comparisonPowerUp = new Teleport(ammoCubeColor);
                break;
            default:
                throw new IllegalArgumentException(type + "is not a powerup type.");
        }

        PowerUp powerUp = null;
        try {
            do {
                try {
                    powerUpDeck.discard(powerUp);
                } catch (CannotDiscardFirstCardOfDeckException ignored) { }
                powerUp = powerUpDeck.draw();
            } while(!powerUp.getType().equals(comparisonPowerUp.getType()) || !powerUp.getAmmoCubes().equals(comparisonPowerUp.getAmmoCubes()));
        } catch (EmptyDeckException e) {
            return Optional.empty();
        } finally {
            powerUpDeck.regenerate();
        }
        return Optional.of(powerUp);
    }

    public Optional<AmmoTile> fetchAmmoTile(int red, int yellow, int blue, boolean includesPowerUp) {
        AmmoCubes comparisonAmmoCubes = new AmmoCubes(red, yellow, blue);

        AmmoTile ammoTile = null;
        try {
            do {
                try {
                    ammoTileDeck.discard(ammoTile);
                } catch (CannotDiscardFirstCardOfDeckException ignored) { }
                ammoTile = ammoTileDeck.draw();
            } while(!ammoTile.getAmmoCubes().equals(comparisonAmmoCubes) || ! ammoTile.includesPowerUp() == includesPowerUp);
        } catch (EmptyDeckException e) {
            return Optional.empty();
        } finally {
            ammoTileDeck.regenerate();
        }
        return Optional.of(ammoTile);
    }
}
