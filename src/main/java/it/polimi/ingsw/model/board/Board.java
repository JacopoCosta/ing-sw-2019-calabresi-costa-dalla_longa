package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ScoreList;
import it.polimi.ingsw.model.powerups.*;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.util.json.JsonObjectGenerator;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.remote.ContentType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The {@link Board} is the space the game is played on.
 * @see Cell
 */
public class Board {

    /**
     * The maximum number of {@link Weapon}s each {@link Weapon} shop can hold at any given time.
     */
    private static final int MAX_WEAPONS_PER_SPAWNPOINT = 3;

    /**
     * The {@link Game} the {@link Board} is pertaining to.
     */
    private Game game;

    /**
     * The list of {@link Player}s who committed murder since the beginning of the current turn.
     * Each {@link Player} appears as many times as kills they were able to score.
     */
    private List<Player> turnKillers;

    /**
     * The list of {@link Player}s who committed murder since the beginning of the {@link Game}.
     * Each {@link Player} appears as many times as kills they were able to score.
     */
    private List<Player> killers;

    /**
     * The list of {@link Player}s who killed two opponents in the same turn since the beginning of the {@link Game}.
     * Each {@link Player} appears as many times as double kills they were able to score.
     */
    private List<Player> doubleKillers;

    /**
     * The list of {@link Cell}s that make up the {@link Board}.
     */
    private List<Cell> cells;

    /**
     * The {@link Deck} of {@link Weapon}s.
     */
    private Deck<Weapon> weaponDeck;

    /**
     * The {@link Deck} of {@link PowerUp}s.
     */
    private Deck<PowerUp> powerUpDeck;

    /**
     * The {@link Deck} of {@link AmmoTile}s.
     */
    private Deck<AmmoTile> ammoTileDeck;

    /**
     * This is the only constructor. It does nothing (thus creates an empty {@link Board}) and is inaccessible from outside this class.
     */
    private Board() {}

    /**
     * This factory method creates a {@link Board}, based on the board type. It then binds the {@link Board} to a {@link Game}.
     * @param game The {@link Game} the {@link Board} belongs to.
     * @param type The id of the board configuration (1~4).
     * @return a newly generated {@link Board}.
     */
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

