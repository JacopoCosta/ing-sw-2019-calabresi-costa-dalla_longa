package it.polimi.ingsw.util.json;

import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * A {@code DecoratedJsonObject} wraps a {@code JSONObject} and decorates it with some methods.
 */
@SuppressWarnings("unchecked")
public class DecoratedJsonObject {
    /**
     * The wrapped {@code JSONObject}.
     */
    private JSONObject content;

    /**
     * This constructor creates a new {@code DecoratedJsonObject} by wrapping an already existing
     * {@code JSONObject} passed as argument.
     * @param jsonObject the {@code JSONObject}.
     */
    public DecoratedJsonObject(JSONObject jsonObject) {
        content = jsonObject;
    }

    /**
     * This constructor creates a new empty {@code DecoratedJsonObject}.
     */
    public DecoratedJsonObject() {
        content = new JSONObject();
    }

    /**
     * This constructor creates a new {@code DecoratedJsonObject} by wrapping a
     * new {@code JSONObject} defined as a key-value pair. Both the key and the value
     * are passed in as arguments.
     * @param key the key, this is used to reference the {@code JSONObject}.
     * @param value the informative content of the {@code JSONObject}.
     */
    public DecoratedJsonObject(String key, Object value) {
        content = new JSONObject();
        content.put(key, value);
    }

    /**
     * Returns the content of the current instance.
     * @return the wrapped {@code JSONObject}.
     */
    JSONObject unpack() {
        return content;
    }

    /**
     * Creates and returns a new {@code DecoratedJsonObject} by opening a {@code .json} file
     * found at a specific location, and parsing it.
     * @param file the {@code .json} file ito read from.
     * @return a new {@code DecoratedJsonObject} with all the content read from the file.
     */
    static DecoratedJsonObject getFromFile(File file) {
        JSONParser parser = new JSONParser();

        try {
            return new DecoratedJsonObject((JSONObject) parser.parse(new FileReader(file)));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            throw new JsonException("Could not find file to read.");
        }
    }

    /**
     * Overwrites a {@code .json} file at a specific location with the content of the {@code JSONObject}
     * using {@code json} syntax.
     * @param file the file to write to.
     */
    public void writeToFile(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new JsonException("Could not find file to write.");
        }
    }

    /**
     * Adds a new key-value pair to the current instance, where the value is a {@link DecoratedJsonArray}.
     * @param key the name of the entry.
     * @param value a {@link DecoratedJsonArray} containing the information to associate with the key.
     */
    public void putArray(String key, DecoratedJsonArray value) {
        content.put(key, value.unpack());
    }

    /**
     * Adds a new key-value pair to the current instance, where the value is another instance of this class.
     * @param key the name of the entry.
     * @param value a {@link DecoratedJsonArray} containing the information to associate with the key.
     */
    public void putObject(String key, DecoratedJsonObject value) {
        content.put(key, value.content);
    }

    /**
     * Adds a new key-value pair to the current instance, where the value is of a primitive type.
     * @param key the name of the entry.
     * @param value a primitive-type object to associate with the key.
     */
    public void putValue(String key, Object value) {
        content.put(key, value);
    }

    /**
     * Tells whether the current object is empty.
     * @return {@code true} if it's empty.
     */
    public boolean isEmpty() {
        return content == null;
    }

    /**
     * Fetches the {@link DecoratedJsonArray} associated with a key inside {@code this}.
     * @param key the label identifying the array.
     * @return the array, if found.
     * @throws JullPointerException when no such label as {@code key} was defined inside {@code this}.
     */
    public DecoratedJsonArray getArray(String key) throws JullPointerException{
        if(content == null)
            throw new JullPointerException("Array of objects not found.");
        return new DecoratedJsonArray((JSONArray) this.content.get(key));
    }

    /**
     * Fetches the {@code DecoratedJsonObject} associated with a key inside {@code this}.
     * @param key the label identifying the object.
     * @return the object, if found.
     * @throws JullPointerException when no such label as {@code key} was defined inside {@code this}.
     */
    public DecoratedJsonObject getObject(String key) throws JullPointerException {
        if(content == null)
            throw new JullPointerException("Object not found.");
        return new DecoratedJsonObject((JSONObject) this.content.get(key));
    }

    /**
     * Fetches the string associated with a key inside {@code this}.
     * @param key the label identifying the string.
     * @return the string, if found.
     * @throws JullPointerException when no such label as {@code key} was defined inside {@code this}.
     */
    public String getString(String key) throws JullPointerException{
        if(content == null)
            throw new JullPointerException("String not found.");
        return (String) this.content.get(key);
    }

    /**
     * Fetches the number associated with a key inside {@code this}.
     * @param key the label identifying the number.
     * @return the integer value of the number, if found.
     * @throws JullPointerException when no such label as {@code key} was defined inside {@code this}.
     */
    public int getInt(String key) throws JullPointerException {
            if(content == null)
                throw new JullPointerException("Integer not found.");
            return ((Long) this.content.get(key)).intValue();
    }

    /**
     * Fetches the boolean associated with a key inside {@code this}.
     * @param key the label identifying the boolean flag.
     * @return the value of the boolean flag, if found.
     * @throws JullPointerException when no such label as {@code key} was defined inside {@code this}.
     */
    public boolean getBoolean(String key) throws JullPointerException {
        if(content == null)
            throw new JullPointerException("Boolean not found.");
        return (Boolean) this.content.get(key);
    }
}