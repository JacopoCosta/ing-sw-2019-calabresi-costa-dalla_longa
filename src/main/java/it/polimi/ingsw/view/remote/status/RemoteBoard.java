package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.view.remote.WallType;

import java.util.ArrayList;
import java.util.List;

public class RemoteBoard {

    private List<RemotePlayer> participants;
    private List<RemotePlayer> killers;
    private List<RemotePlayer> doubleKillers;

    private int Width;
    private int Height;

    private ArrayList<RemoteCell> cells;

    public int getWidth() {
        return Width;
    }

    public int getHeight() {
        return Height;
    }

    public List<RemotePlayer> getParticipants() {
        return participants;
    }

    public List<RemotePlayer> getKillers() {
        return killers;
    }

    public List<RemotePlayer> getDoubleKillers() {
        return doubleKillers;
    }

    public ArrayList<RemoteCell> getCells() {
        return cells;
    }

    public void setDoubleKillers(List<RemotePlayer> doubleKillers) {
        this.doubleKillers = doubleKillers;
    }

    public void setKillers(List<RemotePlayer> killers) {
        this.killers = killers;
    }

    public void setParticipants(List<RemotePlayer> participants) {
        this.participants = participants;
    }

    public void setWidth(int width) {
        Width = width;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public RemoteCell getCellByCoordinates(int x, int y) {
        return null;
        //TODO
    }

}
