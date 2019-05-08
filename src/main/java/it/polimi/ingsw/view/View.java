package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;

public abstract class View {
    private Game game;

    public Game getGame() {
        return this.game;
    }
}
