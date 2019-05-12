package it.polimi.ingsw;

import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {


        Deck<Weapon> deck = Deck.generateWeapons();

        boolean keepDrawing = true;

        while(keepDrawing) {
            try {
                System.out.print(deck.draw().toString());
            } catch (EmptyDeckException e) {
                keepDrawing = false;
            }
        }
    }
}
