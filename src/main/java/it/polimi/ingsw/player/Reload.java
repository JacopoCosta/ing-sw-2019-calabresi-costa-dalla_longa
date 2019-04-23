package it.polimi.ingsw.player;

import it.polimi.ingsw.weaponry.Weapon;

public class Reload extends Activity {

    private Weapon weapon;

    public Reload() {
        this.type = ActivityType.RELOAD;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public void perform(Player player) {
        this.weapon.reload();
    }
}
