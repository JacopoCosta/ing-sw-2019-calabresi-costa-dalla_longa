package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck<T> {
    private List<T> cards;

    private Deck() {
        this.cards = new ArrayList<>();
    }

    public T draw() {
        return this.cards.remove(0);
    }
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public static Deck<Weapon> generateWeapons() {
        Deck<Weapon> deck = new Deck<>();


        DecoratedJSONObject jDeck = DecoratedJSONObject.getFromFile("src\\main\\json\\weapons.json");

        for(DecoratedJSONObject jWeapon : jDeck.getArray("weapons").asList()) {
            deck.cards.add(Weapon.build(jWeapon));
        }
        return deck;
    }

    public static Deck<PowerUp> generatePowerUps() {
        Deck<PowerUp> deck = new Deck<>();
        return deck;
    }

    public static Deck<AmmoTile> generateAmmoTiles() {
        Deck<AmmoTile> deck = new Deck<>();
        return deck;
    }
}
