package it.polimi.ingsw.util;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.exceptions.InvalidSaveStateException;
import it.polimi.ingsw.model.exceptions.UnmatchedSavedParticipantsException;
import it.polimi.ingsw.model.player.Player;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class TestJson {

    @Test
    public void invalidateSaveState() {
        Game game = Game.create(false ,2, 2, new ArrayList<>());
        game.invalidateSaveState();
    }

    @Test
    public void save() {
        List<Player> participants = new ArrayList<>();
        participants.add(new Player("Aldo"));
        participants.add(new Player("Giovanni"));
        participants.add(new Player("Giacomo"));
        participants.add(new Player("Roberto"));

        Game game = Game.create(false ,2, 2, participants);
        game.getBoard().spreadWeapons();
        game.getBoard().spreadAmmo();

        participants.get(0).setPosition(game.getBoard().getCells().get(9));
        game.getBoard()
                .getWeaponDeck()
                .smartDraw(false)
                .ifPresent(w -> {
                    try {
                        participants.get(0).giveWeapon(w);
                    } catch (FullHandException ignored) {}
                });
        game.getBoard()
                .getWeaponDeck()
                .smartDraw(false)
                .ifPresent(w -> {
                    try {
                        participants.get(0).giveWeapon(w);
                    } catch (FullHandException ignored) {}
                });
        game.getBoard()
                .getPowerUpDeck()
                .smartDraw(false)
                .ifPresent(w -> {
                    try {
                        participants.get(0).givePowerUp(w);
                    } catch (FullHandException ignored) {}
                });
        game.getBoard()
                .getPowerUpDeck()
                .smartDraw(false)
                .ifPresent(w -> {
                    try {
                        participants.get(0).givePowerUp(w);
                    } catch (FullHandException ignored) {}
                });

        game.save();
    }

    @Test
    public void load() {
        List<Player> participants = new ArrayList<>();
        participants.add(new Player("Aldo"));
        participants.add(new Player("Giovanni"));
        participants.add(new Player("Giacomo"));
        participants.add(new Player("Roberto"));

        try {
            Game game = Game.create(false, 1, 2, participants);
            game.save();
            Game loadedGame = Game.load(participants);
        } catch (InvalidSaveStateException | UnmatchedSavedParticipantsException e) {
            fail();
        }
    }
}