    /**
     * This factory method constructs an object with the properties found inside the JSON object passed as argument.
     * @param jBoardSet the JSON object containing the list of {@link Board}s, each with the desired properties.
     * @param boardType The id of the board configuration (1~4).
     * @return an instance of this class in accordance with the specified properties.
     */
    private static List<Cell> buildBoard(DecoratedJsonObject jBoardSet, int boardType) {
        DecoratedJsonObject jBoards;
        try {
            jBoards = jBoardSet.getObject("boards");
        } catch (JullPointerException e) {
            throw new JsonException("Boards not found.");
        }

        List<DecoratedJsonObject> jConfigurations;
        try {
            jConfigurations = jBoards.getArray("configurations").toList();
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
            jCells = jConfiguration.getArray("cells").toList();
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
            jAdjacencyList = jConfiguration.getArray("adjacencies").toList();
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
            jRooms = jConfiguration.getArray("rooms").toList();
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
                jRoomCells = jr.getArray("cells").toList();
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

    /**
     * Finds the {@link SpawnCell} whose colour corresponds with the colour of the {@link AmmoCubes} passed in as argument.
     * @param ammoCubeColor the {@link AmmoCubes} used to identify the {@link Cell}.
     * @return the {@link Cell} whose colour corresponds with the colour of the {@link AmmoCubes} passed in as argument,
     * or null if no {@link Cell} meets the described criteria.
     */
    public Cell findSpawnPoint(AmmoCubes ammoCubeColor) {
        for(Cell cell : cells) {
            if(cell.isSpawnPoint() && ((SpawnCell)cell).getAmmoCubeColor().equals(ammoCubeColor))
                return cell;
        }
        return null;
    }

    /**
     * Finds the {@link Cell} whose coordinates are equal to those passed in as arguments.
     * @param xCoord The horizontal coordinate of the {@link Cell}.
     * @param yCoord The vertical coordinate of the {@link Cell}.
     * @return the {@link Cell} at the given coordinates, or null if no {@link Cell} is at the given coordinates.
     */
    public Cell getCellByCoordinates(int xCoord, int yCoord) {
        for(Cell cell: cells) {
            if(cell.getXCoord() == xCoord && cell.getYCoord() == yCoord)
                return cell;
        }
        return null;
    }

    /**
     * Returns the width of the {@link Board} expressed as number of {@link Cell}s.
     * @return the {@link Board} width.
     */
    public int getWidth() {
        return cells.stream()
                .map(Cell::getXCoord)
                .reduce(0, Integer::max) + 1;
    }

    /**
     * Returns the height of the {@link Board} expressed as number of {@link Cell}s.
     * @return the {@link Board} height.
     */
    public int getHeight() {
        return cells.stream()
                .map(Cell::getYCoord)
                .reduce(0, Integer::max) + 1;
    }

    /**
     * Returns the list of {@link Cell}s composing the {@link Board}.
     * @return the list of {@link Cell}s composing the {@link Board}.
     */
    public List<Cell> getCells() {
        return cells;
    }

    /**
     * Adds all {@link Player}s appearing more than once in the {@code turnKillers} list
     * to the {@code doubleKilllers} list.
     * @see Board#turnKillers
     * @see Board#doubleKillers
     */
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

    /**
     * Adds a {@link Player} to the killers list.
     * @param killer the {@link Player} to add.
     */
    public void addKiller(Player killer) {
        turnKillers.add(killer);
        killers.add(killer);
    }

    /**
     * Returns the list of killers.
     * @return the list of killers.
     */
    public List<Player> getKillers() {
        return this.killers;
    }

    /**
     * Sets the killers list to a list passed in as argument.
     * @param killers the new list of killers.
     */
    public void setKillers(List<Player> killers) {
        this.killers = killers;
    }

    /**
     * Returns the list of double killers.
     * @return the list of double killers.
     */
    public List<Player> getDoubleKillers() {
        return this.doubleKillers;
    }

    /**
     * Sets the double killers list to a list passed in as argument.
     * @param doubleKillers the new list of double killers.
     */
    public void setDoubleKillers(List<Player> doubleKillers) {
        this.doubleKillers = doubleKillers;
    }

    /**
     * Returns the {@link Weapon} {@link Deck}.
     * @return the {@link Deck}.
     */
    public Deck<Weapon> getWeaponDeck() {
        return weaponDeck;
    }

    /**
     * Returns the {@link PowerUp} {@link Deck}.
     * @return the {@link Deck}.
     */
    public Deck<PowerUp> getPowerUpDeck() {
        return powerUpDeck;
    }

    /**
     * Returns the {@link AmmoTile} {@link Deck}.
     * @return the {@link Deck}.
     */
    public Deck<AmmoTile> getAmmoTileDeck() {
        return ammoTileDeck;
    }

    /**
     * Returns the {@link Game} that is being played on the {@link Board}.
     * @return the {@link Game} that is being played on the {@link Board}.
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * Spreads ammo on every {@link AmmoCell} of the {@link Board}.
     * More specifically, for each {@link AmmoCell} that does not contain any {@link AmmoTile}, a new {@link AmmoTile} is drawn from
     * the {@link AmmoTile} {@link Deck} and placed onto that {@link Cell}.
     * @see Board#ammoTileDeck
     */
    public void spreadAmmo() {
        cells.stream()
                .filter(c -> !c.isSpawnPoint())
                .map(c -> (AmmoCell) c)
                .filter(ac -> ac.getAmmoTile() == null)
                .forEach(ac -> ammoTileDeck.smartDraw(true).ifPresent(ac::setAmmoTile));
    }

    /**
     * Spreads {@link Weapon}s on every {@link SpawnCell}'s {@link Weapon} shop.
     * More specifically, for each {@link SpawnCell} that contains fewer {@link Weapon}s than the maximum allowed amount,
     * a new {@link Weapon} card is drawn from the {@link Weapon} {@link Deck} for each free slot in the shop, to which the {@link Weapon} is added.
     * This method stops having an effect once the {@link Weapon} {@link Deck} has been depleted.
     * @see Board#weaponDeck
     * @see Board#MAX_WEAPONS_PER_SPAWNPOINT
     */
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

    /**
     * Awards {@link Player}s with points from the {@code ScoreList}, with the {@link Player}s ranked best to last according
     * to amount of kills (overkills are worth two kills), with ties broken in favour of the {@link Player} who made the first
     * kill the earliest. One additional point is awarded to each {@link Player} for each double kill they were able to
     * perform during the {@link Game}.
     * @see ScoreList
     * @see Board#countKills(Player)
     */
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
            game.getVirtualView().sendUpdateScore(trueKillers.get(i), null, points, false);
        }

        doubleKillers.forEach(p -> {
            p.giveScore(1);
            game.getVirtualView().sendUpdateScore(p, null, 1, true);
        }); // one extra point for each double kill scored
    }

