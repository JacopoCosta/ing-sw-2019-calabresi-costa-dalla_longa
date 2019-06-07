package it.polimi.ingsw.model.util.json;

import it.polimi.ingsw.model.exceptions.JsonException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DecoratedJsonArray {
    private JSONArray content;

    public DecoratedJsonArray(List<DecoratedJsonObject> content, Object uselessObject) {
        if(uselessObject != null)
            throw new JsonException("Useless object should always be null.");
        this.content = new JSONArray();
        content.stream()
                .map(DecoratedJsonObject::unpack)
                .forEach(o -> this.content.add(o));
    }

    DecoratedJsonArray(JSONArray jsonArray) {
        content = jsonArray;
    }

    JSONArray unpack() {
        return content;
    }

    public DecoratedJsonObject get(int index) {
        return new DecoratedJsonObject((JSONObject) this.content.get(index));
    }

    public List<DecoratedJsonObject> toList() {

        List<DecoratedJsonObject> list = new ArrayList<>();

        for(Object o : content) {
            list.add(new DecoratedJsonObject((JSONObject) o));
        }

        return list;
    }
}