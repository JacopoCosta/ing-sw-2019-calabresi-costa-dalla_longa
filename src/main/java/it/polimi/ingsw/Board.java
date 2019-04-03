package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private boolean finalFrenzy;
    private int roundsLeft;

    private List<Player> players;
    private int currentTurnPlayer;

    private List<Player> killers;
    private List<Player> doubleKillers;
    private List<Cell> cells;

    private Deck<Weapon> weaponDeck;
    private Deck<AmmoTile> ammoTileDeck;
    private Deck<PowerUp> powerUpDeck;

    private Board() {}

    public static Board generate(int type, boolean finalFrenzy, int roundsLeft, List<Player> players) {
        Board board = new Board();

        // apply parameters
        board.finalFrenzy = finalFrenzy;
        board.roundsLeft = roundsLeft;
        board.players = new ArrayList<>(players);

        // initialize starting values
        board.currentTurnPlayer = 0;
        board.killers = new ArrayList<>();
        board.doubleKillers = new ArrayList<>();

        // generate decks


        // type is used to choose one hard-coded cell configuration
        Board.configureCells(type);

        return board;
    }

    public static List<Cell> configureCells(int boardType) {
        List<Cell> cells = new ArrayList<>();
        switch(boardType) {
            default:
                break;
        }
        return cells;
    }
}
