package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.powerups.*;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;
import it.polimi.ingsw.model.utilities.JsonPathGenerator;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.*;
import java.util.function.Function;

import static it.polimi.ingsw.model.Game.autoPilot;

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

    public void discard(T card) throws CannotDiscardFirstCardOfDeckException {
        if(card == null)
            throw new CannotDiscardFirstCardOfDeckException("Attempted to discard null.");
        discarded.add(card);
    }

    void shuffle() {
        if(autoPilot)
            return;
        Collections.shuffle(this.cards);
    }

    void regenerate() {
        this.cards.addAll(this.discarded);
        this.discarded.clear();
    }

    public Optional<T> smartDraw(boolean autoRegenerate) {
        T card;
        try {
            card = draw();
        } catch (EmptyDeckException e) {
            if(autoRegenerate) {
                regenerate();
                shuffle();
                try {
                    card = draw();
                } catch (EmptyDeckException fatal) {
                    throw new CorruptedDeckException("Can't regenerate deck.");
                }
            }
            else return Optional.empty();
        }
        return Optional.of(card);
    }

    static Deck<Weapon> generateWeapons() {
        Deck<Weapon> deck = new Deck<>();

        DecoratedJsonObject jDeck = DecoratedJsonObject.getFromFile(JsonPathGenerator.getPath("weapons.json"));
        try {
            for(DecoratedJsonObject jWeapon : jDeck.getArray("weapons").asList()) {
                deck.cards.add(Weapon.build(jWeapon));
            }
        } catch (JullPointerException e) {
            throw new JsonException("Weapons JSON does not include \"weapons\" as top-level object.");
        }
        return deck;
    }

    static Deck<PowerUp> generatePowerUps() {
        Deck<PowerUp> deck = new Deck<>();

        List<AmmoCubes> unitCubes = new ArrayList<>();
        unitCubes.add(AmmoCubes.red());
        unitCubes.add(AmmoCubes.yellow());
        unitCubes.add(AmmoCubes.blue());

        unitCubes
                .forEach( // two per colour per type (total 2 * 3 * 4 = 24)
                        color -> {
                            deck.cards.add(new Grenade(color));
                            deck.cards.add(new Grenade(color));
                            deck.cards.add(new Newton(color));
                            deck.cards.add(new Newton(color));
                            deck.cards.add(new Scope(color));
                            deck.cards.add(new Scope(color));
                            deck.cards.add(new Teleport(color));
                            deck.cards.add(new Teleport(color));
                        }
                );

        return deck;
    }

    static Deck<AmmoTile> generateAmmoTiles() {
        Deck<AmmoTile> deck = new Deck<>();

        List<AmmoCubes> unitCubes = new ArrayList<>();
        unitCubes.add(AmmoCubes.red());
        unitCubes.add(AmmoCubes.yellow());
        unitCubes.add(AmmoCubes.blue());

        unitCubes.stream()
                .peek(
                        u -> unitCubes.stream()
                                .filter(other -> !other.equals(u))
                                .map(other -> other.sum(other).sum(u))
                                .forEach( // three per colour per pair of equal colours (total 2 * 3 * 3 = 18)
                                        s -> unitCubes.forEach(r -> deck.cards.add(new AmmoTile(s, false)))
                                )
                )
                .map(
                        u -> unitCubes.stream()
                        .map(u::sum)
                )
                .flatMap(Function.identity())
                .forEach( // two per colour per colour (total 2 * 3 * 3 = 18)
                        s -> {
                            deck.cards.add(new AmmoTile(s, true));
                            deck.cards.add(new AmmoTile(s, true));
                        }
                );

        return deck;
    }

    @Override
    public String toString() {
        return "[" + Table.list(cards) + "] discarded(" + Table.list(discarded) + ")";
    }
}
