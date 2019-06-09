package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.model.powerups.PowerUpType;

public class RemotePowerUp {
    private PowerUpType type;
    private String colorCube;

    public RemotePowerUp(PowerUpType type, String colorCube) {
        this.type = type;
        this.colorCube = colorCube;
    }

    public PowerUpType getType() {
        return type;
    }

    public String getColorCube() {
        return colorCube;
    }
}