    /**
     * Counts the equivalent number of kills a {@link Player} scored during the {@link Game}.
     * Overkills are counted as two kills.
     * @param author the {@link Player} on whom to count the kills.
     * @return the number of equivalent kills.
     */
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

    /**
     * Searches through the {@link Weapon} {@link Deck} until it finds a {@link Weapon} with the given name.
     * @param name the name of the {@link Weapon}.
     * @return An optional containing that {@link Weapon}, if found, otherwise an empty optional.
     */
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

    /**
     * Searches through the {@link PowerUp} {@link Deck} until it finds a {@link PowerUp} with the given type and colour.
     * @param type the type of the {@link PowerUp}.
     * @param color the colour of the {@link PowerUp}.
     * @return An optional containing that {@link PowerUp}, if found, otherwise an empty optional.
     */
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

    /**
     * Searches through the {@link AmmoTile} {@link Deck} until it finds an {@link AmmoTile} with the given properties.
     * @param red the amount of red cubes on the {@link AmmoTile}.
     * @param yellow the amount of yellow cubes on the {@link AmmoTile}.
     * @param blue the amount of blue cubes on the {@link AmmoTile}.
     * @param includesPowerUp whether or not the {@link AmmoTile} includes a {@link PowerUp}.
     * @return An optional containing that {@link PowerUp}, if found, otherwise an empty optional.
     */
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

    /**
     * Calculates the morphology of the {@link Cell} scheme of this {@link Board}, i.e. a simple scheme containing basic info about the type of walls between every {@link Cell}.
     * Its only purpose is being analysed by CLI-type clients in order to draw the correct {@link Board} configuration.
     * @return A List containing the morphology of the current {@link Board} {@link Cell} scheme.
     */
    public List<ContentType> getMorphology() {
        List<ContentType> morphology = new ArrayList<>();

        int gridHeight = this.getHeight()*2 + 1;
        int gridWidth = this.getWidth()*2 + 1;

        for(int h = 0; h < gridHeight; h++) {
            for(int w = 0; w < gridWidth; w++) {

                if(h%2 == 0) {  //we are cycling on a row made of ANGLES and horizontal walls
                    if(w%2 == 0)

                        morphology.add(ContentType.ANGLE);

                    else    //w is odd
                            //must add the wall between the cell above and the cell below
                        morphology.add(getWallBetweenCells((w-1)/2, (h-2)/2, (w-1)/2, h/2));
                }
                else { //we are cycling on a row made of vertical walls and cells
                    if(w%2 == 0)

                        morphology.add(getWallBetweenCells((w-2)/2, (h-1)/2, w/2, (h-1)/2));

                    else {   //it's a cell, but it can exist or not
                        if(this.getCellByCoordinates((w-1)/2, (h-1)/2) == null) //the cell doesn't exist
                            morphology.add(ContentType.NONE);
                        else
                            morphology.add(ContentType.CELL);
                    }
                }
            }
        }

        return morphology;
    }

