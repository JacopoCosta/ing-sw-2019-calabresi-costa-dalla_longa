package it.polimi.ingsw.model.util.json;

import java.io.File;

/**
 * This class is used to generated adaptive paths to the {@code .json} files.
 */
public abstract class JsonPathGenerator {
    /**
     * The resources directory containing the {@code .json} files.
     */
    private static final String folderName = File.separator + "src" + File.separator + "resources" + File.separator + "json";

    /**
     * Creates a string with the path to a file, given its name.
     * @param fileName the name of the {@code .json} file.
     * @return the path to the file in the resources directory.
     */
    public static String getPath(String fileName) {
        return System.getProperty("user.dir") + folderName + File.separator + fileName;
    }
}
