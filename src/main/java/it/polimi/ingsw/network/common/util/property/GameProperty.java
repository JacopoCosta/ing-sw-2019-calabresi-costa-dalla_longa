package it.polimi.ingsw.network.common.util.property;

public class GameProperty {
    private final boolean finalFrenzy;
    private final int roundsToPlay;
    private final int boardType;

    public GameProperty(boolean finalFrenzy, int roundsToPlay, int boardType) {
        this.finalFrenzy = finalFrenzy;
        this.roundsToPlay = roundsToPlay;
        this.boardType = boardType;
    }

    public boolean finalFrenzy(){
        return this.finalFrenzy;
    }

    public int roundsToPlay(){
        return this.roundsToPlay;
    }

    public int boardType(){
        return this.boardType;
    }
}
