package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.view.virtual.Deliverable;

/**
 * This class stores simplified information about any weapon. Such info is used by CLI-using clients in order to get the player know about the related weapon.
 * The gathered info is completely received from the server via {@link Deliverable}s communication with no further elaboration (every data here is for player acknowledgment only).
 * There isn't any setter method, as elaborating data and manage players choices is up to the server, that's not the purpose of this client.
 */
public class RemoteWeapon {
    /**
     * Weapon name.
     */
    private String name;
    /**
     * Weapon purchase cost, codified as String.
     */
    private String purchaseCost;
    /**
     * Weapon reload cost, codified as String.
     */
    private String reloadCost;

    /**
     * Weapon loaded/unloaded status. {@code true} is used for a loaded weapon, {@code false} for an unloaded one.
     */
    private boolean loaded;

    /**
     * This is the only constructor.
     * @param name Weapon name.
     * @param purchaseCost Weapon purchase cost.
     * @param reloadCost Weapon reload cost.
     * @param loaded Weapon loaded/unloaded status, {@see this.loaded}.
     */
    public RemoteWeapon(String name, String purchaseCost, String reloadCost, boolean loaded) {
        this.name = name;
        this.purchaseCost = purchaseCost;
        this.reloadCost = reloadCost;
        this.loaded = loaded;
    }

    /**
     * Getter method for the {@link this.name} attribute.
     * @return the weapon name.
     */
    public String getName() {
        return name;
    }
    /**
     * Getter method for the {@link this.purchaseCost} attribute.
     * @return the weapon purchase cost.
     */
    public String getPurchaseCost() {
        return purchaseCost;
    }
    /**
     * Getter method for the {@link this.reloadCost} attribute.
     * @return the weapon reload cost.
     */
    public String getReloadCost() {
        return reloadCost;
    }
    /**
     * Getter method for the {@link this.loaded} attribute.
     * @return the weapon loaded/unloaded status.
     */
    public boolean isLoaded() {
        return loaded;
    }
}
