package it.polimi.ingsw.model.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DecoratedJsonArray {
    private JSONArray common;

    public DecoratedJsonArray(List<?> content, Object uselessObject) {
        common = new JSONArray();
        common.addAll(content);
    }

    public DecoratedJsonArray(JSONArray jsonArray) {
        common = jsonArray;
    }

    public DecoratedJsonObject get(int index) {
        return new DecoratedJsonObject((JSONObject) this.common.get(index));
    }

    public List<DecoratedJsonObject> asList() {
        List<DecoratedJsonObject> list = new ArrayList<>();

        for(Object o : common) {
            list.add(new DecoratedJsonObject((JSONObject) o));
        }

        return list;
    }
}