package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.weaponry.effects.Damage;

/**
 * This class determines the amount of points a {@link Player} hands out to his opponents from whom they took {@link Damage}.
 */
public abstract class ScoreList {
    /**
     * The amount of points that a kill is worth such that the sum of the amount of times the {@link Player}
     * had already died in the past and the number of {@link Player}s who dealt either more {@link Damage}
     * or the same amount of {@link Damage}, the first of which being earlier, on the same kill on the same {@link Player} is equal to 0.
     */
    private static final int VALUE_0 = 8;

    /**
     * The amount of points that a kill is worth such that the sum of the amount of times the {@link Player}
     * had already died in the past and the number of {@link Player}s who dealt either more {@link Damage}
     * or the same amount of {@link Damage}, the first of which being earlier, on the same kill on the same {@link Player} is equal to 1.
     */
    private static final int VALUE_1 = 6;

    /**
     * The amount of points that a kill is worth such that the sum of the amount of times the {@link Player}
     * had already died in the past and the number of {@link Player}s who dealt either more {@link Damage}
     * or the same amount of {@link Damage}, the first of which being earlier, on the same kill on the same {@link Player} is equal to 2.
     */
    private static final int VALUE_2 = 4;

    /**
     * The amount of points that a kill is worth such that the sum of the amount of times the {@link Player}
     * had already died in the past and the number of {@link Player}s who dealt either more {@link Damage}
     * or the same amount of {@link Damage}, the first of which being earlier, on the same kill on the same {@link Player} is equal to 3.
     */
    private static final int VALUE_3 = 2;

    /**
     * The amount of points that a kill is worth such that the sum of the amount of times the {@link Player}
     * had already died in the past and the number of {@link Player}s who dealt either more {@link Damage}
     * or the same amount of {@link Damage}, the first of which being earlier, on the same kill on the
     * same {@link Player} is equal to or greater than 4.
     */
    private static final int VALUE_INFINITY = 1;

    /**
     * The amount of deaths it would take to a regular {@link Player} to score the same number of points to each opponent as the number of points
     * awarded by who died for the first time, but on final frenzy.
     */
    private static final int FRENETIC_OFFSET = 3;

    /**
     * Returns the appropriate amount of points based on the conditions described by the arguments.
     * @param index the sum of the index of the opponent in the list of opponents who damaged a {@link Player}, and the
     *              number of times said {@link Player} had died in the past.
     * @param frenetic whether or not the {@link Player} is on final frenzy. Final frenzy kills are worth as many points as a regular kill
     *                 on a {@link Player} killed three ({@link ScoreList#FRENETIC_OFFSET}) additional times, therefore many fewer points.
     * @return the amount of points to give to the opponent who damaged the considered {@link Player}.
     */
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
