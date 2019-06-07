package it.polimi.ingsw.model.ammo;

import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;

import java.util.List;
import java.util.stream.Collectors;

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
    public static AmmoCubes build(DecoratedJsonObject jAmmoCube) throws IllegalArgumentException {
        int red;
        try {
            red = jAmmoCube.getInt("red");
        } catch (JullPointerException e) {
            throw new JsonException("AmmoCubes red not found.");
        }
        int yellow;
        try {
            yellow = jAmmoCube.getInt("yellow");
        } catch (JullPointerException e) {
            throw new JsonException("AmmoCubes yellow not found.");
        }
        int blue;
        try {
            blue = jAmmoCube.getInt("blue");
        } catch (JullPointerException e) {
            throw new JsonException("AmmoCubes blue not found.");
        }
        return new AmmoCubes(red, yellow, blue);
    }

    /**
     * This method tells the amount of red cubes in the current set.
     * @return the amount of red cubes in the current set.
     */
    public int getRed() {
        return red;
    }

    /**
     * This method tells the amount of yellow cubes in the current set.
     * @return the amount of yellow cubes in the current set.
     */
    public int getYellow() {
        return yellow;
    }

    /**
     * This method tells the amount of blue cubes in the current set.
     * @return the amount of blue cubes in the current set.
     */
    public int getBlue() {
        return blue;
    }

    /**
     * This methods sums the values of two sets of ammo cubes. The sum is evaluated on each colour individually
     * and differently coloured cubes do not interfere with each other or with the final result. Should any of the
     * colours end up having more than three cubes, the final result will be truncated to have at most that many.
     * @param ammoCubes the other set of ammo cubes that needs to be summed with the current set.
     * @return a new object where each colour's amount is equal to the sum of that same colour's amounts in the addends, limited to three.
     */
    public AmmoCubes sum(AmmoCubes ammoCubes) {
        int red = Math.min(MAX_AMMO_CUBES, this.getRed() + ammoCubes.getRed());
        int yellow = Math.min(MAX_AMMO_CUBES, this.getYellow() + ammoCubes.getYellow());
        int blue = Math.min(MAX_AMMO_CUBES, this.getBlue() + ammoCubes.getBlue());
        return new AmmoCubes(red, yellow, blue);
    }

    /**
     * This methods subtracts the values of a sets of ammo cubes from the values of another set. The difference is evaluated on each colour individually
     * and differently coloured cubes do not interfere with each other or with the final result. Should any of the
     * colours end up having a negative number of cubes, the operation is aborted and an exception is thrown.
     * @param ammoCubes the other set of ammo cubes that needs to be subtracted from the current set.
     * @return a new object where each colour's amount is equal to the difference between that same colour's amount in the object this method was
     * called upon and that same colour's amount in the object passed as argument.
     * @throws CannotAffordException when trying to take away more ammo cubes than there actually are for at least one colour.
     */
    public AmmoCubes take(AmmoCubes ammoCubes) throws CannotAffordException {
        int red = this.getRed() - ammoCubes.getRed();
        int yellow = this.getYellow() - ammoCubes.getYellow();
        int blue = this.getBlue() - ammoCubes.getBlue();

        if(red < 0 || yellow < 0 || blue < 0)
            throw new CannotAffordException("Attempted to take more ammo cubes than available.");

        return new AmmoCubes(red, yellow, blue);
    }

    /**
     * Tells if a set of ammo cubes covers a cost expressed in ammo cubes.
     * The condition for coverage is having, for each colour, at least as many cubes of that colour as the cost.
     * @param cost the cost to cover.
     * @return whether or not the coverage condition is fulfilled.
     */
    public boolean covers(AmmoCubes cost) {
        return  this.getRed() >= cost.getRed() &&
                this.getYellow() >= cost.getYellow() &&
                this.getBlue() >= cost.getBlue();
    }

    /**
     * Combines the purchasing power of a set of ammo cubes with that of a list of power ups.
     * This is because each power up translates into a single ammo cube of a specific colour.
     * @param powerUps the power ups to add to the set of ammo cubes.
     * @return A new set of ammo cubes equivalent to the combination of the current set with the list of power ups.
     */
    public AmmoCubes augment(List<PowerUp> powerUps) {
        return powerUps.stream()
                .map(PowerUp::getAmmoCubes)
                .reduce(this, AmmoCubes::sum);
    }

    /**
     * Tells how far a set of ammo cubes is from being able to cover a cost, passed in as argument.
     * @param cost the cost to cover.
     * @return A set of ammo cubes containing, for each colour, the minimum amount of ammo cubes of that colour that
     * would need to be added to the current set, in order to allow it to cover that cost.
     * @see AmmoCubes#covers(AmmoCubes)
     */
    private AmmoCubes differenceFromCovering(AmmoCubes cost) {
        return new AmmoCubes(
                Math.max(0, cost.red - this.red),
                Math.max(0, cost.yellow - this.yellow),
                Math.max(0, cost.blue - this.blue)
        );
    }

    /**
     * Filters a list of power ups, allowing through only those power ups that, should the current
     * set of ammo cubes be augmented with, would reduce the difference from covering the cost passed as argument.
     * @param powerUps the list of power ups from which to filter.
     * @param cost the cost to cover.
     * @return the filtered list of power ups.
     * @see AmmoCubes#augment(List)
     * @see AmmoCubes#differenceFromCovering(AmmoCubes)
     */
    public List<PowerUp> filterValidAugmenters(List<PowerUp> powerUps, AmmoCubes cost) {
        // this == the player's base balance to cover the cost
        // only power-ups that "make the difference" should pass the filter

        return powerUps.stream()
                .filter(powerUp -> this.differenceFromCovering(cost).covers(powerUp.getAmmoCubes()))
                .collect(Collectors.toList());
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
     * This method tells if two sets of ammo cubes are worth the same.
     * For this purpose, equal amounts of different colours are not comparable, implying that,
     * in order for two instances of this class to be considered equivalent, they must have equal amounts
     * of cubes for every colour.
     * @param object the set of ammo cubes to compare with.
     * @return whether or not the object this method was called upon is equivalent to the one passed as argument.
     */
    @Override
    public boolean equals(Object object) {
        if(object == null)
            return false;
        if(!(object instanceof AmmoCubes))
            return false;
        AmmoCubes ammoCubes = ((AmmoCubes) object);
        return  this.getRed() == ammoCubes.getRed() &&
                this.getYellow() == ammoCubes.getYellow() &&
                this.getBlue() == ammoCubes.getBlue();
    }

    /**
     * This methods is used to inspect an object.
     * @return a summary of the object's attributes.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        final int[] values = {red, yellow, blue};
        final String[] names = {"R", "Y", "B"};
        for(int i = 0; i < 3; i ++) {
            for(int j = 0; j < values[i]; j ++)
                s.append(names[i]);
        }
        return s.toString();
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
        return super.toString();
    }
}
