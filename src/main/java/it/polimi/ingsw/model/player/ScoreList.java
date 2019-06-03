package it.polimi.ingsw.model.player;

public abstract class ScoreList {
    private static final int VALUE_0 = 8;
    private static final int VALUE_1 = 6;
    private static final int VALUE_2 = 4;
    private static final int VALUE_3 = 2;
    private static final int VALUE_INFINITY = 1;
    private static final int FRENETIC_OFFSET = 3;

    public static int get(int index, boolean frenetic) {
        if(frenetic)
            index += FRENETIC_OFFSET;
        switch (index) {
            case 0:
                return VALUE_0;
            case 1:
                return VALUE_1;
            case 2:
                return VALUE_2;
            case 3:
                return VALUE_3;
            default:
                return VALUE_INFINITY;
        }
    }
}
