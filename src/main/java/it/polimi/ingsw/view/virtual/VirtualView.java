package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;

public class VirtualView {
    private static final String FATAL_ERROR = "You should never see this message. If you do, run for your life!";

    private Game game;

    public VirtualView(Game game) {
        this.game = game;
    }

    public void sendMessage(Player recipient, String message) {

    }

    public void broadcast(String message) {
        game.getParticipants().forEach(p -> sendMessage(p, message));
    }
}
