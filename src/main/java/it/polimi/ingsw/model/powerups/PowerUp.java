package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.InvalidPowerUpTypeException;
import it.polimi.ingsw.model.exceptions.JsonException;
import it.polimi.ingsw.model.exceptions.JullPointerException;
import it.polimi.ingsw.model.util.json.DecoratedJsonObject;

/**
 * Power-ups are action cards that each player can use in their turn, before each execution, during an attack,
 * or during someone else's attack when they're being damaged.
 */
public abstract class PowerUp {
    /**
     * The type of the power up.
     */
    protected PowerUpType type;

    /**
     * A set of ammo cubes containing only one cube equivalent to the spawn point the power up is linked to.
     * @see PowerUp#getSpawnPoint(Board)
     */
    protected AmmoCubes ammoCubes;

    /**
     * This is the only constructor.
     * @param ammoCubes a set of ammo cubes containing only one cube of the colour of the power up.
     */
    public PowerUp(AmmoCubes ammoCubes) {
        this.ammoCubes = ammoCubes;
    }

    /**
     * This factory method constructs a power up card, with the properties found inside the JSON object passed as argument.
     * @param jPowerUp the JSON object containing the desired properties.
     * @return an instance of this class in accordance with the specified properties.
     */
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

    /**
     * Returns the type of the power up.
     * @return the type of the power up.
     */
    public PowerUpType getType() {
        return type;
    }

    /**
     * Returns a set of ammo cubes containing one cube of the colour of the power up.
     * @return a set of ammo cubes containing one cube of the colour of the power up.
     */
    public AmmoCubes getAmmoCubes() {
        return ammoCubes;
    }

    /**
     * Looks for the spawn point whose colour is the same as the power up's.
     * @param board the board in which to search for the spawn point.
     * @return the spawn cell.
     */
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
