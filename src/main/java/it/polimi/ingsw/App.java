package it.polimi.ingsw;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.utilities.PathGenerator;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.io.File;

/**
 * Hello world! (yay)
 */
public class App {
    public static void main(String[] args) {

        AmmoCubes ac = new AmmoCubes(0, 2, 3);
        System.out.println(PathGenerator.getPath("weapons.json"));

        Deck<Weapon> deck1 = Deck.generateWeapons();
        Deck<PowerUp> deck2 = Deck.generatePowerUps();
        Deck<AmmoTile> deck3 = Deck.generateAmmoTiles();

        deck1.shuffle();
        deck2.shuffle();
        deck3.shuffle();

        boolean keepDrawing = true;
        while (keepDrawing) {
            try {
                System.out.print(deck1.draw().toString());
            } catch (EmptyDeckException e) {
                keepDrawing = false;
            }
        }
        System.out.print("\n");
        keepDrawing = true;
        while (keepDrawing) {
            try {
                System.out.print(deck2.draw().toString());
            } catch (EmptyDeckException e) {
                keepDrawing = false;
            }
        }
        System.out.print("\n");
        keepDrawing = true;
        while (keepDrawing) {
            try {
                System.out.print(deck3.draw().toString());
            } catch (EmptyDeckException e) {
                keepDrawing = false;
            }
        }

    }
}
