package it.polimi.ingsw.model.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DecoratedJSONObject {
    private JSONObject common;

    protected DecoratedJSONObject(JSONObject jsonObject) {
        this.common = jsonObject;
    }

    public static DecoratedJSONObject getFromFile(String path) {
        JSONParser parser = new JSONParser();

        try {
            return new DecoratedJSONObject((JSONObject) parser.parse(new FileReader(path)));
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DecoratedJSONArray getArray(String key) {
        return new DecoratedJSONArray((JSONArray) this.common.get(key));
    }

    public DecoratedJSONObject getObject(String key) {
        return new DecoratedJSONObject((JSONObject) this.common.get(key));
    }

    public String getString(String key) {
        return (String) this.common.get(key);
    }

    public int getInt(String key) {
        return ((Long) this.common.get(key)).intValue();
    }

    public boolean getBoolean(String key) {
        return (Boolean) this.common.get(key);
    }
}
