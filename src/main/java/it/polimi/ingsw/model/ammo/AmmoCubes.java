package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

/**
 * This class represents the game's currency. Ammo cubes come in three colours (red, yellow, blue). Players
 * can individually hold only up to three cubes for each colour. Any further transaction that would lead to
 * this rule being broken will be ignored, leaving the player with still three cubes of the involved colour.
 * A player can also never be in debt of these, meaning negative values are not accepted.
 */
public class AmmoCubes {
    /**
     * The maximum amount of cubes per colour contained in each set of ammo cubes.
     */
    private static final int MAX_AMMO_CUBES = 3;

    /**
     * The amount of red cubes in the current set.
     */
    private int red;

    /**
     * The amount of yellow cubes in the current set.
     */
    private int yellow;

    /**
     * The amount of blue cubes in the current set.
     */
    private int blue;

    /**
     * This is the default constructor. It instantiates the equivalent of no ammo cubes.
     */
    public AmmoCubes() {
        new AmmoCubes(0, 0, 0);
    }

    /**
     * This constructor allows to specify the amount of cubes for each individual colour.
     * @param red the amount of red cubes
     * @param yellow the amount of yellow cubes
     * @param blue the amount of blue cubes
     * @throws IllegalArgumentException when trying to create an illegal number of cubes, such as beyond three, or a negative number.
     */
    public AmmoCubes(int red, int yellow, int blue) throws IllegalArgumentException {
        if(red < 0 || yellow < 0 || blue < 0 || red > MAX_AMMO_CUBES || yellow > MAX_AMMO_CUBES || blue > MAX_AMMO_CUBES)
            throw new IllegalArgumentException();

        this.red = red;
        this.yellow = yellow;
        this.blue = blue;
    }

    /**
     * This factory method constructs an object with the properties found inside the JSON object passed as argument.
     * @param jAmmoCube the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     * @throws IllegalArgumentException when the properties include values not suitable for the explicit constructor.
     * @see AmmoCubes#AmmoCubes(int, int, int)
     */
    public static AmmoCubes build(DecoratedJSONObject jAmmoCube) throws IllegalArgumentException {
        int red = jAmmoCube.getInt("red");
        int yellow = jAmmoCube.getInt("yellow");
        int blue = jAmmoCube.getInt("blue");

        return new AmmoCubes(red, yellow, blue);
    }

    /**
     * This method tells the amount of red cubes in the current set.
     * @return the amount of red cubes in the current set.
     */
    private int getRed() {
        return red;
    }

    /**
     * This method tells the amount of yellow cubes in the current set.
     * @return the amount of yellow cubes in the current set.
     */
    private int getYellow() {
        return yellow;
    }

    /**
     * This method tells the amount of blue cubes in the current set.
     * @return the amount of blue cubes in the current set.
     */
    private int getBlue() {
        return blue;
    }

    /**
     * This methods sums the values of two sets of ammo cubes. The sum is evaluated on each colour individually
     * and differently coloured cubes do not interfere with each other or with the final result. Should any of the
     * colours end up having more than three cubes, the final result will be truncated to have at most that many.
     * @param a the other set of ammo cubes that needs to be summed with the current set.
     * @return a new object where each colour's amount is equal to the sum of that same colour's amounts in the addends, limited to three.
     */
    public AmmoCubes sum(AmmoCubes a) {
        int red = Math.min(MAX_AMMO_CUBES, this.getRed() + a.getRed());
        int yellow = Math.min(MAX_AMMO_CUBES, this.getYellow() + a.getYellow());
        int blue = Math.min(MAX_AMMO_CUBES, this.getBlue() + a.getBlue());
        return new AmmoCubes(red, yellow, blue);
    }

    /**
     * This methods subtracts the values of a sets of ammo cubes from the values of another set. The difference is evaluated on each colour individually
     * and differently coloured cubes do not interfere with each other or with the final result. Should any of the
     * colours end up having a negative number of cubes, the operation is aborted and an exception is thrown.
     * @param a the other set of ammo cubes that needs to be subtracted from the current set.
     * @return a new object where each colour's amount is equal to the difference between that same colour's amount in the object this method was
     * called upon and that same colour's amount in the object passed as argument.
     * @throws CannotAffordException when trying to take away more ammo cubes than there actually are for at least one colour.
     */
    public AmmoCubes take(AmmoCubes a) throws CannotAffordException {
        int red = this.getRed() - a.getRed();
        int yellow = this.getYellow() - a.getYellow();
        int blue = this.getBlue() - a.getBlue();

        if(red < 0 || yellow < 0 || blue < 0)
            throw new CannotAffordException("Attempted to take more ammo cubes than available.");

        return new AmmoCubes(red, yellow, blue);
    }

    /**
     * This method tells if two sets of ammo cubes are worth the same.
     * For this purpose, equal amounts of different colours are not comparable, implying that,
     * in order for two instances of this class to be considered equivalent, they must have equal amounts
     * of cubes for every colour.
     * @param that the set of ammo cubes to compare with.
     * @return whether or not the object this method was called upon is equivalent to the one passed as argument.
     */
    public boolean equals(AmmoCubes that) {
        return  this.getRed() == that.getRed() &&
                this.getYellow() == that.getYellow() &&
                this.getBlue() == that.getBlue();
    }

    /**
     * This method instantiates a set of ammo cubes equivalent to a single red cube.
     * @return a set of ammo cubes containing only a red cube.
     */
    public static AmmoCubes red() {
        return new AmmoCubes(1, 0, 0);
    }

    /**
     * This method instantiates a set of ammo cubes equivalent to a single yellow cube.
     * @return a set of ammo cubes containing only a yellow cube.
     */
    public static AmmoCubes yellow() {
        return new AmmoCubes(0, 1, 0);
    }

    /**
     * This method instantiates a set of ammo cubes equivalent to a single blue cube.
     * @return a set of ammo cubes containing only a blue cube.
     */
    public static AmmoCubes blue() {
        return new AmmoCubes(0, 0, 1);
    }

    /**
     * This methods is used to inspect an object.
     * @return a summary of the object's attributes.
     */
    public String toString() {
        boolean addComma = false;
        String s = "";
        if(red > 0) {
            addComma = true;
            s += red + " red";
        }
        if(yellow > 0) {
            if(addComma)
                s += ", ";
            addComma = true;
            s += yellow + " yellow";
        }
        if(blue > 0) {
            if(addComma)
                s += ", ";
            s += blue + " blue";
        }
        if(s.equals(""))
            return "free";
        return s;
    }

    /**
     * This methods is used to inspect a set of ammo cubes containing exactly one cube.
     * @return the name of the colour of the only cube in the set (or the empty string, if the usage criterion is not met).
     */
    public String toStringAsColor() {
        if(red + yellow + blue == 1) {
            if(red == 1)
                return "red";
            if(yellow == 1)
                return "yellow";
            return "blue";
        }
        return "";
    }
}
