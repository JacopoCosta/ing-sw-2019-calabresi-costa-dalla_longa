package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.view.virtual.Deliverable;

/**
 * This class stores simplified information about any power-up. Such info is used by clients in order to get the player know about the related power-up card.
 * The gathered info is completely received from the server via {@link Deliverable} communication with no further elaboration (every data here is for player acknowledgment only).
 * There isn't any setter method, as elaborating data and manage players choices is up to the server, that's not the purpose of this client.
 */
public class RemotePowerUp {
    /**
     * Type of the power-up card, codified as String.
     */
    private String type;
    /**
     * The equivalent ammo that can be earned by using this power-up as payment for weapons, codified as String.
     */
    private String colorCube;

    /**
     * This is the only constructor.
     * @param type the type of this power-up card, codified as String.
     * @param colorCube The ammo that can be earned by using this power-up as payment, codified as String.
     */
    public RemotePowerUp(String type, String colorCube) {
        this.type = type.toLowerCase();
        this.colorCube = colorCube;
    }

    /**
     * Getter method for {@link this.type}.
     * @return the type of the power-up card.
     */
    public String getType() {
        return type;
    }

    /**
     * Getter method for {@link this.colorCube}.
     * @return the equivalent ammo that can be earned by using this power-up as payment, codified as String.
     */
    public String getColorCube() {
        return colorCube;
    }
}
