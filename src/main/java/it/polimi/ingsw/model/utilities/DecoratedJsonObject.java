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
    private JSONObject common;

    public DecoratedJsonObject(JSONObject jsonObject) {
        common = jsonObject;
    }

    public DecoratedJsonObject() {
        common = new JSONObject();
    }

    public DecoratedJsonObject(String key, Object value) {
        common = new JSONObject();
        common.put(key, value);
    }

    public JSONObject unpack() {
        return common;
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
            fileWriter.write(common.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new JsonException("Could not find file to write.");
        }
    }

    public void putArray(String key, DecoratedJsonArray value) {
        common.put(key, value.unpack());
    }

    public void putObject(String key, DecoratedJsonObject value) {
        common.put(key, value.common);
    }

    public void putValue(String key, Object value) {
        common.put(key, value);
    }

    public DecoratedJsonArray getArray(String key) {
        return new DecoratedJsonArray((JSONArray) this.common.get(key));
    }

    public DecoratedJsonObject getObject(String key) {
        return new DecoratedJsonObject((JSONObject) this.common.get(key));
    }

    public String getString(String key) {
        return (String) this.common.get(key);
    }

    public int getInt(String key) throws JullPointerException {
            if(common == null)
                throw new JullPointerException("");
            return ((Long) this.common.get(key)).intValue();
    }

    public boolean getBoolean(String key) {
        return (Boolean) this.common.get(key);
    }
}