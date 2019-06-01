package it.polimi.ingsw.model.utilities;

import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DecoratedJsonObject {
    private JSONObject content;

    public DecoratedJsonObject(JSONObject jsonObject) {
        content = jsonObject;
    }

    public DecoratedJsonObject() {
        content = new JSONObject();
    }

    public DecoratedJsonObject(String key, Object value) {
        content = new JSONObject();
        content.put(key, value);
    }

    JSONObject unpack() {
        return content;
    }

    public static DecoratedJsonObject getFromFile(String path) {
        JSONParser parser = new JSONParser();

        try {
            return new DecoratedJsonObject((JSONObject) parser.parse(new FileReader(path)));
        } catch (ParseException | IOException e) {
            e.printStackTrace();
            throw new JsonException("Could not find file to read.");
        }
    }

    public void writeToFile(String path) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(content.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new JsonException("Could not find file to write.");
        }
    }

    public void putArray(String key, DecoratedJsonArray value) {
        content.put(key, value.unpack());
    }

    public void putObject(String key, DecoratedJsonObject value) {
        content.put(key, value.content);
    }

    public void putValue(String key, Object value) {
        content.put(key, value);
    }

    public boolean isEmpty() {
        return content == null;
    }

    public DecoratedJsonArray getArray(String key) throws JullPointerException{
        if(content == null)
            throw new JullPointerException("Array of objects not found.");
        return new DecoratedJsonArray((JSONArray) this.content.get(key));
    }

    public DecoratedJsonObject getObject(String key) throws JullPointerException {
        if(content == null)
            throw new JullPointerException("Object not found.");
        return new DecoratedJsonObject((JSONObject) this.content.get(key));
    }

    public String getString(String key) throws JullPointerException{
        if(content == null)
            throw new JullPointerException("String not found.");
        return (String) this.content.get(key);
    }

    public int getInt(String key) throws JullPointerException {
            if(content == null)
                throw new JullPointerException("Integer not found.");
            return ((Long) this.content.get(key)).intValue();
    }

    public boolean getBoolean(String key) throws JullPointerException {
        if(content == null)
            throw new JullPointerException("Boolean not found.");
        return (Boolean) this.content.get(key);
    }
}