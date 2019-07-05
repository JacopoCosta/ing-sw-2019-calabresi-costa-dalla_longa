package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.Player;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class makes possible an integration test of the {@link Game} behaviour. There are no {@code Assert}, but
 * no exception must be thrown by neither of its tests in a correct game round.
 * Also, every new game is completely randomized in its parameters (such as "Final Frenzy" mode or number of players)
 * in order to ensure the most robust behaviour.
 */
public class TestGame {

    @Before
    public void setUp() {
        Game.autoPilot = true;
        Game.offlineMode = true;
        Game.silent = true;
    }

    /**
     * This test runs a game with specified parameters: five players, largest possible map, Final Frenzy rule, five rounds.
     */
    @Test
    public void autoPlay() {
        List<String> participantNames = Arrays.asList("Aldo", "Giovanni", "Giacomo", "Luca", "Paolo");
        List<Player> participants = participantNames.stream()
                .map(Player::new)
                .collect(Collectors.toList());

        //Change these parameters
        Game game = Game.create(true, 5, 4, participants);
        game.play();
    }

    /**
     * This test runs a game with runtime randomized parameters: 3-5 players, random map, randomized Final Frenzy rule, 1-8 rounds.
     */
    @Test
    public void autoPlayRandomized() {
        List<Player> participants = new ArrayList<>();

        int randomPlayerCount = (int) Math.floor(Math.random() * 3) + 3; // between 3 and 5

        for(int i = 1; i <= randomPlayerCount; i ++)
            participants.add(new Player("Player" + i));

        boolean randomFrenzy = Math.random() < 0.5;
        int randomGameLength = (int) Math.floor(Math.random() * 8) + 1; // between 1 and 8
        int randomBoardType = (int) Math.floor(Math.random() * 4) + 1; // between 1 and 4

        Game game = Game.create(randomFrenzy, randomGameLength, randomBoardType, participants);
        game.play();
    }

    /**
     * This test runs for 16 times {@link TestGame#autoPlayRandomized()} test.
     */
    @Test
    public void autoPlayLargeNumbers() {
        for(int i = 0; i < 16; i ++)
            autoPlayRandomized();
    }
}
