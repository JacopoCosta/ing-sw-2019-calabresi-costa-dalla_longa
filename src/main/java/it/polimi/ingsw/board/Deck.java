package it.polimi.ingsw.board;

import it.polimi.ingsw.ammo.AmmoTile;
import it.polimi.ingsw.powerups.PowerUp;
import it.polimi.ingsw.weaponry.Weapon;

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
        Deck<Weapon> deck = new Deck<>();
        deck.cards.add(Weapon.build("")); // ... strings loaded from file, this statement will be in a loop
        return deck;
    }

    public static Deck<PowerUp> generatePowerUps() {
        Deck<PowerUp> deck = new Deck<>();
        deck.cards.add(PowerUp.build("")); // ...
        return deck;
    }

    public static Deck<AmmoTile> generateAmmoTiles() {
        Deck<AmmoTile> deck = new Deck<>();
        deck.cards.add(AmmoTile.build("")); // ...
        return deck;
    }
}
