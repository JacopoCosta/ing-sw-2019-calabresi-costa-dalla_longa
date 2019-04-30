package it.polimi.ingsw.model.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Parsing { // custom-made library of methods for loading and parsing files
    public static String readFromFile(String path) throws IOException {
        byte[] content = Files.readAllBytes(Paths.get(path));
        return new String(content);
    }

    public static String[] prepareForFactory(String fileString) {
        fileString = fileString.replaceAll("\\s", ""); // remove all whitespace characters
        fileString = fileString.replaceAll("_", " "); // replace all underscores with spaces
        return fileString.split(":"); // split on every colon
    }
}
