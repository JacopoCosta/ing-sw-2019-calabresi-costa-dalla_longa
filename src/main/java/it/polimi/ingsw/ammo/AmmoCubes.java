package it.polimi.ingsw.ammo;

public class AmmoCubes {
    private static final int MAX_AMMO_CUBES = 3; //maximum amount of cubes per colour

    private int red;
    private int yellow;
    private int blue;

    public AmmoCubes() {
        try {
            new AmmoCubes(0, 0, 0);
        } catch (Exception ignored) {}
    }

    public AmmoCubes(int red, int yellow, int blue) throws Exception {
        if(red < 0 || yellow < 0 || blue < 0 || red > MAX_AMMO_CUBES || yellow > MAX_AMMO_CUBES || blue > MAX_AMMO_CUBES)
            throw new Exception();

        this.red = red;
        this.yellow = yellow;
        this.blue = blue;
    }

    public static AmmoCubes build(String descriptor) {
        int red = Integer.parseInt(descriptor.substring(0, 0));
        int yellow = Integer.parseInt(descriptor.substring(1, 1));
        int blue = Integer.parseInt(descriptor.substring(2, 2));

        try {
            return new AmmoCubes(red, yellow, blue);
        }
        catch (Exception e) {
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

        try {
            return new AmmoCubes(red, yellow, blue);
        } catch (Exception ignored) {
            return null; // this exception will never occur
        }
    }

    // takes an amount of cubes away from this. Throws exception if
    public AmmoCubes take(AmmoCubes a) throws Exception {
        int red = this.getRed() - a.getRed();
        int yellow = this.getYellow() - a.getYellow();
        int blue = this.getBlue() - a.getBlue();

        if(red < 0 || yellow < 0 || blue < 0)
            throw new Exception();

        return new AmmoCubes(red, yellow, blue);
    }

    public boolean equals(AmmoCubes that) {
        return  this.getRed() == that.getRed() &&
                this.getYellow() == that.getYellow() &&
                this.getBlue() == that.getBlue();
    }

    public static AmmoCubes red() {
        try {
            return new AmmoCubes(1, 0, 0);
        } catch (Exception ignored) {
            return null; // this exception will never occur
        }
    }

    public static AmmoCubes yellow() {
        try {
            return new AmmoCubes(0, 1, 0);
        } catch (Exception ignored) {
            return null; // this exception will never occur
        }
    }

    public static AmmoCubes blue() {
        try {
            return new AmmoCubes(0, 0, 1);
        } catch (Exception ignored) {
            return null; // this exception will never occur
        }
    }
}
