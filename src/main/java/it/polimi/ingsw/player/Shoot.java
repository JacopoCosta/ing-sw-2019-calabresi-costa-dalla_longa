package it.polimi.ingsw.player;

import it.polimi.ingsw.weaponry.Weapon;

public class Shoot extends Activity {

    private Player author;
    private Player target;
    private Weapon weapon;

    public void setAuthor(Player author) {
        this.author = author;
    }

    public void setTarget(Player target) {
        this.target = target;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    @Override
    public void perform(Player player) {

    }
}
