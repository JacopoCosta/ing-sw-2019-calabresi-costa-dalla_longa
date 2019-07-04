package it.polimi.ingsw.util.json;

import java.io.File;
import java.net.URISyntaxException;

/**
 * This class is used to generated adaptive paths to the {@code .json} files.
 */
public abstract class JsonPathGenerator {
    /**
     * The resources directory containing the {@code .json} files.
     */
    private static final String folderPath = "/json/";

    /**
     * Creates a string with the path to a file, given its name.
     *
     * @param fileName the name of the {@code .json} file.
     * @return the path to the file in the resources directory.
     */
    public static File getFile(String fileName) {
        try {
            return new File((JsonPathGenerator.class.getResource(folderPath + fileName)).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