    /**
     * Calculates the wall between two given {@link Cell}s. Note that this calculation is symmetrical
     * (i.e. its result doesn't change in case x1 and x2 are swapped, as long as y1 and y2 are swapped as well).
     * @param x1 the horizontal coordinate of the first {@link Cell}, ranging from 0 (left) to boardWidth-1 (right).
     * @param y1 the vertical coordinate of the first {@link Cell}, ranging from 0 (top) to boardHeight-1 (bottom).
     * @param x2 the horizontal coordinate of the second {@link Cell}, ranging from 0 (left) to boardWidth-1 (right).
     * @param y2 the vertical coordinate of the second {@link Cell}, ranging from 0 (top) to boardHeight-1 (bottom).
     * @return The right wall separating the {@link Cell}s. Note: If given {@link Cell}s are too far to be separated by a wall, ContentType.ANGLE will be returned instead.
     */
    private ContentType getWallBetweenCells(int x1, int y1, int x2, int y2) {
        if (this.getCellByCoordinates(x1, y1) != null && this.getCellByCoordinates(x2, y2) != null) { //they both exist

            try {
                if (this.getCellByCoordinates(x1, y1).isGhostlyAdjacent(this.getCellByCoordinates(x2, y2))) { //the cells may be separated by a wall, a door or nothing
                    if (!this.getCellByCoordinates(x1, y1).isAdjacent(this.getCellByCoordinates(x2, y2))) { //the cells are separated by a wall
                        if (x1 == x2)
                            return ContentType.HOR_FULL;
                        else if (y1 == y2)
                            return ContentType.VER_FULL;
                    }
                    else if (this.getCellByCoordinates(x1, y1).getRoom() == this.getCellByCoordinates(x2, y2).getRoom()) { //they're part of the same room
                        if (x1 == x2)
                            return ContentType.HOR_VOID;
                        else if (y1 == y2)
                            return ContentType.VER_VOID;
                    }
                    else { //they are separated by a door
                        if(x1 == x2)
                            return ContentType.HOR_DOOR;
                        else if (y1 == y2)
                            return ContentType.VER_DOOR;
                    }
                }
            } catch (NullCellOperationException ignored) {
                //it never happens, as this method is invoked only after the whole board has been initialised
            }
            //the cells aren't even ghostlyAdjacent, so there isn't any separator between them, so it should be
            //return ContentType.ANGLE;
            //Yet, this case is covered by "return ANGLE" at the end of the method

        } //end if (both cells exist)

        else if(this.getCellByCoordinates(x1, y1) == null && this.getCellByCoordinates(x2, y2) == null) {
            //none of them exist; however, they may be printed if they refers to blank spaces
            if (x1 == x2 && Math.abs(y1 - y2) == 1)
                return ContentType.HOR_VOID;
            else if (y1 == y2 && Math.abs(x1 - x2) == 1)
                return ContentType.VER_VOID;
        }
        else
        {
            if(this.getCellByCoordinates(x1, y1) == null) { //cell1 does not exists, while cell2 does
                if(x1 == x2 && Math.abs(y1 - y2) == 1)
                    return ContentType.HOR_FULL;
                else if(y1 == y2 && Math.abs(x1 - x2) == 1)
                    return ContentType.VER_FULL;
            }
            else if(this.getCellByCoordinates(x2, y2) == null) { //cell2 does not exists, while cell1 does
                if(x1 == x2 && Math.abs(y1 - y2) == 1)
                    return ContentType.HOR_FULL;
                else if(y1 == y2 && Math.abs(x1 - x2) == 1) {
                    return ContentType.VER_FULL;
                }
            }
        }//end else (exactly one cell exists)
        return ContentType.ANGLE; //can be useful, however in this whole program this method will be called only to determine walls, not angle
    }
}
