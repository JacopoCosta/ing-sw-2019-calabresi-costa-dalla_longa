package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;

public class VirtualDispatcher {
    private static final String FATAL_ERROR = "You should never see this message. If you do, run for your life!";

    private Game game;

    public VirtualDispatcher(Game game) {
        this.game = game;
    }

    public void sendMessage(Player recipient, String message) {

    }

    public void broadcast(String message) {
        game.getParticipants().forEach(p -> sendMessage(p, message));
    }
}
