package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.view.remote.ContentType;

import java.util.ArrayList;
import java.util.List;

public abstract class RemoteBoard {

    private static List<RemotePlayer> participants;

    private static int indexOfUserCharacter;    //participants.get(indexOfUserCharacter) is the user's token. Can be used for better and more specific visualization
                                                //of power-ups, weapons and so on. TODO: modify virtualView, CLI or whatever to set it correctly

    private static List<String> killers;
    private static List<String> doubleKillers;

    private static int width;
    private static int height;

    private static List<ContentType> morphology;

    private static List<RemoteCell> cells;

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static List<RemotePlayer> getParticipants() {
        return participants;
    }

    public static List<String> getKillers() {
        return killers;
    }

    public static List<String> getDoubleKillers() {
        return doubleKillers;
    }

    public static List<RemoteCell> getCells() {
        return cells;
    }

    public static List<ContentType> getMorphology() {
        return morphology;
    }

    public static int getIndexOfUserCharacter() {
        return indexOfUserCharacter;
    }

    public static void setIndexOfUserCharacter(int indexOfUserCharacter) {
        RemoteBoard.indexOfUserCharacter = indexOfUserCharacter;
    }

    public static void setCells(List<RemoteCell> cells) {
        RemoteBoard.cells = cells;
    }

    public static void setDoubleKillers(List<String> doubleKillers) {
        RemoteBoard.doubleKillers = doubleKillers;
    }

    public static void setKillers(List<String> killers) {
        RemoteBoard.killers = killers;
    }

    public static void setParticipants(List<RemotePlayer> participants) {
        RemoteBoard.participants = participants;
    }

    public static void setWidth(int width) {
        RemoteBoard.width = width;
    }

    public static void setHeight(int height) {
        RemoteBoard.height = height;
    }

    public static void setMorphology(List<ContentType> morphology) {
        RemoteBoard.morphology = morphology;
    }

    public static void generateCellScheme() {

        List<RemoteCell> cells = new ArrayList<>();

        for(ContentType c: morphology) {
            if(c.equals(ContentType.CELL))
                cells.add(new RemoteCell());

            else if(c.equals(ContentType.NONE)) {
                cells.add(null);
            }
        }

        RemoteBoard.cells = cells;
    }

    public static void updatePlayersPosition() {
        for(int i=0; i<getCells().size(); i++) {

            List<String> playersInThisCell = new ArrayList<>();

            for(RemotePlayer p: RemoteBoard.getParticipants()) {
                if(p.getPosition() == i)
                    playersInThisCell.add(p.getName());
            }
            RemoteBoard.cells.get(i).setPlayers(playersInThisCell);
        }
    }

}
