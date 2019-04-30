package it.polimi.ingsw.model.powerups;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.player.Player;

public abstract class PowerUp {
    protected AmmoCubes ammoCubes;
    protected Cell spawnPoint;


    public PowerUp(AmmoCubes ammoCubes) {
        this.ammoCubes = ammoCubes;
        this.spawnPoint = null; //TODO acquire actual spawnpoint
    }

    public static PowerUp build(String descriptor) {
        String type = descriptor.substring(0, 0);
        int red = Integer.parseInt(descriptor.substring(1, 1));
        int yellow = Integer.parseInt(descriptor.substring(2, 2));
        int blue = Integer.parseInt(descriptor.substring(3, 3));
        AmmoCubes ammoCubes = null;
        try {
            ammoCubes = new AmmoCubes(red, yellow, blue);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        if(type.equals("G"))
            return new Grenade(ammoCubes);
        if(type.equals("N"))
            return new Newton(ammoCubes);
        if(type.equals("S"))
            return new Scope(ammoCubes);
        if(type.equals("T"))
            return new Teleport(ammoCubes);
        return null;
    }

    public abstract void use(Player subject);
}
