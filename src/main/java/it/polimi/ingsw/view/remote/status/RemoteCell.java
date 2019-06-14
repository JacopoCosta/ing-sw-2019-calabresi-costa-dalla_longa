package it.polimi.ingsw.view.remote.status;

import java.util.List;

public class RemoteCell {

    private boolean isAmmoCell;

    private List<RemoteWeapon> shop;    //if AmmoCell is true, this list is empty as there's no shop in an ammoCell
    private List<String> players;       //names of the players occupying this cell

    private int red;
    private int yellow;
    private int blue;
    private boolean includesPowerUp;

    public void setAmmoCell(boolean isAmmoCell) {
        this.isAmmoCell = isAmmoCell;
    }

    public List<RemoteWeapon> getShop() {
        return shop;
    }

    public List<String> getPlayers() {
        return players;
    }

    public int getRed() {
        return red;
    }

    public int getYellow() {
        return yellow;
    }

    public int getBlue() {
        return blue;
    }

    public boolean isAmmoCell() {
        return isAmmoCell;
    }

    public boolean includesPowerUp() {
        return includesPowerUp;
    }

    public void setShop(List<RemoteWeapon> shop) {
        this.shop = shop;
    }

    public void rewrite(int red, int yellow, int blue, boolean includesPowerUp) {   //used if isAmmoCell == true
        this.red = red;
        this.yellow = yellow;
        this.blue = blue;
        this.includesPowerUp = includesPowerUp;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }
}
