package it.polimi.ingsw.model;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;

import java.util.ArrayList;
import java.util.List;

public class Sandbox {
    public static void main(String[] args) {
        Game.offlineMode = true;
        consoleSandbox();
    }

    private static void play() {

        List<Player> participants = new ArrayList<>();
        Player aldo = new Player("Aldo");
        Player giovanni = new Player("Giovanni");
        Player giacomo = new Player("Giacomo");

        participants.add(aldo);
        participants.add(giovanni);
        participants.add(giacomo);
        Game game = Game.create(true, 1, 2, participants);
        Board board = game.getBoard();

        board.fetchWeapon("Whisper").ifPresent(w -> {
            try {
                aldo.giveWeapon(w);
            } catch (FullHandException ignored) { }
        });

        board.fetchWeapon("Machine Gun3").ifPresent(w -> {
            try {
                aldo.giveWeapon(w);
            } catch (FullHandException ignored) { }
        });

        aldo.giveAmmoCubes(new AmmoCubes(3, 0, 1));

        aldo.setPosition(board.getCells().get(8));
        giovanni.setPosition(board.getCells().get(10));

        PowerUp yellowScope = game.getBoard()
                .fetchPowerUp("scope", "yellow")
                .orElse(null);

        try {
            aldo.givePowerUp(yellowScope);
        } catch (FullHandException ignored) { }

        for(int i = 0; i < 7; i ++)
            giovanni.applyDamage(aldo);

        game.play();
    }

    private static void autoPlay() {
        Game.autoPilot = true;

        List<Player> participants = new ArrayList<>();

        participants.add(new Player("Carlo Conti"));
        participants.add(new Player("Cesare Svelto"));
        participants.add(new Player("Fox"));
        participants.add(new Player("FBF"));
        participants.add(new Player("TDM"));
        participants.add(new Player("GrEN"));

        participants.add(new Player("Ing Conti"));
        participants.add(new Player("Amadeus"));
        participants.add(new Player("Gerry Scotti"));
        participants.add(new Player("Dottor Scotti"));
        participants.add(new Player("Mentana"));
        participants.add(new Player("Sponge Bob"));

        Game game = Game.create(true, 80, 4, participants);
        game.play();
    }

    private static void printWeapons() {
        Game game = Game.create(false, 1, 1, new ArrayList<>());
        Deck<Weapon> deck = game.getBoard().getWeaponDeck();

        while(true) {
            try {
                System.out.println("\n" + deck.draw().getDescription());
            } catch (Throwable e) {
                break;
            }
        }
    }

    private static void consoleSandbox() {


    }
}
