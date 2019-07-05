package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.weaponry.Weapon;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * This class tests {@link Board} methods.
 */
public class TestBoard {

    /**
     * This method tests {@link Board#fetchWeapon(String)} the fetching of weapons during {@link Board#generate(Game, int)} method,
     * i.e. the loading of any weapon from Json resources.
     */
    @Test
    public void fetchWeapon() {
        Game game = Game.create(false, 1, 1, new ArrayList<>());
        Board board = game.getBoard();

        /*
            Tests the fetching of a correct, existing weapon
         */
        Weapon lockRifle = board.fetchWeapon("Lock Rifle").orElse(null);
        assertNotNull(lockRifle);

        /*
            Tests the fetching of a correct, existing weapon
         */
        Weapon plasmaGun = board.fetchWeapon("Plasma Gun").orElse(null);
        assertNotNull(plasmaGun);

        /*
            Tests the fetching of a non-existing weapon
         */
        Weapon nonExistentWeapon = board.fetchWeapon("Nyan Cat Launcher").orElse(null);
        assertNull(nonExistentWeapon);

        /*
            Tests the fetching of a weapon which has already been fetched on this game
         */
        Weapon lockRifle2 = board.fetchWeapon("Lock Rifle").orElse(null);
        assertNull(lockRifle2);
    }
}
