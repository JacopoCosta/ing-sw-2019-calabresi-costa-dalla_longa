package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.view.remote.ContentType;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores simplified information about game global properties and board settings. Such info is used by clients in order to get the player know about the game.
 * The gathered info is completely received from the server.
 * This class is abstract, as there's no need for a client to instantiate a RemoteBoard object because every client is playing to at most one game, while for a server this
 * entire class is useless, for its purpose is client visualization only.
 */
public abstract class RemoteBoard {

    /**
     * The list of participants for the current game, codified as {@link RemotePlayer}.
     */
    private static List<RemotePlayer> participants;

    private static String Username;

    /**
     * The index for the user's token in {@link this.participants} list.
     * Such information about which participant is the user's one is useful for better and more specific visualization methods for {@link RemoteWeapon} and {@link RemotePowerUp}.
     */
    private static int indexOfUserCharacter;    //participants.get(indexOfUserCharacter) is the user's token. Can be used for better and more specific visualization
                                                //of power-ups, weapons and so on. TODO: modify virtualView, CLI or whatever to set it correctly
    //TODO: maybe you can remove this

    /**
     * A list of names of players who has killed somebody since the beginning of the game.
     * It's codified as String, as there's no need of more detailed info about killers.
     * Some of its element can be {@code null}, in order to represent an overkill: in that case, any name followed by a {@code null} string means that such player
     * has performed an overkill in that situation.
     */
    private static List<String> killers;
    /**
     * A list of names of players who has achieved a double-kill since the beginning of the game.
     * It's codified as String, as there's no need of more detailed info about killers.
     */
    private static List<String> doubleKillers;

    /**
     * The path for the right board map image which will be displayed by GUI client.
     */
    private static String boardImage;

    /**
     * The horizontal number of cells in this board map. In standard game maps, its value is 4.
     */
    private static int width;
    /**
     * The vertical number of cells in this board map. In standard game maps, its value is 3.
     */
    private static int height;

    /**
     * A sorted list of {@link ContentType} used to represent board map composition in its extremely basic components.
     */
    private static List<ContentType> morphology;

    /**
     * A sorted list of {@link RemoteCell} containing cells that compose the board map.
     */
    private static List<RemoteCell> cells;

    /**
     * Getter method for {@link #boardImage}.
     * @return the path of the source image for board drawing if using GUI.
     */
    public static String getBoardImage() {
        return boardImage;
    }

    public static String getUsername() {
        return Username;
    }

    /**
     * Getter method for {@link #width}.
     * @return the horizontal number of cells in this board map.
     */
    public static int getWidth() {
        return width;
    }
    /**
     * Getter method for {@link #height}.
     * @return the vertical number of cells in this board map.
     */
    public static int getHeight() {
        return height;
    }

    /**
     * Getter method for {@link #participants}.
     * @return the list of participants in this game.
     */
    public static List<RemotePlayer> getParticipants() {
        return participants;
    }

    /**
     * Getter method for {@link #killers}.
     * @return the authors names of every kill since the beginning of the game.
     */
    public static List<String> getKillers() {
        return killers;
    }
    /**
     * Getter method for {@link #doubleKillers}.
     * @return the authors names of every double kill since the beginning of the game.
     */
    public static List<String> getDoubleKillers() {
        return doubleKillers;
    }

    /**
     * Getter method for {@link #morphology}.
     * @return the morphology of this board map, i.e. a sorted list of extremely basic components of this board map.
     */
    public static List<ContentType> getMorphology() {
        return morphology;
    }

    /**
     * Getter method for {@link #cells}.
     * @return the sorted list of cells composing this board map.
     */
    public static List<RemoteCell> getCells() {
        return cells;
    }

    public static List<RemoteCell> getLogicalCells() {
        List<RemoteCell> cellsList = new ArrayList<>();
        for(RemoteCell c: RemoteBoard.getCells()) {
            if(c != null)
                cellsList.add(c);
        }
        return cellsList;
    }

    public static RemoteCell getCellByLogicalIndex(int index) {    //remember that ranges from 0 to 11 in standard cases
        for(int i=0; i<cells.size(); i++) {
            if(cells.get(i) != null && cells.get(i).getLogicalIndex() == index)
                return cells.get(i);
        }
        return null;    //should never happen
    }

    /**
     * Getter method for {@link #indexOfUserCharacter}.
     * @return the index for the user's token in {@link this.participants} list.
     */
    public static int getIndexOfUserCharacter() {
        return indexOfUserCharacter;
    }

