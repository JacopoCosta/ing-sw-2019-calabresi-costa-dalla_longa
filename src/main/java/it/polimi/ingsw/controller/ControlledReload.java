package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.Dispatcher;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledReload {

    private static final String RELOAD_REQUEST_IF = "Would you like to reload?";
    private static final String RELOAD_REQUEST_WHICH = "Which weapon would you like to reload?";
    private static final String RELOAD_INSUFFICIENT_AMMO = "You don't have enough ammo to reload this weapon.";
    private static final String RELOAD_SUCCESSFUL = " was successfully reloaded.";

    protected static synchronized void routine(Player subject) {

        List<Weapon> weapons = subject.getWeapons().stream()
                .filter(w -> !w.isLoaded())
                .collect(Collectors.toList());

        boolean keepReloading = true;
        int reloadableWeapons = weapons.size();

        while(reloadableWeapons > 0 && keepReloading) {
            keepReloading = Dispatcher.requestBoolean(RELOAD_REQUEST_IF);

            if(keepReloading) {
                int weaponIndex = Dispatcher.requestIndex(RELOAD_REQUEST_WHICH, weapons);

                try {
                    Weapon weaponToReload = weapons.get(weaponIndex);
                    weaponToReload.reload();
                    subject.takeAmmoCubes(weaponToReload.getReloadCost());
                    Dispatcher.sendMessage(weaponToReload.getName() + RELOAD_SUCCESSFUL);
                }
                catch (CannotAffordException e) {
                    Dispatcher.sendMessage(RELOAD_INSUFFICIENT_AMMO);
                }
            }

            // re-evaluate situation before continuing
            weapons = subject.getWeapons().stream()
                    .filter(w -> !w.isLoaded())
                    .collect(Collectors.toList());

            reloadableWeapons = weapons.size();
        }
    }
}
