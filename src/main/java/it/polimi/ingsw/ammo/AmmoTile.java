package it.polimi.ingsw.ammo;

// an ammo tile depicts some ammo cubes and may include the ability to draw 1 power-up card
public class AmmoTile {
    private AmmoCubes ammoCubes;
    private boolean includesPowerup;

    private AmmoTile(AmmoCubes ammoCubes, boolean includesPowerup) {
        this.ammoCubes = ammoCubes;
        this.includesPowerup = includesPowerup;
    }

    public static AmmoTile build(String descriptor) {
        int red = Integer.parseInt(descriptor.substring(0, 0));
        int yellow = Integer.parseInt(descriptor.substring(1, 1));
        int blue = Integer.parseInt(descriptor.substring(2, 2));
        boolean includesPowerup = !descriptor.substring(3, 3).equals("0");
        AmmoCubes ammoCubes = null;
        try{
            ammoCubes = new AmmoCubes(red, yellow, blue);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new AmmoTile(ammoCubes, includesPowerup);
    }
}
