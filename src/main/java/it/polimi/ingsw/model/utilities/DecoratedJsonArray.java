package it.polimi.ingsw.model.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DecoratedJSONArray {
    private JSONArray common;

    protected DecoratedJSONArray(JSONArray jsonArray) {
        this.common = jsonArray;
    }

    public DecoratedJSONObject get(int index) {
        return new DecoratedJSONObject((JSONObject) this.common.get(index));
    }

    public List<DecoratedJSONObject> asList() {
        List<DecoratedJSONObject> list = new ArrayList<>();

        for(Object o : common) {
            list.add(new DecoratedJSONObject((JSONObject) o));
        }

        return list;
    }
}