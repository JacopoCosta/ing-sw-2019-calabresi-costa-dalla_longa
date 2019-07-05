package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.util.json.JsonObjectGenerator;
import it.polimi.ingsw.model.weaponry.Weapon;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * This class tests {@link Deck} methods.
 */
public class TestDeck {

    /**
     * This methods tests the {@link Deck#draw()} of a card by a full Deck of {@link Weapon}. Since draw method throws
     * a {@link EmptyDeckException} when the Deck is out of elements, this tests is passed if such exception is not thrown.
     */
    @Test
    public void draw() {
        Deck<Weapon> deck = Deck.generateWeapons(JsonObjectGenerator.getWeaponDeckBuilder());

        boolean catchTaken = false;

        try {
            deck.draw();
        } catch (EmptyDeckException ignored) {
            catchTaken = true;
        }

        assertFalse(catchTaken);
    }
}
