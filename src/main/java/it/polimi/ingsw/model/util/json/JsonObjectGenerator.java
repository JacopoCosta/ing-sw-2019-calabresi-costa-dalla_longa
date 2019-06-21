package it.polimi.ingsw.model.util.json;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * This class is a collection of methods, each of which returns a {@link DecoratedJsonObject}
 * containing all information needed to build a particular object used in the {@link Game}.
 */
public abstract class JsonObjectGenerator {
    /**
     * Used when building the {@link Weapon} {@link Deck}
     * @return a {@link DecoratedJsonObject} containing all the information necessary to build it.
     */
    public static DecoratedJsonObject getWeaponDeckBuilder() {
        return DecoratedJsonObject.getFromFile(JsonPathGenerator.getPath("weapons.json"));
    }

    /**
     * Used when building the {@link Board}.
     * @return a {@link DecoratedJsonObject} containing all the information necessary to build it.
     */
    public static DecoratedJsonObject getBoardBuilder() {
        return DecoratedJsonObject.getFromFile(JsonPathGenerator.getPath("boards.json"));
    }

    /**
     * Used when trying to load a {@link Game} from a save state.
     * @return a {@link DecoratedJsonObject} containing all the information necessary to restore the game status.
     */
    public static DecoratedJsonObject getSavedGameBuilder() {
        return DecoratedJsonObject.getFromFile(JsonPathGenerator.getPath("saved.json"));
    }
}
