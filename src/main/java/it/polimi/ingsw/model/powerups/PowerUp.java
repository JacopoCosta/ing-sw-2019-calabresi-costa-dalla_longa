package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.InvalidPowerUpTypeException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.utilities.DecoratedJsonObject;

public abstract class PowerUp {
    protected PowerUpType type;
    protected AmmoCubes ammoCubes;

    public PowerUp(AmmoCubes ammoCubes) {
        this.ammoCubes = ammoCubes;
    }

    public static PowerUp build(DecoratedJsonObject jPowerUp) throws InvalidPowerUpTypeException {
        String type;
        try {
            type = jPowerUp.getString("type");
        } catch (JullPointerException e) {
            throw new JsonException("PowerUp type not found.");
        }
        AmmoCubes ammoCubes;
        try {
            ammoCubes = AmmoCubes.build(jPowerUp.getObject("ammoCubes"));
        } catch (JullPointerException e) {
            throw new JsonException("PowerUp ammoCubes not found.");
        }

        if(type.equals("grenade"))
            return new Grenade(ammoCubes);
        if(type.equals("newton"))
            return new Newton(ammoCubes);
        if(type.equals("scope"))
            return new Scope(ammoCubes);
        if(type.equals("teleport"))
            return new Teleport(ammoCubes);
        throw new InvalidPowerUpTypeException(type + " is not a valid name for a PowerUp type. Use \"grenade\", \"newton\", \"scope\", or \"teleport\"");
    }

    public PowerUpType getType() {
        return type;
    }

    public AmmoCubes getAmmoCubes() {
        return ammoCubes;
    }

    public Cell getSpawnPoint(Board board) {
        return board.getCells()
                .stream()
                .filter(Cell::isSpawnPoint)
                .filter(c -> ((SpawnCell) c).getAmmoCubeColor().equals(ammoCubes))
                .findFirst()
                .orElse(null); // this should never happen
    }

    @Override
    public String toString() {
        return ammoCubes.toStringAsColor() + " " + this.type.toString();
    }
}
