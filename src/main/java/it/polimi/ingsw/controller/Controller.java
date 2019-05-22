package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.effects.EffectType;
import it.polimi.ingsw.model.weaponry.effects.OffensiveEffect;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;
import it.polimi.ingsw.model.weaponry.targets.TargetRoom;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.List;

public class Controller {

    private VirtualView virtualView;

    //TODO constructor


    public void spawn(Player subject, PowerUp powerUpToKeep, PowerUp powerUpToRespawn) {
        subject.spawn(powerUpToRespawn.getSpawnPoint(subject.getPosition().getBoard()));
        try {
            subject.givePowerUp(powerUpToKeep);
        } catch (FullHandException e) {
            virtualView.discardPowerUp(subject);
        }
    }

    public void discardPowerUp(Player subject, PowerUp toDiscard) {
        subject.discardPowerUp(toDiscard);
    }

    public void discardWeapon(Player subject, Weapon toDiscard) {
        subject.discardWeapon(toDiscard);
    }

    public void move(Player subject, Cell destination) {
        subject.setPosition(destination);
    }

    public boolean grabAmmo(Player subject) {
        AmmoCell ammoCell = (AmmoCell) subject.getPosition();
        AmmoTile ammoTile = ammoCell.getAmmoTile();
        if(ammoTile == null)
            return false;

        subject.giveAmmoCubes(ammoTile.getAmmoCubes());
        if(ammoTile.includesPowerUp()) {
            PowerUp card = subject.getGame()
                    .getBoard()
                    .getPowerUpDeck()
                    .smartDraw(true)
                    .orElse(null); // null should never happen
            if(card != null) {
                try {
                    subject.givePowerUp(card);
                } catch (FullHandException e) {
                    virtualView.discardPowerUp(subject);
                }
            }
        }
        return true;
    }

    public boolean grabWeapon(Player subject, int weaponIndex) {
        SpawnCell spawnCell = (SpawnCell) subject.getPosition();
        List<Weapon> weapons = spawnCell.getWeaponShop();

        try {
            subject.takeAmmoCubes(weapons.get(weaponIndex).getPurchaseCost()); // this might throw an exception
            Weapon weapon = spawnCell.takeFromWeaponShop(weaponIndex); // if the player can't afford, this won't be executed
            try {
                subject.giveWeapon(weapon);
            } catch (FullHandException e) {
                virtualView.discardWeapon(subject);
            }
            subject.getGame()
                    .getBoard()
                    .getWeaponDeck()
                    .smartDraw(false)
                    .ifPresent(spawnCell::addToWeaponShop); // refill the shop if there are available cards

        } catch (CannotAffordException e) {
            return false;
        }
        return true;
    }

    public void shoot(Player subject, AttackPattern pattern) {
        pattern.setAuthor(subject);
        pattern.resetAllModules();

        int moduleId = virtualView.shootAttackModule(pattern);
        while(moduleId != -1) {
            AttackModule attackModule = pattern.getModule(moduleId);
            List<Target> targets = attackModule.getTargets();
            boolean invalid = false;

            for(Target target : targets) {
                switch(target.getType()) {
                    case PLAYER:
                        Player acquiredPlayer = virtualView.shootPlayer((TargetPlayer) target);
                        if(acquiredPlayer == null)
                            invalid = true;
                        else
                            ((TargetPlayer) target).setPlayer(acquiredPlayer);
                        break;

                    case CELL:
                        Cell acquiredCell = virtualView.shootCell((TargetCell) target);
                        if(acquiredCell == null)
                            invalid = true;
                        else
                        ((TargetCell) target).setCell(acquiredCell);
                        break;

                    case ROOM:
                        Room acquiredRoom = virtualView.shootRoom((TargetRoom) target);
                        if(acquiredRoom == null)
                            invalid = true;
                        else
                        ((TargetRoom) target).setRoom(acquiredRoom);
                        break;

                    default:
                        break;
                }
            }

            if(!invalid)
                attackModule.getEffects().forEach(e -> {
                    if (e.getType() == EffectType.MOVE)
                        e.apply();
                    else {
                        OffensiveEffect oe = (OffensiveEffect) e;
                        oe.setAuthor(subject);
                        oe.apply();
                    }
                });

            attackModule.setUsed(true);
            moduleId = virtualView.shootAttackModule(pattern);
        }
    }

    public boolean reload(Player subject, Weapon weapon) {
        try {
            subject.takeAmmoCubes(weapon.getReloadCost()); // this might throw an exception
            weapon.reload();; // if the player can't afford, this won't be executed
        }
        catch (CannotAffordException e) {
            return false;
        }
        return true;
    }
}
