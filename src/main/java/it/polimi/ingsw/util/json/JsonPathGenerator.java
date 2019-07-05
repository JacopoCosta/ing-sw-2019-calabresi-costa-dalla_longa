package it.polimi.ingsw.util.json;

import java.io.File;
import java.io.InputStream;

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
    static InputStream getInputStream(String fileName) {
        return (JsonPathGenerator.class.getResourceAsStream(folderPath + fileName));
    }

    /**
     * Returns a {@code File} from the given {@code fileName}.
     *
     * @param fileName the name of the {@code File} to return.
     * @return a {@code File} from the given {@code fileName}.
     */
    public static File getFile(String fileName) {
        return new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + "classes" + File.separator + "json" + File.separator + fileName);
    }
}
