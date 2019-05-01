package it.polimi.ingsw.model.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DecoratedJSONArray {
    private JSONArray common;

    protected DecoratedJSONArray(JSONArray jsonArray) {
        this.common = jsonArray;
    }

    public DecoratedJSONObject get(int index) {
        return new DecoratedJSONObject((JSONObject) this.common.get(index));
    }
}
