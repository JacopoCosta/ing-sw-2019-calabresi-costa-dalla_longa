package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.InvalidPowerUpTypeException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.utilities.DecoratedJSONObject;

public abstract class PowerUp {
    protected AmmoCubes ammoCubes;
    protected PowerUpType type;

    public PowerUp(AmmoCubes ammoCubes) {
        this.ammoCubes = ammoCubes;
    }

    public static PowerUp build(DecoratedJSONObject jPowerUp) throws InvalidPowerUpTypeException {
        String type = jPowerUp.getString("type");
        AmmoCubes ammoCubes = AmmoCubes.build(jPowerUp.getObject("ammoCubes"));

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
