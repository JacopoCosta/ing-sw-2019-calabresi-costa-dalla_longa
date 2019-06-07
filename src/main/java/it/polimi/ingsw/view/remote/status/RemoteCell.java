package it.polimi.ingsw.view.remote.status;

import java.util.List;

public class RemoteCell {

    private boolean isAmmoCell;

    private List<RemoteWeapon> shop;    //if AmmoCell is true, this list is empty as there's no shop in an ammoCell

    private ColorCube colorCube;
    private boolean includesPowerUp;

    private RemoteBoard remoteBoard;

    public RemoteCell (RemoteBoard remoteBoard, boolean isAmmoCell) {
        this.isAmmoCell = isAmmoCell;
        //this.remoteBoard = remoteBoard;
    }

    public List<RemoteWeapon> getShop() {
        return shop;
    }

    public ColorCube getColorCube() {
        return colorCube;
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

    public void refresh(ColorCube colorCube, boolean includesPowerUp) {
        this.colorCube = colorCube;
        this.includesPowerUp = includesPowerUp;
    }

    public void refresh(List <RemoteWeapon> shop) {
        this.shop = shop;
    }

    public RemoteBoard getRemoteBoard() {
        return remoteBoard;
    }
}
