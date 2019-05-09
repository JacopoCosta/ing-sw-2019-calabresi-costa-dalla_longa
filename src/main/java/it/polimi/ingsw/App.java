package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Aldo"));
        players.add(new Player("Giovanni"));
        players.add(new Player("Giacomo"));
        players.add(new Player("Luca"));
        players.add(new Player("Paolo"));


        Game game = new Game(true, 8, 2, players);

        for(Player player: players)
            player.spawn(game.getBoard().findSpawnPoint(AmmoCubes.red()));



    }
}
