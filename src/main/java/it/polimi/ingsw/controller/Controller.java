package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.powerups.PowerUpType;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.effects.Damage;
import it.polimi.ingsw.model.weaponry.effects.Mark;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.List;

public class Controller {

    private VirtualView virtualView;

    public Controller(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    public void spawn(Player subject, PowerUp powerUpToKeep, PowerUp powerUpToRespawn) {
        subject.spawn(powerUpToRespawn.getSpawnPoint(subject.getGame().getBoard()));
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
        ammoCell.setAmmoTile(null);

        return true;
    }

    public void grabWeapon(Player subject, int weaponIndex) {
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
        } catch (CannotAffordException ignored) { } // this should never happen
    }

    public void prepareForShoot(Player subject, AttackPattern pattern) {
        pattern.setAuthor(subject);
        pattern.resetAllModules();
    }

    public void shoot(Player subject, AttackPattern pattern, int moduleId) {
        if(moduleId == -1) // -1 ends action
            return;
        AttackModule attackModule = pattern.getModule(moduleId);
        try {
            subject.takeAmmoCubes(attackModule.getSummonCost());
        } catch (CannotAffordException ignored) { } // this should never happen

        List<Target> targets = attackModule.getTargets();
        virtualView.acquireTargets(subject, attackModule, targets);
    }

    public void shootTargets(Player subject, AttackModule attackModule, List<Target> targets) {
        boolean invalid = targets.stream().map( t -> {
            switch (t.getType()) {
                case PLAYER:
                    return t.getPlayer() == null;
                case CELL:
                    return t.getCell() == null;
                case ROOM:
                    return t.getRoom() == null;
                default:
                    return true;
                }
            })
            .reduce(false, (a, b) -> a || b);

        if(!invalid)
            attackModule.getEffects().forEach(e -> {
                    e.setAuthor(subject);
                    e.apply();
            });

        attackModule.setUsed(true);
        virtualView.shootAttackModule(subject, attackModule.getContext(), attackModule.getNext());
    }

    public void reload(Player subject, Weapon weapon) {
        try {
            subject.takeAmmoCubes(weapon.getReloadCost()); // this might throw an exception
            weapon.reload(); // if the player can't afford, this won't be executed
        }
        catch (CannotAffordException ignored) { }
    }

    public void usePowerUp(Player subject, PowerUp powerUp) {
        if(powerUp.getType() == PowerUpType.NEWTON)
            virtualView.newton(subject);
        else if(powerUp.getType() == PowerUpType.TELEPORT)
            virtualView.teleport(subject);
    }

    public void scope(Damage damage, List<Player> targets, List<Player> scopedPlayers) {
        damage.applyAfterScopes(targets, scopedPlayers);
    }

    public void grenade(Player subject, Player originalAttacker) {
        if(originalAttacker == null)
            return;

        Mark mark = new Mark(1, null);
        mark.grenade(subject, originalAttacker);
    }

    public void newton(Player target, Cell destination) {
        target.setPosition(destination);
    }

    public void teleport(Player subject, Cell destination) {
        subject.setPosition(destination);
    }
}