    /**
     * Setter method for {@link #indexOfUserCharacter}.
     * @param indexOfUserCharacter the index for the user's token in {@link this.participants} list.
     */
    public static void setIndexOfUserCharacter(int indexOfUserCharacter) {
        RemoteBoard.indexOfUserCharacter = indexOfUserCharacter;
    }

    public static void setUsername(String username) {
        Username = username;
    }

    /**
     * Getter method for {@link #killers}.
     * @param killers the authors names of every kill since the beginning of the game.
     */
    public static void setKillers(List<String> killers) {
        RemoteBoard.killers = killers;
    }
    /**
     * Getter method for {@link #doubleKillers}.
     * @param doubleKillers the authors names of every double kill since the beginning of the game.
     */
    public static void setDoubleKillers(List<String> doubleKillers) {
        RemoteBoard.doubleKillers = doubleKillers;
    }

    /**
     * Setter method for {@link #participants}.
     * @param participants a list of participants for the current game, codified as {@link RemotePlayer}.
     */
    public static void setParticipants(List<RemotePlayer> participants) {
        RemoteBoard.participants = participants;
    }

    /**
     * Calculator method for {@link #boardImage}.
     * @param boardType the number of boardType (ranging from 1 to 4) needed to calculate the source image for board map drawing (needed if using GUI).
     */
    public static void calculateBoardImage(int boardType) {
        RemoteBoard.boardImage = "/gui/png/board/board_" + (char) boardType + ".png";
    }

    /**
     * Setter method for {@link #width}.
     * @param width the width of the board.
     */
    public static void setWidth(int width) {
        RemoteBoard.width = width;
    }
    /**
     * Setter method for {@link #height}.
     * @param height the height of the board.
     */
    public static void setHeight(int height) {
        RemoteBoard.height = height;
    }

    /**
     * Setter method for {@link #morphology}.
     * @param morphology the morphology of this board map, i.e. a sorted list of extremely basic components of this board map.
     */
    public static void setMorphology(List<ContentType> morphology) {
        RemoteBoard.morphology = morphology;
    }

    /**
     * This method initialises the list {@link #cells}. This is done by iterating through every element of {@link #morphology}
     * completely ignoring {@link ContentType} walls and angles and focusing on CELL and NONE elements.
     * CELL refers to an existing cell, which can be either an ammo cell or a shop cell (in this case, a new cell will be created and added to cell list),
     * while NONE refers to a hole in the map, with no cell associated (in which case, such element will be skipped and not represented as cell).
     * After this operation, the RemoteBoard will be ready to receive more specific info about every single cell by the server.
     * NOTE: the cell scheme will never be changed after its generation, so this method make a setter method for {@link #cells} completely useless.
     * Also, this method will be called exactly once per game.
     */
    public static void generateCellScheme() {

        List<RemoteCell> cells = new ArrayList<>();

        int logicalIndex = 0;

        for(ContentType c: morphology) {
            if(c.equals(ContentType.CELL)) {
                RemoteCell cell = new RemoteCell();
                cell.setLogicalIndex(logicalIndex);
                cells.add(cell);
                logicalIndex++;
            }

            else if(c.equals(ContentType.NONE)) {
                cells.add(null);
            }

        }

        /*
         DEBUG ONLY
        System.out.println("Generated cell scheme:");
        for(int i=0; i<cells.size(); i++) {
            if(cells.get(i) == null)
                System.out.println("cell "+ i + " is null");
            else
                System.out.println("cell "+ i + "has phys. index " + cells.get(i).getLogicalIndex());
        }
        */

        RemoteBoard.cells = cells;
    }

    /**
     * This method refreshes the list of participants in every {@link RemoteCell}. Since {@link RemoteCell#setPlayers(List)} requires a whole list of participants as argument,
     * this method iterates through every RemoteCell contained in {@link #cells}, then iterates on every {@link RemotePlayer} in {@link #participants} in order
     * to acknowledge which of them are currently located in the current cell, then the name list of players on that cell is used as argument for {@code RemoteCell.setPlayers}.
     * Given k players and n cells, the complexity of this algorithm is O(k*n).
     */
    public static void updatePlayersPosition() {
        for(int i=0; i<getLogicalCells().size(); i++) {

            List<String> playersInThisCell = new ArrayList<>();

            for(RemotePlayer p: RemoteBoard.getParticipants()) {
                if(p.getPosition() == i)    //since i starts from 0, every player in a position less than 0 won't be considered
                    playersInThisCell.add(p.getName());
            }
            RemoteBoard.getLogicalCells().get(i).setPlayers(playersInThisCell);
        }
    }

}
