package it.polimi.ingsw;

import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.weaponry.Weapon;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) {

        Deck<Weapon> myDeck = Deck.generateWeapons();

        System.out.println(myDeck.draw().getName());
    }
}
