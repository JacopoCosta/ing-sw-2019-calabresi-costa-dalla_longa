package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.Dispatcher;

import java.util.List;

public abstract class ControlledGrab {
    private static final String PURCHASE_REQUEST_IF = "Would you like to buy a weapon?";
    private static final String PURCHASE_REQUEST_WHICH = "Which weapon would you like to purchase?";
    private static final String DISCARD_WEAPON_REQUEST_WHICH = "Which weapon would you like to discard?";
    private static final String DISCARD_POWERUP_REQUEST_WHICH = "Which power-up would you like to discard?";

    protected static synchronized void routine(Player subject) {
        Cell cell = subject.getPosition();
        if(cell.isSpawnPoint()) {
            SpawnCell spawnCell = (SpawnCell) cell;
            List<Weapon> weapons = spawnCell.getWeaponShop();
            boolean keepPurchasing = true;

            int purchasableWeapons = weapons.size();

            while(purchasableWeapons > 0 && keepPurchasing) {
                keepPurchasing = Dispatcher.requestBoolean(PURCHASE_REQUEST_IF);

                if(keepPurchasing) {
                    int weaponIndex = Dispatcher.requestInteger(PURCHASE_REQUEST_WHICH, 0, weapons.size());

                    try {
                        subject.takeAmmoCubes(weapons.get(weaponIndex).getPurchaseCost());
                        Weapon weapon = spawnCell.takeFromWeaponShop(weaponIndex);
                        try {
                            subject.giveWeapon(weapon);
                        } catch (FullHandException e) {
                            List<Weapon> discardable = subject.getWeapons();
                            int discardIndex = Dispatcher.requestInteger(DISCARD_WEAPON_REQUEST_WHICH, 0, discardable.size());
                            subject.discardWeapon(discardable.get(discardIndex));
                        }
                        try {
                            spawnCell.addToWeaponShop(subject.getGame().getBoard().getWeaponDeck().draw());
                        } catch (EmptyDeckException ignored) {} // ignored because no action is required when the weapon deck is depleted

                    } catch (CannotAffordException e) {

                    }
                }

                purchasableWeapons = weapons.size();
            }
        }
        else {
            AmmoCell ammoCell = (AmmoCell) cell;
            AmmoTile ammoTile = ammoCell.getAmmoTile();
            if(ammoTile != null && ammoTile.includesPowerUp()) {
                Deck<PowerUp> deck = subject.getGame().getBoard().getPowerUpDeck();
                PowerUp card = null;
                try {
                    card = deck.draw();
                } catch (EmptyDeckException e) {
                    deck.regenerate();
                    deck.shuffle();
                }
                try {
                    subject.givePowerUp(card);
                } catch (FullHandException e) {
                    List<PowerUp> discardable = subject.getPowerUps();
                    int discardIndex = Dispatcher.requestInteger(DISCARD_POWERUP_REQUEST_WHICH, 0, discardable.size());
                    subject.discardPowerUp(discardable.get(discardIndex));
                }
            }
        }
    }
}
