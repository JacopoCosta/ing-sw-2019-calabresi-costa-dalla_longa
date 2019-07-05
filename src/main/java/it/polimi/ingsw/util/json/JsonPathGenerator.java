package it.polimi.ingsw.util.json;

import java.io.File;

/**
 * This class is used to generated adaptive paths to the {@code .json} files.
 */
public abstract class JsonPathGenerator {
    /**
     * Returns a {@code File} from the given {@code fileName}.
     *
     * @param fileName the name of the {@code File} to return.
     * @return a {@code File} from the given {@code fileName}.
     */
    public static File getFile(String fileName) {
        return new File(System.getProperty("user.dir") + File.separator + "server" + File.separator + "json" + File.separator + fileName);
    }
}
