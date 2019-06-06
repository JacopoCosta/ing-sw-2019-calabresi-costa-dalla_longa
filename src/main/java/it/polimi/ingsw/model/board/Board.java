package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.CannotDiscardFirstCardOfDeckException;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ScoreList;
import it.polimi.ingsw.model.powerups.*;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.util.json.JsonObjectGenerator;
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

        board.weaponDeck = Deck.generateWeapons(JsonObjectGenerator.getWeaponDeckBuilder());
        board.powerUpDeck = Deck.generatePowerUps();
        board.ammoTileDeck = Deck.generateAmmoTiles();

        board.weaponDeck.shuffle();
        board.ammoTileDeck.shuffle();
        board.powerUpDeck.shuffle();

        // type is used to choose one predefined cell configuration
        board.cells = buildBoard(JsonObjectGenerator.getBoardBuilder(), type);
        board.cells.forEach(c -> c.setBoard(board));

        return board;
    }

    private static List<Cell> buildBoard(DecoratedJsonObject jBoardSet, int boardType) {
        DecoratedJsonObject jBoards;
        try {
            jBoards = jBoardSet.getObject("boards");
        } catch (JullPointerException e) {
            throw new JsonException("Boards not found.");
        }

        List<DecoratedJsonObject> jConfigurations;
        try {
            jConfigurations = jBoards.getArray("configurations").asList();
        } catch (JullPointerException e) {
            throw new JsonException("Configurations not found.");
        }

        Optional<DecoratedJsonObject> jConfigurationOptional = jConfigurations.stream()
                .filter(djo -> {
                    try {
                        return djo.getInt("id") == boardType;
                    } catch (JullPointerException e) {
                        throw new JsonException("Configuration id not found.");
                    }
                })
                .findFirst();

        if(jConfigurationOptional.isEmpty())
            throw new JsonException("Board configuration with id " + boardType + " was not found.");

        DecoratedJsonObject jConfiguration = jConfigurationOptional.get();

        List<DecoratedJsonObject> jCells;
        try {
            jCells = jConfiguration.getArray("cells").asList();
        } catch (JullPointerException e) {
            throw new JsonException("Configuration cells not found.");
        }

        List<Cell> cells = jCells.stream()
                .map(jc -> {
                    int x;
                    try {
                        x = jc.getInt("x");
                    } catch (JullPointerException e) {
                        throw new JsonException("X coordinate not found.");
                    }
                    int y;
                    try {
                        y = jc.getInt("y");
                    } catch (JullPointerException e) {
                        throw new JsonException("Y coordinate not found.");
                    }

                    boolean spawnPoint;
                    try {
                        spawnPoint = jc.getBoolean("spawnPoint");
                    } catch (JullPointerException e) {
                        throw new JsonException("SpawnPoint flag not found.");
                    }

                    if(spawnPoint) {
                        String color;
                        try {
                            color = jc.getString("color");
                        } catch (JullPointerException e) {
                            throw new JsonException("SpawnPoint color not found.");
                        }

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
                                    throw new JsonException(color + "is not a valid color for a spawnpoint.");
                            }

                            return new SpawnCell(x, y, ammoCubeColor);
                        } else {
                            return new AmmoCell(x, y);
                        }
                })
                .collect(Collectors.toList());

        List<DecoratedJsonObject> jAdjacencyList;
        try {
            jAdjacencyList = jConfiguration.getArray("adjacencies").asList();
        } catch (JullPointerException e) {
            throw new JsonException("Adjacency list not found.");
        }
        jAdjacencyList.forEach(djo -> {
            int sourceCellId;
            try {
                sourceCellId = djo.getInt("sourceCellId");
            } catch (JullPointerException e) {
                throw new JsonException("Source cell id not found.");
            }
            int drainCellId;
            try {
                drainCellId = djo.getInt("drainCellId");
            } catch (JullPointerException e) {
                throw new JsonException("Drain cell id not found.");
            }
            cells.get(sourceCellId).setAdjacent(cells.get(drainCellId));
        });

        List<DecoratedJsonObject> jRooms;
        try {
            jRooms = jConfiguration.getArray("rooms").asList();
        } catch (JullPointerException e) {
            throw new JsonException("Room list not found.");
        }

        jRooms.forEach(jr -> {
            String color;
            try {
                color = jr.getString("color");
            } catch (JullPointerException e) {
                throw new JsonException("Room colour not found");
            }
            List<DecoratedJsonObject> jRoomCells;
            try {
                jRoomCells = jr.getArray("cells").asList();
            } catch (JullPointerException e) {
                throw new JsonException("Room cell list not found");
            }
            List<Cell> roomCells = jRoomCells.stream()
                    .map(jrc -> {
                        int roomCellId;
                        try {
                            roomCellId = jrc.getInt("id");
                        } catch (JullPointerException e) {
                            throw new JsonException("Room cell id not found.");
                        }
                        return cells.get(roomCellId);
                    })
                    .collect(Collectors.toList());
            new Room(color, roomCells);
        });

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
                .reduce(0, Integer::max) + 1;
    }

    public int getHeight() {
        return cells.stream()
                .map(Cell::getYCoord)
                .reduce(0, Integer::max) + 1;
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
