package it.polimi.ingsw.model.utilities;

import java.io.File;

public abstract class JsonPathGenerator {
    public static final String folderName = File.separator + "resources" + File.separator + "json";
    public static String getPath(String fileName) {
        return System.getProperty("user.dir") + folderName + File.separator + fileName;
    }
}
