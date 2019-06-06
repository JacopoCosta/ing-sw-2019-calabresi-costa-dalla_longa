package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.util.json.JsonObjectGenerator;
import it.polimi.ingsw.model.weaponry.Weapon;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class TestDeck {

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
