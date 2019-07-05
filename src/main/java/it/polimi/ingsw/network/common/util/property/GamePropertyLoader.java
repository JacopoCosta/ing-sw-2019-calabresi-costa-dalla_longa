package it.polimi.ingsw.network.common.util.property;

import it.polimi.ingsw.network.common.exceptions.InvalidPropertyException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * This is a utility class used to load the {@link GameProperty} from its location into the {@code game.cfg} file.
 *
 * @see GameProperty
 */
public class GamePropertyLoader {
    /**
     * The {@code game.cfg} file path.
     */
    private static final String CONFIG_FILE_PATH = "/config/game.cfg";

    /**
     * The sequence of characters representing a start of line to be ignored. Any line starting with this exact
     * sequence of characters will be interpreted as a comment and will be ignored.
     */
    private static final String IGNORE_SEQUENCE = "#";

    /**
     * The prefix indicating the {@link #finalFrenzy} flag.
     */
    private static final String FINAL_FRENZY_SEQUENCE = "final frenzy";

    /**
     * The prefix indicating the {@link #roundsToPlay} attribute.
     */
    private static final String ROUNDS_TO_PLAY_SEQUENCE = "rounds to play";

    /**
     * The prefix indicating the {@link #boardType} attribute.
     */
    private static final String BOARD_TYPE_SEQUENCE = "board type";

    /**
     * The prefix indicating the {@link #turnDuration} attribute.
     */
    private static final String TURN_DURATION_SEQUENCE = "turn duration";

    /**
     * The lower bound for the {@link #roundsToPlay} attribute.
     */
    private static final int MINIMUM_ROUNDS_TO_PLAY_VALUE = 5;

    /**
     * The upper bound for the {@link #roundsToPlay} attribute.
     */
    private static final int MAXIMUM_ROUNDS_TO_PLAY_VALUE = 8;

    /**
     * The lower bound for the {@link #boardType} attribute.
     */
    private static final int MINIMUM_BOARD_TYPE_VALUE = 1;

    /**
     * The upper bound for the {@link #boardType} attribute.
     */
    private static final int MAXIMUM_BOARD_TYPE_VALUE = 4;

    /**
     * The upper bound for the {@link #turnDuration} attribute.
     */
    private static final int MAXIMUM_TURN_DURATION_VALUE = 180;

    /**
     * The lower bound for the {@link #turnDuration} attribute.
     */
    private static final int MINIMUM_TURN_DURATION_VALUE = 10;

    /**
     * The flag containing the {@link GameProperty} {@code finalFrenzy} value.
     */
    private boolean finalFrenzy;

    /**
     * The attribute containing the {@link GameProperty} {@code roundsToPlay} value.
     */
    private int roundsToPlay;

    /**
     * The attribute containing the {@link GameProperty} {@code boardType} value.
     */
    private int boardType;

    /**
     * The attribute containing the {@link GameProperty} {@code turnDuration} value.
     */
    private int turnDuration;

    /**
     * The {@link Scanner} responsible for the actual reading of the {@link GameProperty} values from the configuration file.
     */
    private Scanner scanner;

