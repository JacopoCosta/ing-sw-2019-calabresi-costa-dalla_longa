package it.polimi.ingsw.view.remote.status;

import java.util.List;

public class RemotePlayer {

    private String name;

    private int score;
    private int XPos;
    private int Ypos;
    private int deathCount;

    private ColorCube ammo;

    private List<RemoteWeapon> weapons;
    private List<RemotePowerUp> powerUps;

    private int[] scoreBoard;

    public RemotePlayer(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public int getXPos() {
        return XPos;
    }

    public int getYpos() {
        return Ypos;
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

    public int[] getScoreBoard() {
        return scoreBoard;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setXPos(int XPos) {
        this.XPos = XPos;
    }

    public void setYpos(int ypos) {
        Ypos = ypos;
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

    public void setScoreBoard(int[] scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

}