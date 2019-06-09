package it.polimi.ingsw.view.remote.status;

public class RemoteWeapon {

    private String name;
    private String purchaseCost;
    private String reloadCost;

    private boolean loaded;

    public RemoteWeapon(String name, String purchaseCost, String reloadCost, boolean loaded) {
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
        this.loaded = loaded;
    }

    public String getName() {
        return name;
    }

    public String getPurchaseCost() {
        return purchaseCost;
    }

    public String getReloadCost() {
        return reloadCost;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
