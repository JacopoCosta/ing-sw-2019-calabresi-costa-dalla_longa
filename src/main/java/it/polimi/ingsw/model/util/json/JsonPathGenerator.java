package it.polimi.ingsw.model.util.json;

import java.io.File;

public abstract class JsonPathGenerator {
    private static final String folderName = File.separator + "src" + File.separator + "resources" + File.separator + "json";
    public static String getPath(String fileName) {
        return System.getProperty("user.dir") + folderName + File.separator + fileName;
    }
}
