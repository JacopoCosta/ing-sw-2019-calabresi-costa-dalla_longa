package it.polimi.ingsw.model.util.json;


public abstract class JsonObjectGenerator {
    public static DecoratedJsonObject getWeaponDeckBuilder() {
        return DecoratedJsonObject.getFromFile(JsonPathGenerator.getPath("weapons.json"));
    }

    public static DecoratedJsonObject getBoardBuilder() {
        return DecoratedJsonObject.getFromFile(JsonPathGenerator.getPath("boards.json"));
    }
}
