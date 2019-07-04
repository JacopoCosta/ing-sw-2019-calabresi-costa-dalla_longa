package it.polimi.ingsw.network.common.deliverable;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.server.Server;
import it.polimi.ingsw.util.ColoredString;

/**
 * {@code Bulk}-type {@link Deliverable}s are used to convey generic objects, most often compound lists,
 * from the {@link Server} to the {@link Client}. Since these are primarily used to transport data,
 * unbeknownst to the player, no {@link Response} is required (or indeed expected) by the server, after sending a {@code Bulk}.
 */
//  ^ Footnote: IntelliJ's scrubby vocabulary thinks that "unbeknownst" is a typo, but it's not. It's a valid word.
public class Assets extends Deliverable {

    private static final String tracksHeader = "Kills:" + " ".repeat(21) + "Double Kills";

    private ColoredString[][] tracks;

    private ColoredString[][] board;

    private static final String opponentToasterHeader = "Your opponents:";

    private ColoredString[][] opponentToasters;

    private static final String ownToasterHeader = "Your situation";

    private ColoredString[][] ownToaster;

    public Assets(DeliverableEvent event, ColoredString[][] tracks, ColoredString[][] board, ColoredString[][] opponentToasters, ColoredString[][] ownToaster) {
        super(event);
        this.type = DeliverableType.ASSETS;
        this.tracks = tracks;
        this.board = board;
        this.opponentToasters = opponentToasters;
        this.ownToaster = ownToaster;
    }

    public static String getTracksHeader() {
        return tracksHeader;
    }

    public ColoredString[][] getTracks() {
        return tracks;
    }

    public ColoredString[][] getBoard() {
        return board;
    }

    public static String getOpponentToasterHeader() {
        return opponentToasterHeader;
    }

    public ColoredString[][] getOpponentToasters() {
        return opponentToasters;
    }

    public static String getOwnToasterHeader() {
        return ownToasterHeader;
    }

    public ColoredString[][] getOwnToaster() {
        return ownToaster;
    }
}
