package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.VirtualDispatcher;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Game {
    private static final String RESPAWN_REQUEST = "Which power-up would you like to keep? You will respawn where indicated by the one you discard.";

    private boolean finalFrenzy;
    private int roundsLeft;
    private boolean gameOver;

    private List<Player> participants;
    private int currentTurnPlayer;

    private Board board;
    private Controller controller;
    private VirtualDispatcher virtualDispatcher;

    public Board getBoard() {
        return this.board;
    }

    public Controller getController() {
        return controller;
    }

    public VirtualDispatcher getDispatcher() {
        return virtualDispatcher;
    }

    private Game(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        this.finalFrenzy = finalFrenzy;
        this.roundsLeft = roundsToPlay;
        this.gameOver = false;
        this.participants = participants;
        this.currentTurnPlayer = 0;

        this.board = Board.generate(this, boardType);
        this.controller = new Controller(this);
        this.virtualDispatcher = new VirtualDispatcher(this);
    }

    public static Game create(boolean finalFrenzy, int roundsToPlay, int boardType, List<Player> participants) {
        Game game = new Game(finalFrenzy, roundsToPlay, boardType, participants);
        participants.forEach(p -> p.setGame(game));
        return game;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public void setup() {
        board.spreadAmmo();
        board.spreadWeapons();

        // <temp>
        participants.get(0).setPosition(board.getCells().get(5));

        for(int i = 0; i < 10; i ++) {
            try {
                participants.get(0).giveWeapon(board.getWeaponDeck().smartDraw(false).orElse(null));
            } catch (FullHandException ignored) { }
        }
        participants.get(1).setPosition(board.getCells().get(6));
        // </temp>
    }

    public void playTurn() {
        Player subject = participants.get(currentTurnPlayer);

        if(subject.getPosition() == null) {
            printGameStatus();

            List<PowerUp> powerUps = new ArrayList<>();

            for(int i = 0; i <= 1; i ++)
                powerUps.add(board.getPowerUpDeck().smartDraw(true).orElse(null));

            int keepIndex = Dispatcher.requestIndex(RESPAWN_REQUEST, powerUps);

            PowerUp powerUpToKeep = powerUps.get(keepIndex);
            PowerUp powerUpToRespawn = powerUps.get(1 - keepIndex);

            subject.spawn(powerUpToRespawn.getSpawnPoint(board));
            try {
                subject.givePowerUp(powerUpToKeep);
            } catch (FullHandException e) {
                controller.discardPowerUpRoutine(subject);
            }
        }

        subject.beginTurn();
        subject.savePosition();
        subject.resetRecentlyDamaged();
        while(subject.getRemainingExecutions() > 0) { // a turn is made by several executions
            printGameStatus();
            List<Execution> options = Execution.getOptionsForPlayer(subject);
            Execution choice = controller.requestExecution(subject, options);

            for(Activity activity : choice.getActivities()) // each execution consists of some activities
                this.controller.activityRoutine(subject, activity);

            subject.endExecution();
        }

        participants.stream() // score all dead players
                .filter(Player::isKilled)
                .forEach(Player::scoreUponDeath);

        this.currentTurnPlayer = (this.currentTurnPlayer + 1) % this.participants.size();
    }

    public void play() {
        setup();
        while(!gameOver) {
            this.playTurn();
        }
    }

    public void printGameStatus() {
        Dispatcher.sendMessage(this.toString());
    }

    @Override
    public String toString() {
        String s = "Board:\n";
        s += board.toString() + "\n";

        s += "Weapons : " + board.getWeaponDeck().toString() + "\n";
        s += "Power-Ups : " + board.getPowerUpDeck().toString() + "\n";
        s += "Ammo Tiles : " + board.getAmmoTileDeck().toString() + "\n";

        s += "Players:\n";
        s += Table.create(
                participants.stream().map(p -> "#" + p.getID()).collect(Collectors.toList()),
                participants.stream().map(Player::getName).collect(Collectors.toList()),
                participants.stream().map(Player::getScore).collect(Collectors.toList()),
                participants.stream().map(Player::getPosition).map(c -> "@" +  (c == null ? "null" : c.getId())).collect(Collectors.toList()),
                participants.stream().map(p -> "Damage[" +
                        Table.list(p.getDamagersList().stream().map(Player::getID).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "Marks[" +
                        Table.list(p.getDamagersList().stream().map(Player::getID).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "| Weapons[" +
                        Table.list(p.getWeapons().stream().map(Weapon::getName).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "Power-ups[" +
                        Table.list(p.getPowerUps().stream().map(PowerUp::toString).collect(Collectors.toList()))
                        + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "Ammo[" + p.getAmmoCubes() + "]").collect(Collectors.toList()),
                participants.stream().map(p -> "| died " + p.getDeathCount() + " times").collect(Collectors.toList()),
                participants.stream().map(p -> p.isOnFrenzy() ? "(frenzy activated)" : "").collect(Collectors.toList())
        ) + "\n";

        s += "There are " + roundsLeft + " skulls on the killshot track.\n";
        s += "Currently playing: " + participants.get(currentTurnPlayer).getName() + " with " + participants.get(currentTurnPlayer).getRemainingExecutions() + " actions left.";

        return s;
    }
}
