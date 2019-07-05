package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.ammo.AmmoTile;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Deck;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.AbortedTurnException;
import it.polimi.ingsw.model.exceptions.CannotAffordException;
import it.polimi.ingsw.model.exceptions.CannotDiscardFirstCardOfDeckException;
import it.polimi.ingsw.model.exceptions.FullHandException;
import it.polimi.ingsw.model.player.Grab;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.Reload;
import it.polimi.ingsw.model.powerups.*;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.effects.Damage;
import it.polimi.ingsw.model.weaponry.effects.Effect;
import it.polimi.ingsw.model.weaponry.effects.Mark;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.view.virtual.VirtualView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class controls changes in the status of the {@link Game}. One {@code Controller} is instantiated for each {@link Game}.
 */

public class Controller {
    /**
     * A reference to the {@link VirtualView} of the same {@link Game}. This is useful for allowing message exchanges that involve
     * a multiple sequence of request/response before any effect actually takes action on the {@link Game}. It's essentially a shortcut
     * that skips the model altogether as long as it doesn't have enough information to fully describe a state transition.
     */
    private VirtualView virtualView;

    /**
     * This is the only constructor.
     *
     * @param virtualView The {@link VirtualView} that calls this constructor and passes itself as argument.
     */
    public Controller(VirtualView virtualView) {
        this.virtualView = virtualView;
    }

    /**
     * Causes a {@link Player} to spawn, after they selected the {@link PowerUp} to discard.
     *
     * @param subject          The player that is about to spawn.
     * @param powerUpToDiscard The {@link PowerUp} the player has discarded. The {@link PowerUp}'s colour is used to infer the cell where the spawn event will take place.
     */
    public void spawn(Player subject, PowerUp powerUpToDiscard) {
        subject.spawn(powerUpToDiscard.getSpawnPoint(subject.getGame().getBoard()));
        subject.discardPowerUp(powerUpToDiscard);
    }

    /**
     * Causes a {@link Player} to discard a {@link PowerUp} card.
     *
     * @param subject   The player who has discarded a {@link PowerUp}.
     * @param toDiscard The {@link PowerUp} to remove from the player's hand and place back into the {@link Deck}.
     */
    public void discardPowerUp(Player subject, PowerUp toDiscard) {
        subject.discardPowerUp(toDiscard);
    }

    /**
     * Causes a {@link Player} to discard a {@link Weapon}.
     *
     * @param subject   The player who has discarded a weapon.
     * @param toDiscard The weapon to remove from the player's hand.
     */
    public void discardWeapon(Player subject, Weapon toDiscard) {
        subject.discardWeapon(toDiscard);
    }

    /**
     * Causes a {@link Player} to change position on the game {@link Board}.
     *
     * @param subject     The player that is about to move.
     * @param destination The {@link Cell} the player
     */
    public void move(Player subject, Cell destination) {
        subject.setPosition(destination);
    }

    /**
     * Causes a {@link Player} to {@link Grab} {@link AmmoCubes} from the {@link Cell} they are currently standing on.
     *
     * @param subject The player who is about to grab ammo.
     * @return Whether or not the {@link Grab} was successful. For example, grabbing on an empty cell will be considered a failure case.
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
     * Causes a {@link Player} to purchase a {@link Weapon} from the weapon shop they are currently standing on.
     *
     * @param subject     The player who is about to grab a weapon.
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
     * Sets up an {@link AttackPattern} to be authored by a {@link Player} and ready to be used.
     *
     * @param subject The attacker.
     * @param pattern The {@link AttackPattern} that the player intends to use.
     */
    public void prepareForShoot(Player subject, AttackPattern pattern) {
        pattern.setAuthor(subject);
        pattern.resetAllModules();
    }

    /**
     * Starts an {@link AttackModule} and invokes the {@link Target} acquisition.
     *
     * @param subject  The attacker.
     * @param pattern  The {@link AttackPattern} the module belongs to.
     * @param moduleId The identifier of the {@link AttackModule} inside the {@link AttackPattern}.
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
     * After the target acquisition, it applies every {@link Effect} of the {@link AttackModule}.
     *
     * @param subject      The attacker.
     * @param attackModule The {@link AttackModule} containing the {@link Target}s and {@link Effect}s of relevance.
     * @param targets      The {@link Target}s on which the {@link Effect}s will be applied to.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     * @see it.polimi.ingsw.model.weaponry.effects.Effect
     */
    public void shootTargets(Player subject, AttackModule attackModule, List<Target> targets) throws AbortedTurnException {
        boolean invalid = targets.stream().map(t -> {
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

        if (!invalid)
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
     * Causes a {@link Player} to {@link Reload} a {@link Weapon} and pay the relevant {@link AmmoCubes} cost.
     *
     * @param subject The buyer.
     * @param weapon  The weapon about to be reloaded.
     */
    public void reload(Player subject, Weapon weapon) {
        try {
            subject.takeAmmoCubes(weapon.getReloadCost()); // this might throw an exception
            weapon.reload(); // if the player can't afford, this won't be executed
        } catch (CannotAffordException ignored) {
        }
    }

    /**
     * Causes a {@link Player} to use a {@link PowerUp} between turns (either {@link Newton} or {@link Teleport}).
     *
     * @param subject The player who is using a {@link PowerUp}.
     * @param powerUp The {@link PowerUp} being used by the player.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     */
    public void usePowerUp(Player subject, PowerUp powerUp) throws AbortedTurnException {
        if(powerUp.getType() == PowerUpType.NEWTON)
            virtualView.newton(subject);
        else if(powerUp.getType() == PowerUpType.TELEPORT)
            virtualView.teleport(subject);
    }

    /**
     * Applies the {@link Effect} of a {@link Scope} {@link PowerUp} to an active {@link Damage}, increasing its value by 1.
     *
     * @param damage        The {@link Damage} being buffed.
     * @param targets       The {@link Target}s associated with the {@link Damage} {@link Effect}, scoped or not.
     * @param scopedPlayers The {@link Target}s associated with the {@link Damage} {@link Effect} that have been chosen as victims to the {@link Scope}'s effect.
     */
    public void scope(Damage damage, List<Player> targets, List<Player> scopedPlayers) {
        damage.applyAfterScopes(targets, scopedPlayers);
    }

    /**
     * Applies the {@link Effect} of a {@link Grenade} {@link PowerUp}.
     *
     * @param subject          The player that was hit and chose to respond to the fire.
     * @param originalAttacker The original attacker, namely the target of the {@link Grenade}.
     */
    public void grenade(Player subject, Player originalAttacker) {
        if (originalAttacker == null)
            return;

        Mark mark = new Mark(1, null);
        mark.grenade(subject, originalAttacker);
    }

    /**
     * Applies the {@link Effect} of a {@link Newton} {@link PowerUp}.
     *
     * @param target      The player being moved.
     * @param destination The cell the player is being moved to.
     */
    public void newton(Player target, Cell destination) {
        target.setPosition(destination);
    }

    /**
     * Applies the {@link Effect} of a {@link Teleport} {@link PowerUp}.
     *
     * @param subject     The player using the {@link PowerUp}, i.e. the player being moved.
     * @param destination The player's destination {@link Cell}.
     */
    public void teleport(Player subject, Cell destination) {
        subject.setPosition(destination);
    }
}
