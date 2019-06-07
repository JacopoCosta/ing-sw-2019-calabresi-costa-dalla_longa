package it.polimi.ingsw.view.remote.status;

public class RemoteWeapon {

    private String name;
    private ColorCube purchaseCost;
    private ColorCube reloadCost;

    private boolean loaded;

    public RemoteWeapon(String name, ColorCube purchaseCost, ColorCube reloadCost) {
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
    }

    public String getName() {
        return name;
    }

    public ColorCube getPurchaseCost() {
        return purchaseCost;
    }

    public ColorCube getReloadCost() {
        return reloadCost;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
