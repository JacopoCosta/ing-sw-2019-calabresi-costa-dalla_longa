package it.polimi.ingsw;

import java.util.Collections;
import java.util.List;

public class Deck<T> {
    private List<T> cards;

    private Deck() {}

    public T draw() {
        return this.cards.remove(0);
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public static Deck<Weapon> generateWeapons() {
        return null;
    }

    public static Deck<PowerUp> generatePowerUps() {
        return null;
    }

    public static Deck<AmmoTile> generateAmmoTiles() {
        return null;
    }
}
