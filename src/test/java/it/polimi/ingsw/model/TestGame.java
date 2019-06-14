package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestGame {

    @Before
    public void setUp() {
        Game.autoPilot = true;
        Game.offlineMode = true;
        Game.silent = true;
    }

    @Test
    public void autoPlay() {
        List<Player> participants = new ArrayList<>();

        for(int i = 1; i <= 5; i ++)
            participants.add(new Player("Player" + i));

        Game game = Game.create(true, 8, 4, participants);
        game.play();
    }

    @Test
    public void autoPlayRandomized() {
        List<Player> participants = new ArrayList<>();

        int randomPlayerCount = (int) Math.floor(Math.random() * 6) + 3; // between 3 and 8

        for(int i = 1; i <= randomPlayerCount; i ++)
            participants.add(new Player("Player" + i));

        boolean randomFrenzy = Math.random() < 0.5;
        int randomGameLength = (int) Math.floor(Math.random() * 8) + 1; // between 1 and 8
        int randomBoardType = (int) Math.floor(Math.random() * 4) + 1; // between 1 and 4

        Game game = Game.create(randomFrenzy, randomGameLength, randomBoardType, participants);
        game.play();
    }

    @Test
    public void autoPlayLargeNumbers() {
        for(int i = 0; i < 32; i ++)
            autoPlayRandomized();
    }
}
