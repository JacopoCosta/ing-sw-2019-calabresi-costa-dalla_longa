package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

public class AmmoCubes {
    private static final int MAX_AMMO_CUBES = 3; //maximum amount of cubes per colour

    private int red;
    private int yellow;
    private int blue;

    public AmmoCubes() {
        new AmmoCubes(0, 0, 0);
    }

    public AmmoCubes(int red, int yellow, int blue) throws IllegalArgumentException {
        if(red < 0 || yellow < 0 || blue < 0 || red > MAX_AMMO_CUBES || yellow > MAX_AMMO_CUBES || blue > MAX_AMMO_CUBES)
            throw new IllegalArgumentException();

        this.red = red;
        this.yellow = yellow;
        this.blue = blue;
    }

    public static AmmoCubes build(DecoratedJSONObject jAmmoCube) {
        int red = jAmmoCube.getInt("red");
        int yellow = jAmmoCube.getInt("yellow");
        int blue = jAmmoCube.getInt("blue");

        try {
            return new AmmoCubes(red, yellow, blue);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getRed() {
        return red;
    }

    private int getYellow() {
        return yellow;
    }

    private int getBlue() {
        return blue;
    }

    public AmmoCubes sum(AmmoCubes a) {
        int red = Math.min(MAX_AMMO_CUBES, this.getRed() + a.getRed());
        int yellow = Math.min(MAX_AMMO_CUBES, this.getYellow() + a.getYellow());
        int blue = Math.min(MAX_AMMO_CUBES, this.getBlue() + a.getBlue());
        return new AmmoCubes(red, yellow, blue);
    }

    // takes an amount of cubes away from this. Throws exception if
    public AmmoCubes take(AmmoCubes a) throws CannotAffordException {
        int red = this.getRed() - a.getRed();
        int yellow = this.getYellow() - a.getYellow();
        int blue = this.getBlue() - a.getBlue();

        if(red < 0 || yellow < 0 || blue < 0)
            throw new CannotAffordException("Attempted to take more ammo cubes than available.");

        return new AmmoCubes(red, yellow, blue);
    }

    public boolean equals(AmmoCubes that) {
        return  this.getRed() == that.getRed() &&
                this.getYellow() == that.getYellow() &&
                this.getBlue() == that.getBlue();
    }

    public static AmmoCubes red() {
        return new AmmoCubes(1, 0, 0);
    }

    public static AmmoCubes yellow() {
        return new AmmoCubes(0, 1, 0);
    }

    public static AmmoCubes blue() {
        return new AmmoCubes(0, 0, 1);
    }
}
