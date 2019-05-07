package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.exceptions.WeaponAlreadyLoadedException;
import it.polimi.ingsw.model.weaponry.Weapon;

public class Reload extends Activity {

    private Weapon weapon;

    public Reload() {
        this.type = ActivityType.RELOAD;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public void perform(Player player) throws WeaponAlreadyLoadedException {
        this.weapon.reload();
    }
}
