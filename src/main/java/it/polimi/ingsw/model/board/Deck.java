package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.utilities.Parsing;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.io.IOException;
import java.util.ArrayList;
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
        String fileString = "";
        try {
            fileString = Parsing.readFromFile(""); // gather weapon cfg from file
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] descriptorsArray = Parsing.prepareForFactory(fileString); // prepare for parsing in the factory

        List<String> descriptors = new ArrayList<>();
        for(String s : descriptorsArray) {
            if(s.equals("W")) {
                deck.cards.add(Weapon.build(descriptors));
                descriptors.clear();
            }
            else
                descriptors.add(s);
        }
        return deck;
    }

    public static Deck<PowerUp> generatePowerUps() {
        Deck<PowerUp> deck = new Deck<>();
        String fileString = "";
        try {
            fileString = Parsing.readFromFile(""); // gather weapon cfg from file
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] descriptorsArray = Parsing.prepareForFactory(fileString); // prepare for parsing in the factory

        for(String s : descriptorsArray)
            deck.cards.add(PowerUp.build(s));
        return deck;
    }

    public static Deck<AmmoTile> generateAmmoTiles() {
        Deck<AmmoTile> deck = new Deck<>();
        String fileString = "";
        try {
            fileString = Parsing.readFromFile(""); // gather weapon cfg from file
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] descriptorsArray = Parsing.prepareForFactory(fileString); // prepare for parsing in the factory

        for(String s : descriptorsArray)
            deck.cards.add(AmmoTile.build(s));

        return deck;
    }
}
