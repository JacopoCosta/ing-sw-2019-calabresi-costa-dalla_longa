package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.network.common.deliverable.Deliverable;

import java.util.List;

/**
 * This class stores simplified information about any cell on the board. Such info is used by clients in order to get the player know about the related cell.
 * The gathered info is completely received from the server via {@link Deliverable} communication with no further elaboration (every data here is for player acknowledgment only).
 * There's no custom constructor, for the cell is declared as general as possible and often modified in most of its attributes.
 */
public class RemoteCell {

    /**
     * Cell type. If set to {@code true}, this cell contains ammo and eventually a power-up, while if {@code false} this cell contains a shop and a spawn point.
     */
    private boolean isAmmoCell;

    /**
     * Weapons that can be bought in this shop, codified as {@link RemoteWeapon}. It must be taken into account only in case {@link this.isAmmoCell} is set to {@code false}.
     */
    private List<RemoteWeapon> shop;    //if AmmoCell is true, this list is empty as there's no shop in an ammoCell
    /**
     * Names of players occupying this cell at the moment.
     */
    private List<String> players;       //names of the players occupying this cell

    /**
     * The amount of red ammo on this cell. It must be taken into account only in case {@link this.isAmmoCell} is set to {@code true}.
     */
    private int red;
    /**
     * The amount of yellow ammo on this cell. It must be taken into account only in case {@link this.isAmmoCell} is set to {@code true}.
     */
    private int yellow;
    /**
     * The amount of blue ammo on this cell. It must be taken into account only in case {@link this.isAmmoCell} is set to {@code true}.
     */
    private int blue;
    /**
     * Indicates whether this cell contains a power-up card in addition to its ammo. It must be taken into account only in case {@link this.isAmmoCell} is set to {@code true}.
     */
    private boolean includesPowerUp;

    /**
     * Getter method for {@link this.shop}.
     * @return the list of weapons which can be bought in this shop, codified as {@link RemoteWeapon}.
     */
    public List<RemoteWeapon> getShop() {
        return shop;
    }

    /**
     * Getter method for {@link this.players}.
     * @return a list containing the names of the players who are currently on this cell.
     */
    public List<String> getPlayers() {
        return players;
    }

    /**
     * Getter method for red ammo on this cell. It should be invoked only when {@code {@link this.isAmmoCell} == true}  FIXME
     * @return the amount of red ammo on this cell.
     */
    public int getRed() {
        return red;
    }
    /**
     * Getter method for yellow ammo on this cell. It should be invoked only when {@code {@link this.isAmmoCell} == true}  FIXME
     * @return the amount of yellow ammo on this cell.
     */
    public int getYellow() {
        return yellow;
    }
    /**
     * Getter method for blue ammo on this cell. It should be invoked only when {@code {@link this.isAmmoCell} == true}  FIXME
     * @return the amount of blue ammo on this cell.
     */
    public int getBlue() {
        return blue;
    }
    /**
     * Getter method for this cell containing a power-up in addition to its ammo. It should be invoked only when {@code {@link this.isAmmoCell} == true}  FIXME
     * @return whether this cell contains a power-up card in addition to its ammo.
     */
    public boolean includesPowerUp() {
        return includesPowerUp;
    }

    /**
     * Getter method for this cell being an ammo-cell or a shop-cell.
     * @return whether this cell is an ammo cell.
     */
    public boolean isAmmoCell() {
        return isAmmoCell;
    }

    /**
     * Setter method for the cell being a cell containing ammo or a cell containing a spawn and a shop.
     * Note that this method should be called exactly once per RemoteCell, as there's no way a cell can be switched from ammo-cell to spawn-cell or vice-versa.
     * @param isAmmoCell whether this cell contains ammo or contains a shop and a spawn point, {@see this.isAmmoCell}.
     */
    public void setAmmoCell(boolean isAmmoCell) {
        this.isAmmoCell = isAmmoCell;
    }

    /**
     * Setter method for {@link this.players}.
     * @param players a list of names of players occupying this cell at the moment.
     */
    public void setPlayers(List<String> players) {
        this.players = players;
    }

    /**
     * Setter method for {@link this.shop}.
     * @param shop a list of weapons that can be bought in this shop, codified as {@link RemoteWeapon}.
     */
    public void setShop(List<RemoteWeapon> shop) {
        this.shop = shop;
    }

    /**
     * This method refreshes the ammo and the optional power-up on this cell. Note that this method must be invoked only when {@link this.isAmmoCell} is set to {@code true}.
     * It reminds a constructor, however an actual constructor would require to re-set most of this class parameters that are more unlikely to change than the amount of ammo
     * on this cell, therefore this method is much more useful than a constructor containing the new amount of ammo on this cell.
     * @param red the new amount of red ammo.
     * @param yellow the new amount of yellow ammo.
     * @param blue the new amount of blue ammo.
     * @param includesPowerUp whether the cell will also include a power-up in addition to its new amount of ammo.
     */
    public void rewrite(int red, int yellow, int blue, boolean includesPowerUp) {
        this.red = red;
        this.yellow = yellow;
        this.blue = blue;
        this.includesPowerUp = includesPowerUp;
    }

}
