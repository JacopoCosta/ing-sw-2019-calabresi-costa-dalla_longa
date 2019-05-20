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
                    int weaponIndex = Dispatcher.requestIndex(PURCHASE_REQUEST_WHICH, weapons);

                    try {
                        subject.takeAmmoCubes(weapons.get(weaponIndex).getPurchaseCost());
                        Weapon weapon = spawnCell.takeFromWeaponShop(weaponIndex);
                        try {
                            subject.giveWeapon(weapon);
                        } catch (FullHandException e) {
                            subject.getGame().getController().discardWeaponRoutine(subject);
                        }
                        subject.getGame()
                                .getBoard()
                                .getWeaponDeck()
                                .smartDraw(false)
                                .ifPresent(spawnCell::addToWeaponShop);

                    } catch (CannotAffordException e) {

                    }
                }

                purchasableWeapons = weapons.size();
            }
        }
        else {
            AmmoCell ammoCell = (AmmoCell) cell;
            AmmoTile ammoTile = ammoCell.getAmmoTile();
            if(ammoTile != null) {
                if(ammoTile.includesPowerUp()) {
                    PowerUp card = subject.getGame()
                            .getBoard()
                            .getPowerUpDeck()
                            .smartDraw(true)
                            .orElse(null); // null should never happen
                    try {
                        subject.givePowerUp(card);
                    } catch (FullHandException e) {
                        subject.getGame().getController().discardPowerUpRoutine(subject);
                    }
                }
                subject.giveAmmoCubes(ammoTile.getAmmoCubes());
                ammoCell.setAmmoTile(subject.getGame()
                        .getBoard()
                        .getAmmoTileDeck()
                        .smartDraw(true)
                        .orElse(null)); // null should never happen
            }
        }
    }
}
