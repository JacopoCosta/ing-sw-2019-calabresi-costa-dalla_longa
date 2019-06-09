package it.polimi.ingsw.view.remote.status;

import java.util.List;

public abstract class RemoteBoard {

    private static List<RemotePlayer> participants;
    private static List<String> killers;
    private static List<String> doubleKillers;

    private static int width;
    private static int height;

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

    public static RemoteCell getCellByCoordinates(int x, int y) {
        return null;
        //TODO
    }

}
