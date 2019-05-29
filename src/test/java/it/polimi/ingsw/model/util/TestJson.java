package it.polimi.ingsw.model.util;

import it.polimi.ingsw.model.Game;
import org.junit.Test;

import java.util.ArrayList;

public class TestJson {

    @Test
    public void save() {
        Game game = Game.create(false ,2, 2, new ArrayList<>());
        game.save();
    }

    @Test
    public void load() {
        Game game = Game.create(false, 2, 2, new ArrayList<>());
        game.load();
    }
}
