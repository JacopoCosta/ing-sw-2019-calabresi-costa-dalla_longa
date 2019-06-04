package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.AbortedTurnException;
import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.CannotDiscardFirstCardOfDeckException;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class controls changes in the status of the game. One controller is instantiated for each game.
 */

public class Controller {
    /**
     * A reference to the virtual view of the same game. This is useful for allowing message exchanges that involve
     * a multiple sequence of request/response before any effect actually takes action on the game. It's essentially a shortcut
     * that skips the model altogether as long as it doesn't have enough information to fully describe a state transition.
     */
    private VirtualView virtualView;

    /**
     * This is the only constructor.
     * @param virtualView The virtual view that calls this constructor and passes itself as argument.
     */
    public Controller(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    /**
     * Causes a player to spawn, after they selected the power up to discard.
     * @param subject The player that is about to spawn.
     * @param powerUpToDiscard The power up the player has discarded. The power up's colour is used to infer the cell where the spawn event will take place.
     */
    public void spawn(Player subject, PowerUp powerUpToDiscard) {
        subject.spawn(powerUpToDiscard.getSpawnPoint(subject.getGame().getBoard()));
        subject.discardPowerUp(powerUpToDiscard);
    }

    /**
     * Causes a player to discard a power up card.
     * @param subject The player who has discarded a power up.
     * @param toDiscard The power up to remove from the player's hand and place back into the deck.
     */
    public void discardPowerUp(Player subject, PowerUp toDiscard) {
        subject.discardPowerUp(toDiscard);
    }

    /**
     * Causes a player to discard a weapon.
     * @param subject The player who has discarded a weapon.
     * @param toDiscard The weapon to remove from the player's hand.
     */
    public void discardWeapon(Player subject, Weapon toDiscard) {
        subject.discardWeapon(toDiscard);
    }

    /**
     * Causes a player to change position on the game board.
     * @param subject The player that is about to move.
     * @param destination The cell the player
     */
    public void move(Player subject, Cell destination) {
        subject.setPosition(destination);
    }

    /**
     * Causes a player to grab ammo from the cell they are currently standing on.
     * @param subject The player who is about to grab ammo.
     * @return Whether or not the grab was successful. For example, grabbing on an empty cell will be considered a failure case.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     */
    public boolean grabAmmo(Player subject) throws AbortedTurnException {
        AmmoCell ammoCell = (AmmoCell) subject.getPosition();
        AmmoTile ammoTile = ammoCell.getAmmoTile();
        if(ammoTile == null)
            return false;

        subject.giveAmmoCubes(ammoTile.getAmmoCubes());
        if(ammoTile.includesPowerUp()) {
            Optional<PowerUp> powerUpOptional = subject.getGame()
                    .getBoard()
                    .getPowerUpDeck()
                    .smartDraw(true);

            if(powerUpOptional.isPresent()) {
                PowerUp powerUp = powerUpOptional.get();
                try {
                    subject.givePowerUp(powerUp);
                } catch (FullHandException e) {
                    virtualView.discardPowerUp(subject);
                }
            }
        }
        ammoCell.setAmmoTile(null);
        try {
            subject.getGame().getBoard().getAmmoTileDeck().discard(ammoTile);
        } catch (CannotDiscardFirstCardOfDeckException ignored) { }

        return true;
    }

    /**
     * Causes a player to purchase a weapon from the weapon shop they are currently standing on.
     * @param subject The player who is about to grab a weapon.
     * @param weaponIndex The index of the weapon inside the weapon shop.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     * @see SpawnCell#getWeaponShop()
     */
    public void grabWeapon(Player subject, int weaponIndex) throws AbortedTurnException {
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

    /**
     * Sets up an attack pattern to be authored by a player and ready to be used.
     * @param subject The attacker.
     * @param pattern The attack pattern that the player intends to use.
     */
    public void prepareForShoot(Player subject, AttackPattern pattern) {
        pattern.setAuthor(subject);
        pattern.resetAllModules();
    }

    /**
     * Starts an attack module and invokes the target acquisition.
     * @param subject The attacker.
     * @param pattern The attack pattern the module belongs to.
     * @param moduleId The identifier of the attack module inside the attack pattern.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     */
    public void shoot(Player subject, AttackPattern pattern, int moduleId) throws AbortedTurnException {
        if(moduleId == -1) // -1 ends action
            return;
        AttackModule attackModule = pattern.getModule(moduleId);
        try {
            subject.takeAmmoCubes(attackModule.getSummonCost());
        } catch (CannotAffordException ignored) { } // this should never happen

        List<Target> targets = attackModule.getTargets();
        virtualView.acquireTargets(subject, attackModule, targets);
    }

    /**
     * After the target acquisition, it applies every effect of the attack module.
     * @param subject The attacker.
     * @param attackModule The attack module containing the targets and effects of relevance.
     * @param targets The targets on which the effects will be applied to.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     * @see it.polimi.ingsw.model.weaponry.effects.Effect
     */
    public void shootTargets(Player subject, AttackModule attackModule, List<Target> targets) throws AbortedTurnException {
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

        AttackPattern pattern = attackModule.getContext();

        List<Integer> next = attackModule.getNext()
                .stream()
                .filter(i -> {
                    if(i == -1) // -1 needs to pass
                        return true;
                    return !pattern.getModule(i).isUsed() && subject.canAfford(pattern.getModule(i).getSummonCost());
                }).collect(Collectors.toList());

        virtualView.shootAttackModule(subject, pattern, next);
    }

    /**
     * Causes a player to reload a weapon and pay the relevant cost.
     * @param subject The buyer.
     * @param weapon The weapon about to be reloaded.
     */
    public void reload(Player subject, Weapon weapon) {
        try {
            subject.takeAmmoCubes(weapon.getReloadCost()); // this might throw an exception
            weapon.reload(); // if the player can't afford, this won't be executed
        }
        catch (CannotAffordException ignored) { }
    }

    /**
     * Causes a player to use a power up between turns (either {@code Newton} or {@code Teleport}).
     * @param subject The player who is using a power up.
     * @param powerUp The power up being used by the player.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     */
    public void usePowerUp(Player subject, PowerUp powerUp) throws AbortedTurnException {
        if(powerUp.getType() == PowerUpType.NEWTON)
            virtualView.newton(subject);
        else if(powerUp.getType() == PowerUpType.TELEPORT)
            virtualView.teleport(subject);
    }

    /**
     * Applies the effect of a scope power up to an active damage effect, increasing its value by 1.
     * @param damage The damage effect being buffed.
     * @param targets The targets associated with the damage effect, scoped or not.
     * @param scopedPlayers The targets associated with the damage effect that have been chosen as victims to the scope's effect.
     */
    public void scope(Damage damage, List<Player> targets, List<Player> scopedPlayers) {
        damage.applyAfterScopes(targets, scopedPlayers);
    }

    /**
     * Applies the effect of a grenade power up.
     * @param subject The player that was hit and chose to respond to the fire.
     * @param originalAttacker The original attacker, namely the target of the grenade.
     */
    public void grenade(Player subject, Player originalAttacker) {
        if(originalAttacker == null)
            return;

        Mark mark = new Mark(1, null);
        mark.grenade(subject, originalAttacker);
    }

    /**
     * Applies the effect of a newton power up.
     * @param target The player being moved.
     * @param destination The cell the player is being moved to.
     */
    public void newton(Player target, Cell destination) {
        target.setPosition(destination);
    }

    /**
     * Applies the effect of a teleport power up.
     * @param subject The player using the power up, i.e. the player being moved.
     * @param destination The player's destination cell.
     */
    public void teleport(Player subject, Cell destination) {
        subject.setPosition(destination);
    }
}
