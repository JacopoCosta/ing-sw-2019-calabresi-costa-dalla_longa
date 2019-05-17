package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.powerups.*;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;
import it.polimi.ingsw.model.utilities.PathGenerator;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.*;
import java.util.function.Function;

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

        List<AmmoCubes> unitCubes = new ArrayList<>();
        unitCubes.add(AmmoCubes.red());
        unitCubes.add(AmmoCubes.yellow());
        unitCubes.add(AmmoCubes.blue());

        Arrays.stream(PowerUpType.values())
                .forEach(
                        type -> unitCubes
                        .forEach(
                                unit -> {
                                    switch (type) {
                                        case GRENADE:
                                            deck.cards.add(new Grenade(unit));
                                            deck.cards.add(new Grenade(unit));
                                            break;
                                        case NEWTON:
                                            deck.cards.add(new Newton(unit));
                                            deck.cards.add(new Newton(unit));
                                            break;
                                        case SCOPE:
                                            deck.cards.add(new Scope(unit));
                                            deck.cards.add(new Scope(unit));
                                            break;
                                        case TELEPORT:
                                            deck.cards.add(new Teleport(unit));
                                            deck.cards.add(new Teleport(unit));
                                            break;
                                        default:
                                            break;
                                    }
                                }
                        )
                );

        return deck;
    }

    public static Deck<AmmoTile> generateAmmoTiles() {
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
                                .forEach(
                                        s -> {
                                            unitCubes.forEach(r -> deck.cards.add(new AmmoTile(s, false)));
                                        }
                                )
                )
                .map(
                        u -> unitCubes.stream()
                        .map(u::sum)
                )
                .flatMap(Function.identity())
                .forEach(
                        s -> {
                            deck.cards.add(new AmmoTile(s, true));
                            deck.cards.add(new AmmoTile(s, true));
                        }
                );

        return deck;
    }
}
