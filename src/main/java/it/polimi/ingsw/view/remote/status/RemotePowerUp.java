package it.polimi.ingsw.view.remote.status;

import it.polimi.ingsw.model.powerups.PowerUpType;

public class RemotePowerUp {
    private PowerUpType type;
    private ColorCube colorCube;

    public RemotePowerUp(PowerUpType type, ColorCube colorCube) {
        this.type = type;
        this.colorCube = colorCube;
    }

    public PowerUpType getType() {
        return type;
    }

    public ColorCube getColorCube() {
        return colorCube;
    }
}
