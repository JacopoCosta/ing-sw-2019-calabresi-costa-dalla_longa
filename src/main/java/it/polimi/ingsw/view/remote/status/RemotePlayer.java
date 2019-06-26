package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.view.remote.gui.Token;
import it.polimi.ingsw.network.common.deliverable.Deliverable;

import java.util.List;

/**
 * This class stores simplified information about any weapon. Such info is used by CLI-using clients in order to get the player know about the related weapon.
 * The gathered info is completely received from the server via {@link Deliverable} communication with no further elaboration (every data here is for player acknowledgment only).
 * The only constructor here (i.e. {@link this.RemotePlayer}) is used to create a new player, which will be modified but never re-constructed until the end of the game.
 */
public class RemotePlayer {

    /**
     * Player's name
     */
    private String name;

    /**
     * The current score of the player
     */
    private int score;
    /**
     * The current position of the player, codified as an integer used as index for cell scheme. {@see RemoteBoard.getCells}
     */
    private int position;
    /**
     * The amount of times this player has been killed from the very beginning of the game.
     */
    private int deathCount;

    /**
     * The amount of red ammo owned by this player.
     */
    private int redAmmo;
    /**
     * The amount of yellow ammo owned by this player.
     */
    private int yellowAmmo;
    /**
     * The amount of blue ammo owned by this player.
     */
    private int blueAmmo;

    /**
     * Flag to check player's connection status. It's set to {@code true} if this player is connected and participating to the current game, else it's set to {@code false}.
     * It's not strictly necessary for either CLI or GUI to work, but it could be a good info for other participants.
     */
    private boolean isConnected;    //TODO

    /**
     * Associates the player to the corresponding physical token for a better GUI visualization, according to {@link PlayerColor} scheme.
     */
    private PlayerColor playerColor; //TODO

    private Token token;    //TODO

    /**
     * List of weapons owned by this player, both loaded and unloaded, codified as list of {@link RemoteWeapon}.
     */
    private List<RemoteWeapon> weapons;
    /**
     * List of power-ups owned by this player, codified as list of {@link RemotePowerUp}.
     */
    private List<RemotePowerUp> powerUps;   //powerups owned by this player

    /**
     * List of names of players who inflicted damage on this player since their last scoring, codified as String.
     */
    private List<String> damage;
    /**
     * List of names of players who put a marker on this player since their last scoring, codified as String.
     */
    private List<String> markings;

    /**
     * This is the only constructor.
     * @param name player's name.
     */
    public RemotePlayer(String name) {
        this.name = name;
    }

    /**
     * Getter method for this player's name.
     * @return this player's name.
     */
    public String getName() {
        return name;
    }
    /**
     * Getter method for the current score of this player.
     * @return the current score of this player.
     */
    public int getScore() {
        return score;
    }
    /**
     * Getter method for the current position of this player.
     * @return the current position of this player, codified as an integer used as index for cell scheme. {@see RemoteBoard.getCells}
     */
    public int getPosition() {
        return position;
    }

    /**
     * Getter method for the amount of times this player has been killed from the very beginning of the game.
     * @return the amount of times this player has been killed.
     */
    public int getDeathCount() {
        return deathCount;
    }

    /**
     * Getter method for the number of red ammo currently owned by this player.
     * @return this player's currently owned red ammo.
     */
    public int getRedAmmo() {
        return redAmmo;
    }
    /**
     * Getter method for the number of yellow ammo currently owned by this player.
     * @return this player's currently owned yellow ammo.
     */
    public int getYellowAmmo() {
        return  yellowAmmo;
    }
    /**
     * Getter method for the number of blue ammo currently owned by this player.
     * @return this player's currently owned blue ammo.
     */
    public int getBlueAmmo() {
        return blueAmmo;
    }

    /**
     * Getter method for the weapons currently owned by this player, both loaded and unloaded.
     * @return this player's currently owned weapons, as list of {@link RemoteWeapon}.
     */
    public List<RemoteWeapon> getWeapons() {
        return weapons;
    }
    /**
     * Getter method for the power-ups currently owned by this player.
     * @return this player's currently owned power-ups, as list of {@link RemotePowerUp}.
     */
    public List<RemotePowerUp> getPowerUps() {
        return powerUps;
    }

    /**
     * Flag to check player's connection status, {@see this.isConnected}.
     * @return this player's connection status.
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Getter method for this player's physical token color.
     * @return this player's physical token color, codified as element of {@link PlayerColor}.
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public Token getToken() {
        return token;
    }

    /**
     * Getter method for the names of players who inflicted damage to this player since last scoring.
     * @return a list of names of players who inflicted damage to this player since last scoring.
     */
    public List<String> getDamage() {
        return damage;
    }
    /**
     * Getter method for the names of players who put markings on this player since last scoring.
     * @return a list of names of players who put markings on this player since last scoring.
     */
    public List<String> getMarkings() {
        return markings;
    }

    /**
     * Setter method for the names of players who inflicted damage to this player since last scoring.
     * @param damage a list of names of players who inflicted damage to this player since last scoring.
     */
    public void setDamage(List<String> damage) {
        this.damage = damage;
    }
    /**
     * Setter method for the names of players who put markings on this player since last scoring.
     * @param markings a list of names of players who put markings on this player since last scoring.
     */
    public void setMarkings(List<String> markings) {
        this.markings = markings;
    }

    /**
     * Setter method for the current value of score earned by this player since the beginning of the game.
     * @param score the score earned by this player since the beginning of the game.
     */
    public void setScore(int score) {
        this.score = score;
    }
    /**
     * Setter method for the position of this player, {@see this.position}.
     * @param position the current position of this player.
     */
    public void setPosition(int position) {
        this.position = position;
    }
    /**
     * Setter method for the current amount of death suffered by this player since the beginning of the game.
     * @param deathCount the current amount of death of this player.
     */
    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    /**
     * Setter method for the current amount of red ammo owned by this player.
     * @param ammo the current amount of red ammo owned by this player.
     */
    public void setRedAmmo(int ammo) {
        this.redAmmo = ammo;
    }
    /**
     * Setter method for the current amount of yellow ammo owned by this player.
     * @param ammo the current amount of yellow ammo owned by this player.
     */
    public void setYellowAmmo(int ammo) {
        this.yellowAmmo = ammo;
    }
    /**
     * Setter method for the current amount of blue ammo owned by this player.
     * @param ammo the current amount of blue ammo owned by this player.
     */
    public void setBlueAmmo(int ammo) {
        this.blueAmmo = ammo;
    }
    /**
     * Setter method for weapons currently owned by this player, codified as list of {@link RemoteWeapon}.
     * @param weapons the list of weapons currently owned by this player.
     */
    public void setWeapons(List<RemoteWeapon> weapons) {
        this.weapons = weapons;
    }
    /**
     * Setter method for power-ups currently owned by this player, codified as list of {@link RemotePowerUp}.
     * @param powerUps the list of power-ups currently owned by this player.
     */
    public void setPowerUps(List<RemotePowerUp> powerUps) {
        this.powerUps = powerUps;
    }

    /**
     * Setter method for the connection status of this player, {@see this.connected}.
     * @param connected the actual connection status of this player.
     */
    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}