package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.WeaponAlreadyLoadedException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.Dispatcher;

import java.util.List;

public abstract class ControlledReload {

    private static final String RELOAD_REQUEST_IF = "Would you like to reload?";
    private static final String RELOAD_REQUEST_WHICH = "Which weapon would you like to reload?";

    protected static synchronized void routine(Player subject) {

        List<Weapon> weapons = subject.getWeapons();
        boolean keepReloading = true;
        int reloadableWeapons = (int) weapons.stream().filter(Weapon::isLoaded).count();

        while(reloadableWeapons > 0 && keepReloading) {
            keepReloading = Dispatcher.requestBoolean(RELOAD_REQUEST_IF);

            if(keepReloading) {
                int weaponIndex = Dispatcher.requestInteger(RELOAD_REQUEST_WHICH, 0, weapons.size());

                try {
                    weapons.get(weaponIndex).reload();
                    subject.takeAmmoCubes(weapons.get(weaponIndex).getReloadCost());
                } catch (WeaponAlreadyLoadedException e) {

                } catch (CannotAffordException e) {

                }
            }

            reloadableWeapons = (int) weapons.stream().filter(Weapon::isLoaded).count();
        }
    }
}
