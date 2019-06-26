package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.powerups.*;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;
import it.polimi.ingsw.model.util.Table;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.*;
import java.util.function.Function;

import static it.polimi.ingsw.model.Game.autoPilot;

/**
 * {@code Deck}s are ordered collections of objects from which it is only possible to remove, and then read, one object at a time.
 * @param <T> the type of object that makes up the "cards" of the {@code Deck}.
 */
public class Deck<T> {
    /**
     * The list of cards inside the {@code Deck} (only the first of which is accessible).
     * @see Deck#draw()
     */
    private List<T> cards;

    /**
     * The list of cards that had been drawn before and then discarded.
     * @see Deck#discard(Object)
     */
    private List<T> discarded;

    /**
     * This is the only constructor. It creates an empty {@code Deck}.
     */
    private Deck() {
        this.cards = new ArrayList<>();
        this.discarded = new ArrayList<>();
    }

    /**
     * Removes the first card of the {@code Deck} and returns it.
     * @return the removed card (no longer part of the {@code Deck} once drawn).
     * @throws EmptyDeckException when there are no cards left to draw.
     */
    public T draw() throws EmptyDeckException {
        if(this.cards.size() == 0)
            throw new EmptyDeckException("Can't draw from an empty deck.");
        return this.cards.remove(0);
    }

    /**
     * Adds a card to the discard pile.
     * @param card the card to discard.
     * @throws CannotDiscardFirstCardOfDeckException when attempting to discard before having drawn anything,
     * therefore resulting in an attempt to discard {@code null};
     */
    public void discard(T card) throws CannotDiscardFirstCardOfDeckException {
        if(card == null)
            throw new CannotDiscardFirstCardOfDeckException("Attempted to discard null.");
        discarded.add(card);
    }

    /**
     * Shuffles the order of the cards in the {@code Deck}.
     */
    void shuffle() {
        if(autoPilot)
            return;
        Collections.shuffle(this.cards);
    }

    /**
     * Moves all the discarded object to the cards, allowing them to be drawn once again each.
     */
    void regenerate() {
        this.cards.addAll(this.discarded);
        this.discarded.clear();
    }

    /**
     * Attempts to draw from the {@code Deck}. If the draw was successful, an {@code Optional} containing the drawn card is returned
     * If, instead, the draw failed because the {@code Deck} is empty, if {@code autoRegenerate} is false, an empty optional is returned.
     * Otherwise, if {@code autoRegenerate} is true, the {@code Deck} is regenerated from the discard pile and shuffled,
     * then a second attempt to draw a card occurs. If the {@code Deck} is empty again, an empty optional is returned, while if
     * a card was successfully drawn, it is returned as optional.
     * @param autoRegenerate whether or not to regenerate and shuffle for a second attempt after a possible failure.
     * @return Either an empty optional, or an optional containing a card, as described above.
     */
    public Optional<T> smartDraw(boolean autoRegenerate) {
        T card;
        try {
            card = draw(); // try to draw a card
        } catch (EmptyDeckException e) { // if the deck is empty
            if(autoRegenerate) {
                regenerate();
                shuffle(); // create a new shuffled deck from the discarded pile
                try {
                    card = draw(); // retry as above
                } catch (EmptyDeckException fatal) { // there is no deck
                    return Optional.empty();
                }
            }
            else return Optional.empty();
        }
        return Optional.of(card);
    }

    /**
     * This factory method constructs a {@code Deck} of {@link Weapon} cards, with the properties found inside the JSON object passed as argument.
     * @param jDeck the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     */
    static Deck<Weapon> generateWeapons(DecoratedJsonObject jDeck) {
        Deck<Weapon> deck = new Deck<>();

        try {
            for(DecoratedJsonObject jWeapon : jDeck.getArray("weapons").toList()) {
                deck.cards.add(Weapon.build(jWeapon));
            }
        } catch (JullPointerException e) {
            throw new JsonException("Weapons JSON does not include \"weapons\" as top-level object.");
        }
        return deck;
    }

    /**
     * Generates a {@code Deck} of 24 {@link PowerUp} cards: two copies per colour per type (2×3×4=24)
     * @return a new {@code Deck} of {@link PowerUp}s.
     */
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

    /**
     * Generates a {@code Deck} of 36 {@link AmmoTile} cards:<br>
     * 18 without a {@link PowerUp}: three copies per colour per pair of equal colours (2×3×3=18)<br>
     * 18 with a {@link PowerUp}: two copies per colour per colour (2×3×3=18).
     * @return a new {@code Deck} of {@link AmmoTile}s.
     */
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

    /**
     * Creates a string containing a concatenation of the results of calling {@code toString()} on each element in the {@code Deck}.
     * @return the string.
     */
    @Override
    public String toString() {
        return "[" + Table.list(cards) + "] discarded(" + Table.list(discarded) + ")";
    }
}
