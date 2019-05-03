package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.weaponry.Weapon;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class TestDeck {

    @Test
    public void draw() {
        Deck<Weapon> deck = Deck.generateWeapons();

        boolean catchTaken = false;

        try {
            deck.draw();
        } catch (Exception ignored) {
            catchTaken = true;
        }

        assertFalse(catchTaken);
    }
}