    /**
     * This is the only constructor. It creates a new {@code GamePropertyLoader} and initiate the scanner in order to
     * read the configuration file.
     */
    public GamePropertyLoader() {
        try {
            this.scanner = new Scanner(new FileInputStream(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().toString() + CONFIG_FILE_PATH));
            //this.scanner = new Scanner(new FileInputStream(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).toString() + CONFIG_FILE_PATH));
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Reads the {@link GameProperty} from the configuration file and returns a new instance of them after verifying
     * that the read values verifies all the corresponding requirements and are, as a result, valid.
     *
     * @return the {@link GameProperty} read from the configuration file.
     * @throws InvalidPropertyException if some properties does not verify the corresponding requirements.
     */
    public GameProperty loadGameProperties() throws InvalidPropertyException {
        while (this.scanner.hasNextLine()) {
            String line = this.scanner.nextLine();

            if (line.length() > 0 && !line.startsWith(IGNORE_SEQUENCE)) {
                String prefix = line.substring(0, line.indexOf(" = "));
                String description = line.substring(line.indexOf(" = ") + 3);

                switch (prefix) {
                    case FINAL_FRENZY_SEQUENCE:
                        this.finalFrenzy = this.getFinalFrenzyProperty(description);
                        break;
                    case ROUNDS_TO_PLAY_SEQUENCE:
                        this.roundsToPlay = this.getRoundsToPlayProperty(description);
                        break;
                    case BOARD_TYPE_SEQUENCE:
                        this.boardType = this.getBoardTypeProperty(description);
                        break;
                    case TURN_DURATION_SEQUENCE:
                        this.turnDuration = this.getTurnDurationProperty(description);
                        break;
                    default:
                        throw new InvalidPropertyException("\"" + prefix + "\" is not a valid property.");
                }
            }
        }
        return new GameProperty(this.finalFrenzy, this.roundsToPlay, this.boardType, this.turnDuration);
    }

    /**
     * Returns the boolean flag corresponding to the given {@code String} representation, after verifying that
     * the given {@code description} satisfies all the corresponding requirements.
     *
     * @param description the {@code String} representation of the {@link #finalFrenzy} property.
     * @return the corresponding {@link #finalFrenzy} property.
     * @throws InvalidPropertyException if the corresponding requirements are not satisfied.
     */
    private boolean getFinalFrenzyProperty(String description) throws InvalidPropertyException {
        try {
            return Integer.parseInt(description) != 0;
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException("\"" + description + "\" is not a valid parameter for property \"" + FINAL_FRENZY_SEQUENCE + "\"");
        }
    }

    /**
     * Returns the integer value corresponding to the given {@code String} representation for the {@link #roundsToPlay} property,
     * after verifying that the given {@code description} satisfies all the corresponding requirements.
     *
     * @param description the {@code String} representation of the {@link #roundsToPlay} property.
     * @return the corresponding {@link #roundsToPlay} property.
     * @throws InvalidPropertyException if the corresponding requirements are not satisfied.
     * @see #getIntegerProperty(String, String, int, int)
     */
    private int getRoundsToPlayProperty(String description) throws InvalidPropertyException {
        return this.getIntegerProperty(description, ROUNDS_TO_PLAY_SEQUENCE, MINIMUM_ROUNDS_TO_PLAY_VALUE, MAXIMUM_ROUNDS_TO_PLAY_VALUE);
    }

    /**
     * Returns the integer value corresponding to the given {@code String} representation for the {@link #boardType} property,
     * after verifying that the given {@code description} satisfies all the corresponding requirements.
     *
     * @param description the {@code String} representation of the {@link #boardType} property.
     * @return the corresponding {@link #boardType} property.
     * @throws InvalidPropertyException if the corresponding requirements are not satisfied.
     * @see #getIntegerProperty(String, String, int, int)
     */
    private int getBoardTypeProperty(String description) throws InvalidPropertyException {
        return this.getIntegerProperty(description, BOARD_TYPE_SEQUENCE, MINIMUM_BOARD_TYPE_VALUE, MAXIMUM_BOARD_TYPE_VALUE);
    }

    /**
     * Returns the integer value corresponding to the given {@code String} representation for the {@link #turnDuration} property,
     * after verifying that the given {@code description} satisfies all the corresponding requirements.
     *
     * @param description the {@code String} representation of the {@link #turnDuration} property.
     * @return the corresponding {@link #turnDuration} property.
     * @throws InvalidPropertyException if the corresponding requirements are not satisfied.
     * @see #getIntegerProperty(String, String, int, int)
     */
    private int getTurnDurationProperty(String description) throws InvalidPropertyException {
        return this.getIntegerProperty(description, TURN_DURATION_SEQUENCE, MINIMUM_TURN_DURATION_VALUE, MAXIMUM_TURN_DURATION_VALUE);
        // I swapped the last two arguments (hoping it would help)
    }

    /**
     * Returns the integer value corresponding to the given {@code String} representation, after verifying that
     * the given {@code description} satisfies all the corresponding requirements.
     *
     * @param description  the {@code String} representation of the requested property.
     * @param propertyName the {@code String} sequence to refer to the given property.
     * @param lowerBound   the minimum value accepted for the given property.
     * @param upperBound   the maximum value accepted for the given property.
     * @return the property corresponding to the given {@code description}.
     * @throws InvalidPropertyException if the corresponding requirements are not satisfied.
     */
    private int getIntegerProperty(String description, String propertyName, int lowerBound, int upperBound) throws InvalidPropertyException {
        int value;

        try {
            value = Integer.parseInt(description);
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException("\"" + description + "\" is not a valid parameter for property \"" + propertyName + "\"");
        }

        if (value < lowerBound || value > upperBound)
            throw new InvalidPropertyException(propertyName + " parameter not in range " + lowerBound + "-" + upperBound + ", found: " + value);

        return value;
    }
}