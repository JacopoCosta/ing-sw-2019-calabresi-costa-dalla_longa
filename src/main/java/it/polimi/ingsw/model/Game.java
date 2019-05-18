package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.controller.VirtualDispatcher;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.player.*;
import it.polimi.ingsw.view.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private static final String RESPAWN_REQUEST = "Where would you like to respawn?";

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

    public List<Player> getPlayersByCell(Cell cell) {
        List<Player> result = new ArrayList<>();
        for(Player p : this.participants) {
            if(p.getPosition() == cell)
                result.add(p);
        }
        return result;
    }

    public List<Player> getPlayersByRoom(Room room) {
        List<Player> result = new ArrayList<>();
        for(Player p : this.participants) {
            if(p.getPosition().getRoom() == room)
                result.add(p);
        }
        return result;
    }

    public void setup() {
        board.spreadAmmo();
        board.spreadWeapons();

        System.out.println(board.toString());
    }

    public void playTurn() {
        Player subject = participants.get(currentTurnPlayer);

        if(subject.getPosition() == null) {
            List<Cell> spawnPoints = board.getCells()
                    .stream()
                    .filter(Cell::isSpawnPoint)
                    .collect(Collectors.toList());

            List<String> spawnPointNames = spawnPoints.stream()
                    .map(Cell::getId)
                    .map(i -> "#" + i)
                    .collect(Collectors.toList());

            int spawnPointIndex = Dispatcher.requestIndex(RESPAWN_REQUEST, spawnPointNames);
            subject.setPosition(spawnPoints.get(spawnPointIndex));
        }

        subject.beginTurn();
        subject.savePosition();
        subject.resetRecentlyDamaged();
        while(subject.getRemainingExecutions() > 0) { // a turn is made by several executions
            List<Execution> options = Execution.getOptionsForPlayer(subject);
            Execution choice = controller.requestExecution(subject, options);

            for(Activity activity : choice.getActivities()) // each execution consists of some activities
                this.controller.activityRoutine(subject, activity);
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
}
