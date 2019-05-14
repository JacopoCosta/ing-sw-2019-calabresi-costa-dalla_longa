package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.utilities.PathGenerator;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck<T> {
    private List<T> cards;
    private List<T> discarded;

    private Deck() {
        this.cards = new ArrayList<>();
        this.discarded = new ArrayList<>();
    }

    public T draw() throws EmptyDeckException {
        if(this.cards.size() == 0)
            throw new EmptyDeckException("Can't draw from an empty deck.");
        return this.cards.remove(0);
    }

    public void discard(T card) {
        discarded.add(card);
    }

    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public void regenerate() {
        this.cards = this.discarded;
        this.discarded.clear();
    }

    public static Deck<Weapon> generateWeapons() {
        Deck<Weapon> deck = new Deck<>();

        DecoratedJSONObject jDeck = DecoratedJSONObject.getFromFile(PathGenerator.getPath("weapons.json"));
        for(DecoratedJSONObject jWeapon : jDeck.getArray("weapons").asList()) {
            deck.cards.add(Weapon.build(jWeapon));
        }
        return deck;
    }

    public static Deck<PowerUp> generatePowerUps() {
        Deck<PowerUp> deck = new Deck<>();

        DecoratedJSONObject jDeck = DecoratedJSONObject.getFromFile(PathGenerator.getPath("powerUps.json"));
        for(DecoratedJSONObject jPowerUp : jDeck.getArray("powerUps").asList()) {
            deck.cards.add(PowerUp.build(jPowerUp));
        }
        return deck;
    }

    public static Deck<AmmoTile> generateAmmoTiles() {
        Deck<AmmoTile> deck = new Deck<>();

        DecoratedJSONObject jDeck = DecoratedJSONObject.getFromFile(PathGenerator.getPath("ammoTiles.json"));
        for(DecoratedJSONObject jAmmoTile : jDeck.getArray("ammoTiles").asList()) {
            deck.cards.add(AmmoTile.build(jAmmoTile));
        }
        return deck;
    }
}
