package it.polimi.ingsw.network.common.util.property;

import it.polimi.ingsw.network.common.exceptions.InvalidPropertyException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class GamePropertyLoader {
    private static final String CONFIG_FILE_PATH = System.getProperty("user.dir") +
            File.separator + "src" +
            File.separator + "resources" +
            File.separator + "config" +
            File.separator + "game.cfg";

    private static final String IGNORE_SEQUENCE = "#";
    private static final String FINAL_FRENZY_SEQUENCE = "final frenzy";
    private static final String ROUNDS_TO_PLAY_SEQUENCE = "rounds to play";
    private static final String BOARD_TYPE_SEQUENCE = "board type";

    private static final int MINIMUM_ROUNDS_TO_PLAY_VALUE = 5;
    private static final int MAXIMUM_ROUNDS_TO_PLAY_VALUE = 8;

    private static final int MINIMUM_BOARD_TYPE_VALUE = 1;
    private static final int MAXIMUM_BOARD_TYPE_VALUE = 4;

    private boolean finalFrenzy;
    private int roundsToPlay;
    private int boardType;

    private Scanner scanner;

    public GamePropertyLoader() {
        File file = new File(CONFIG_FILE_PATH);

        try {
            this.scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public GameProperty readGameProperties() throws InvalidPropertyException {
        while (this.scanner.hasNextLine()) {
            String line = this.scanner.nextLine();

            if (line.length() > 0 && !line.startsWith(IGNORE_SEQUENCE)) {
                String prefix = line.substring(0, line.indexOf(" = "));
                String description = line.substring(line.indexOf(" = ") + 3);

                switch (prefix) {
                    case FINAL_FRENZY_SEQUENCE:
                        this.finalFrenzy = getFinalFrenzyProperty(description);
                        break;
                    case ROUNDS_TO_PLAY_SEQUENCE:
                        this.roundsToPlay = getRoundsToPlayProperty(description);
                        break;
                    case BOARD_TYPE_SEQUENCE:
                        this.boardType = getBoardTypeProperty(description);
                        break;
                    default:
                        throw new InvalidPropertyException("\"" + prefix + "\" is not a valid property.");
                }
            }
        }

        return new GameProperty(this.finalFrenzy, this.roundsToPlay, this.boardType);
    }

    private boolean getFinalFrenzyProperty(String description) throws InvalidPropertyException {
        try {
            return Integer.parseInt(description) != 0;
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException("\"" + description + "\" is not a valid parameter for property \"" + FINAL_FRENZY_SEQUENCE + "\"");
        }
    }

    private int getRoundsToPlayProperty(String description) throws InvalidPropertyException {
        int value;

        try {
            value = Integer.parseInt(description);
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException("\"" + description + "\" is not a valid parameter for property \"" + ROUNDS_TO_PLAY_SEQUENCE + "\"");
        }

        if (value < MINIMUM_ROUNDS_TO_PLAY_VALUE || value > MAXIMUM_ROUNDS_TO_PLAY_VALUE)
            throw new InvalidPropertyException(ROUNDS_TO_PLAY_SEQUENCE + " parameter not in range " + MINIMUM_ROUNDS_TO_PLAY_VALUE + "-" + MAXIMUM_ROUNDS_TO_PLAY_VALUE + ", found: " + value);

        return value;
    }

    private int getBoardTypeProperty(String description) throws InvalidPropertyException {
        int value;

        try {
            value = Integer.parseInt(description);
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException("\"" + description + "\" is not a valid parameter for property \"" + BOARD_TYPE_SEQUENCE + "\"");
        }

        if (value < MINIMUM_BOARD_TYPE_VALUE || value > MAXIMUM_BOARD_TYPE_VALUE)
            throw new InvalidPropertyException(BOARD_TYPE_SEQUENCE + " parameter not in range " + MINIMUM_BOARD_TYPE_VALUE + "-" + MAXIMUM_BOARD_TYPE_VALUE  + ", found: " + value);

        return value;
    }
}