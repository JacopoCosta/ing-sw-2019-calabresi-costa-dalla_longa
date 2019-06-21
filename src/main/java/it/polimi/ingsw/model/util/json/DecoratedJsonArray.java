package it.polimi.ingsw.model.util.json;

import it.polimi.ingsw.model.exceptions.JsonException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code DecoratedJsonArray} is a wrapper class that encapsulates a
 * {@code JSONArray}, which is a collection of {@code JSONObject}s, and decorates it
 * with methods.
 */
public class DecoratedJsonArray {
    /**
     * The {@code JSONArray} represented by the instance.
     */
    private JSONArray content;

    /**
     * This constructor builds a {@code DecoratedJsonArray} from a list of {@link DecoratedJsonObject}s.
     * @param content the list.
     * @param uselessObject this parameter is added to the signature of this method
     *                      in order to allow the {@link DecoratedJsonObject#getArray(String)} method to distinguish
     *                      the two constructors of this class. Not including it would not have allowed the program to
     *                      build. This parameter should always be set to {@code null}, otherwise a {@link JsonException}
     *                      will be thrown.
     */
    public DecoratedJsonArray(List<DecoratedJsonObject> content, Object uselessObject) {
        if(uselessObject != null)
            throw new JsonException("Useless object should always be null.");
        this.content = new JSONArray();
        content.stream()
                .map(DecoratedJsonObject::unpack)
                .forEach(o -> this.content.add(o));
    }

    /**
     * This constructor creates a new {@code DecoratedJsonArray} by wrapping an already existing {@code JSONArray}.
     * @param jsonArray the {@code JSONArray}.
     */
    DecoratedJsonArray(JSONArray jsonArray) {
        content = jsonArray;
    }

    /**
     * Returns the wrapped {@code JSONArray}.
     * @return the wrapped {@code JSONArray}.
     */
    JSONArray unpack() {
        return content;
    }

    /**
     * Returns a {@link DecoratedJsonObject} wrapping the {@code JSONObject} contained in the {@code JSONArray}
     * wrapped by {@code this}, at a specified position.
     * @param index the position.
     * @return the {@link DecoratedJsonObject}.
     */
    public DecoratedJsonObject get(int index) {
        return new DecoratedJsonObject((JSONObject) this.content.get(index));
    }

    /**
     * Separates all the items contained in the {@code JSONArray} wrapped by the instance and returns
     * them in a {@code List}.
     * @return the {@code List}.
     */
    public List<DecoratedJsonObject> toList() {

        List<DecoratedJsonObject> list = new ArrayList<>();

        for(Object o : content) {
            list.add(new DecoratedJsonObject((JSONObject) o));
        }

        return list;
    }
}