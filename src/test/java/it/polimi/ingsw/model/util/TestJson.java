package it.polimi.ingsw.model.util;

import it.polimi.ingsw.model.Game;
import org.junit.Test;

import java.util.ArrayList;

public class TestJson {

    @Test
    public void saveAndLoad() {
        Game game = Game.create(false ,0, 0, new ArrayList<>());
        game.save();
        game.load();
    }
}
