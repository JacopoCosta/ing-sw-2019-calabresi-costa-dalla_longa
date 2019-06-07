package it.polimi.ingsw.view.remote.status;

import java.util.List;

public class RemotePlayer {

    private String name;

    private int score;      //the actual score of the player
    private int position;   //the current position of the player
    private int deathCount; //how many times this player has been killed

    private ColorCube ammo;         //the amount of ammo owned by ths player

    private PlayerColor playerColor;    //determines what physical token the player is using

    private List<RemoteWeapon> weapons;     //weapons owned by this player (both loaded and unloaded)
    private List<RemotePowerUp> powerUps;   //powerups owned by this player

    public RemotePlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getPosition() {
        return position;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public ColorCube getAmmo() {
        return ammo;
    }

    public List<RemoteWeapon> getWeapons() {
        return weapons;
    }

    public List<RemotePowerUp> getPowerUps() {
        return powerUps;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public void setAmmo(ColorCube ammo) {
        this.ammo = ammo;
    }

    public void setWeapons(List<RemoteWeapon> weapons) {
        this.weapons = weapons;
    }

    public void setPowerUps(List<RemotePowerUp> powerUps) {
        this.powerUps = powerUps;
    }

}