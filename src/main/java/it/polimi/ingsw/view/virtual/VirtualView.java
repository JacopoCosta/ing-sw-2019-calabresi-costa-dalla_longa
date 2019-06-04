package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Execution;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.Newton;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.powerups.PowerUpType;
import it.polimi.ingsw.model.utilities.Table;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.effects.Damage;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;
import it.polimi.ingsw.model.weaponry.targets.TargetRoom;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.Game.godMode;

/**
 * This class is responsible of bridging the network and model packages converting game-related request into deliverables.
 * On this end, deliverables are sent to (and received from) the virtual client, whose {@code .deliver()} method is invoked.
 */
public class VirtualView {
    /**
     * The game that instantiated the virtual view.
     */
    private Game game;

    /**
     * The controller that the virtual view will forward its responses to, triggering changes in the game status.
     */
    private Controller controller;

    /**
     * This is the only constructor.
     * @param game The relevant game.
     */
    public VirtualView(Game game) {
        this.game = game;
        this.controller = new Controller(this);
    }

    /**
     * Getter method for the controller attribute.
     * @return The controller.
     */
    public Controller getController() {
        return controller;
    }

    /**
     * Sends a deliverable to a player's client.
     * @param recipient The recipient of the deliverable.
     * @param deliverable The deliverable of interest.
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException} and the current turn needs to be ended prematurely.
     */
    private int send(Player recipient, Deliverable deliverable) throws AbortedTurnException {
        if(godMode) {
            switch (deliverable.getType()) {
                case INFO:
                    Dispatcher.sendMessage("\n<" + recipient.getName() + "> " + deliverable.getMessage());
                    return 0;
                case DUAL:
                    return Dispatcher.requestBoolean("\n<" + recipient.getName() + "> " + deliverable.getMessage()) ? 1 : 0;
                case MAPPED:
                    return Dispatcher.requestMappedOption("\n<" + recipient.getName() + "> " + deliverable.getMessage(), ((Mapped) deliverable).getOptions(), ((Mapped) deliverable).getKeys());
                case BULK:
                    Dispatcher.sendMessage(((Bulk) deliverable).unpack().toString());
                default:
                    return 0;
            }
        }
        else {
            try {
                recipient.deliver(deliverable);
            } catch (ConnectionException e) {
                throw new AbortedTurnException("");
            }
            if(deliverable.getType() != DeliverableType.INFO)
                return ((Response) recipient.nextDeliverable()).getNumber();
            return 0;
        }
    }

    private void broadcast(Deliverable deliverable) {
        if(!deliverable.getType().equals(DeliverableType.INFO))
            throw new DeliverableException("Wrong call to send in VirtualView.");

        if(godMode) {
            Dispatcher.sendMessage("\n<#ALL> " + deliverable.getMessage());
        }
        else {
            game.getParticipants().forEach(recipient -> {
                try {
                    send(recipient, deliverable);
                } catch (AbortedTurnException ignored) { } // just don't send
            });
        }
    }

    private void sendGameStatus(Player subject) throws AbortedTurnException {
        if(godMode) {
            Deliverable deliverable = new Bulk(DeliverableEvent.BOARD, game.getBoard());
            deliverable.overwriteMessage(game.toString());
            send(subject, deliverable);
        }
    }


    public void spawn(Player subject) throws AbortedTurnException {
        sendGameStatus(subject);

        List<String> options = subject.getPowerUps()
                .stream()
                .map(PowerUp::toString)
                .collect(Collectors.toList());

        int discardIndex = send(subject, new Mapped(DeliverableEvent.SPAWN_REQUEST, options));
        PowerUp powerUpToDiscard = subject.getPowerUps().get(discardIndex);
        controller.spawn(subject, powerUpToDiscard);
        send(subject, new Info(DeliverableEvent.SPAWN_SUCCESS));
    }

    public void discardPowerUp(Player subject) throws AbortedTurnException {
        List<PowerUp> powerUps = subject.getPowerUps();

        List<String> options = powerUps.stream()
                .map(PowerUp::toString)
                .collect(Collectors.toList());

        int discardIndex = send(subject, new Mapped(DeliverableEvent.DISCARD_POWERUP_REQUEST, options));
        PowerUp powerUpToDiscard = powerUps.get(discardIndex);
        controller.discardPowerUp(subject, powerUpToDiscard);
    }

    public void discardWeapon(Player subject) throws AbortedTurnException {
        List<Weapon> weapons = subject.getWeapons();

        List<String> options = weapons.stream()
                .map(Weapon::toString)
                .collect(Collectors.toList());

        int discardIndex = send(subject, new Mapped(DeliverableEvent.DISCARD_WEAPON_REQUEST, options));
        Weapon weaponToDiscard = weapons.get(discardIndex);
        controller.discardWeapon(subject, weaponToDiscard);
    }

    public Execution chooseExecution(Player subject, List<Execution> executions) throws AbortedTurnException {
        sendGameStatus(subject);

        List<String> options = executions.stream()
                .map(Execution::toString)
                .collect(Collectors.toList());

        int choiceIndex = send(subject, new Mapped(DeliverableEvent.CHOOSE_EXECUTION_REQUEST, options));
        return executions.get(choiceIndex);
    }

    public void move(Player subject, List<Cell> cells) throws AbortedTurnException {
        List<String> options = cells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());

        List<Integer> cellIds = cells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        int destinationIndex = send(subject, new Mapped(DeliverableEvent.MOVE_REQUEST, options, cellIds));
        Cell destination = cells.get(destinationIndex);
        controller.move(subject, destination);
    }

    public void grabAmmo(Player subject) throws AbortedTurnException {
        boolean success = controller.grabAmmo(subject);
        if(success)
            send(subject, new Info(DeliverableEvent.GRAB_AMMO_SUCCESS));
        else
            send(subject, new Info(DeliverableEvent.GRAB_AMMO_FAILURE));
    }

    public void grabWeapon(Player subject) throws AbortedTurnException {

        List<Weapon> weapons = ((SpawnCell) subject.getPosition()).getWeaponShop()
                .stream()
                .filter(w -> subject.canAffordWithPowerUps(w.getPurchaseCost()))
                .collect(Collectors.toList());

        if(weapons.size() == 0)
            return;

        List<String> options = weapons.stream()
                .map(Weapon::toString)
                .collect(Collectors.toList());

        boolean doPurchase = send(subject, new Dual(DeliverableEvent.GRAB_WEAPON_REQUEST_IF)) != 0;
        if(!doPurchase)
            return;

        int purchaseIndex = send(subject, new Mapped(DeliverableEvent.GRAB_WEAPON_REQUEST_WHICH, options));
        Weapon weaponToPurchase = weapons.get(purchaseIndex);
        List<PowerUp> powerUps = new ArrayList<>(); // power-ups can be used to cover the costs of buying a weapon

        while(!subject.getAmmoCubes().augment(powerUps).covers(weaponToPurchase.getPurchaseCost())) {

            List<PowerUp> suitableForPaymentPowerUps = subject.getAmmoCubes()
                    .filterValidAugmenters(subject.getPowerUps(), weaponToPurchase.getPurchaseCost());

            List<String> options1 = suitableForPaymentPowerUps.stream().map(PowerUp::toString).collect(Collectors.toList());

            int suitableForPaymentPowerUpIndex = send(subject, new Mapped(DeliverableEvent.GRAB_WEAPON_NEEDS_POWERUP, options1));
            PowerUp payablePowerUp = suitableForPaymentPowerUps.get(suitableForPaymentPowerUpIndex);
            powerUps.add(payablePowerUp);
        }

        send(subject, new Info(DeliverableEvent.GRAB_WEAPON_SUCCESS));
        int weaponShopIndex = ((SpawnCell) subject.getPosition()).getWeaponShop().indexOf(weaponToPurchase);
        controller.grabWeapon(subject, weaponShopIndex);
        powerUps.forEach(subject::discardPowerUp);
    }

    public void shoot(Player subject) throws AbortedTurnException {
        List<Weapon> availableWeapons = subject.getWeapons()
                .stream()
                .filter(Weapon::isLoaded)
                .collect(Collectors.toList()); // gather all of the player's loaded weapons
                // at the time this method is called and entered, it is assumed that the player is actually able to shoot with at least one weapon

        List<String> options = availableWeapons.stream().map(Weapon::toString).collect(Collectors.toList());

        int weaponIndex = send(subject, new Mapped(DeliverableEvent.SHOOT_WEAPON_REQUEST, options));
        Weapon weapon = availableWeapons.get(weaponIndex);

        AttackPattern pattern = weapon.getPattern();
        controller.prepareForShoot(subject, pattern);

        shootAttackModule(subject, pattern, pattern.getFirst());
        weapon.unload();
    }

    public void shootAttackModule(Player subject, AttackPattern pattern, List<Integer> next) throws AbortedTurnException {
        sendGameStatus(subject);

        List<String> options = next.stream()
                .map(i -> {
                    if(i == -1) // -1 ends action
                        return "End action";
                    return pattern.getModule(i).toString();
                })
                .collect(Collectors.toList());



        int moduleIndex = send(subject, new Mapped(DeliverableEvent.SHOOT_MODULE_REQUEST, options));
        int moduleId = next.get(moduleIndex);
        controller.shoot(subject, pattern, moduleId);
    }

    public void acquireTargets(Player subject, AttackModule attackModule, List<Target> targets) throws AbortedTurnException {
        for(Target target : targets) {
            switch(target.getType()) {
                case PLAYER:
                    Player acquiredPlayer;
                    try {
                        acquiredPlayer = shootPlayer(subject, (TargetPlayer) target);
                    } catch (NoValidTargetsException e) {
                        return;
                    }
                    ((TargetPlayer) target).setPlayer(acquiredPlayer);
                    break;

                case CELL:
                    Cell acquiredCell;
                    try {
                        acquiredCell = shootCell(subject, (TargetCell) target);
                    } catch (NoValidTargetsException e) {
                        return;
                    }
                    ((TargetCell) target).setCell(acquiredCell);
                    break;

                case ROOM:
                    Room acquiredRoom;
                    try {
                        acquiredRoom = shootRoom(subject, (TargetRoom) target);
                    } catch (NoValidTargetsException e) {
                        return;
                    }
                    ((TargetRoom) target).setRoom(acquiredRoom);
                    break;

                default:
                    break;
            }
        }
        controller.shootTargets(subject, attackModule, targets);
    }

    private Player shootPlayer(Player subject, TargetPlayer target) throws NoValidTargetsException, AbortedTurnException {
        List<Player> players = target.filter();
        if(players.size() == 0) {
            send(subject, new Info(DeliverableEvent.SHOOT_PLAYER_FAILURE));
            throw new NoValidTargetsException("No rooms available to target");
        }

        List<String> playerNames = players.stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        Deliverable deliverable = new Mapped(DeliverableEvent.TARGET_REQUEST, playerNames);
        deliverable.overwriteMessage(target.getMessage());
        int playerIndex = send(subject, deliverable);
        return players.get(playerIndex);
    }

    private Cell shootCell(Player subject, TargetCell target) throws NoValidTargetsException, AbortedTurnException {
        List<Cell> cells = target.filter();
        if(cells.size() == 0) {
            send(subject, new Info(DeliverableEvent.SHOOT_CELL_FAILURE));
            throw new NoValidTargetsException("No rooms available to target");
        }

        List<String> cellNames = cells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());

        List<Integer> cellIds = cells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        Deliverable deliverable = new Mapped(DeliverableEvent.TARGET_REQUEST, cellNames, cellIds);
        deliverable.overwriteMessage(target.getMessage());

        int cellIndex = send(subject, deliverable);
        return cells.get(cellIndex);
    }

    private Room shootRoom(Player subject, TargetRoom target) throws NoValidTargetsException, AbortedTurnException {
        List<Room> rooms = target.filter();
        if (rooms.size() == 0) {
            send(subject, new Info(DeliverableEvent.SHOOT_ROOM_FAILURE));
            throw new NoValidTargetsException("No rooms available to target");
        }

        List<String> roomNames = rooms.stream()
                .map(Room::toString)
                .collect(Collectors.toList());

        Deliverable deliverable = new Mapped(DeliverableEvent.TARGET_REQUEST, roomNames);
        deliverable.overwriteMessage(target.getMessage());

        int roomIndex = send(subject, deliverable);
        return rooms.get(roomIndex);
    }

    public void reload(Player subject) throws AbortedTurnException {
        List<Weapon> weapons = subject.getWeapons().stream()
                .filter(w -> !w.isLoaded())
                .filter(w -> subject.canAffordWithPowerUps(w.getReloadCost()))
                .collect(Collectors.toList());

        boolean keepReloading = true;

        while(weapons.size() > 0 && keepReloading) {
            keepReloading = send(subject, new Dual(DeliverableEvent.RELOAD_REQUEST_IF)) != 0;

            if(keepReloading) {
                List<String> options = weapons.stream().map(Weapon::toString).collect(Collectors.toList());
                int reloadIndex = send(subject, new Mapped(DeliverableEvent.RELOAD_REQUEST_WHICH, options));
                Weapon weaponToReload = weapons.get(reloadIndex);
                List<PowerUp> powerUps = new ArrayList<>(); // power-ups can be used to cover the costs of reloading a weapon

                while(!subject.getAmmoCubes().augment(powerUps).covers(weaponToReload.getReloadCost())) {

                    List<PowerUp> suitableForPaymentPowerUps = subject.getAmmoCubes()
                            .filterValidAugmenters(subject.getPowerUps(), weaponToReload.getReloadCost());

                    List<String> options1 = suitableForPaymentPowerUps.stream()
                            .map(PowerUp::toString)
                            .collect(Collectors.toList());

                    int suitableForPaymentPowerUpIndex = send(subject, new Mapped(DeliverableEvent.RELOAD_NEEDS_POWERUP, options1));
                    PowerUp payablePowerUp = suitableForPaymentPowerUps.get(suitableForPaymentPowerUpIndex);
                    powerUps.add(payablePowerUp);
                }

                send(subject, new Info(DeliverableEvent.RELOAD_SUCCESS));
                controller.reload(subject, weaponToReload);
                powerUps.forEach(subject::discardPowerUp);
            }

            // re-evaluate situation before continuing
            weapons = subject.getWeapons().stream()
                    .filter(w -> !w.isLoaded())
                    .filter(w -> subject.canAffordWithPowerUps(w.getReloadCost()))
                    .collect(Collectors.toList());
        }
    }

    public void usePowerUp(Player subject) throws AbortedTurnException {
        List<PowerUp> powerUps = subject.getPowerUps()
                .stream()
                .filter(p -> p.getType() != PowerUpType.GRENADE) // can't use grenade arbitrarily
                .filter(p -> p.getType() != PowerUpType.SCOPE) // can't use scope arbitrarily
                .collect(Collectors.toList());

        if(powerUps.size() == 0)
            return;

        List<String> options = powerUps.stream().map(PowerUp::toString).collect(Collectors.toList());

        boolean usePowerUp = send(subject, new Dual(DeliverableEvent.POWERUP_REQUEST_IF)) != 0;
        if(!usePowerUp)
            return;

        int powerUpIndex = send(subject, new Mapped(DeliverableEvent.POWERUP_REQUEST_WHICH, options));
        PowerUp powerUp = powerUps.get(powerUpIndex);
        subject.discardPowerUp(powerUp);
        controller.usePowerUp(subject, powerUp);
    }

    public void scope(Damage damage, List<Player> targets) throws AbortedTurnException {
        Player subject = damage.getAuthor();
        List<PowerUp> scopes = subject.getScopes();
        List<Player> scopedPlayers = new ArrayList<>();
        boolean hasRed = subject.canAfford(AmmoCubes.red());
        boolean hasYellow = subject.canAfford(AmmoCubes.yellow());
        boolean hasBlue = subject.canAfford(AmmoCubes.blue());
        boolean useScope = true;
        while(scopes.size() > 0 && useScope && (hasRed || hasYellow || hasBlue)) {
            useScope = send(subject, new Dual(DeliverableEvent.SCOPE_REQUEST_IF)) != 0;
            if(useScope) {
                List<String> options = scopes.stream()
                        .map(PowerUp::toString)
                        .collect(Collectors.toList());

                int scopeId = send(subject, new Mapped(DeliverableEvent.SCOPE_REQUEST_WHICH, options));
                PowerUp scope = scopes.get(scopeId);
                subject.discardPowerUp(scope);

                List<AmmoCubes> unitCubes = new ArrayList<>();
                if(hasRed)
                    unitCubes.add(AmmoCubes.red());
                if(hasYellow)
                    unitCubes.add(AmmoCubes.yellow());
                if(hasBlue)
                    unitCubes.add(AmmoCubes.blue());

                List<String> options1 = unitCubes.stream()
                        .map(AmmoCubes::toStringAsColor)
                        .collect(Collectors.toList());

                int unitCubeIndex = send(subject, new Mapped(DeliverableEvent.SCOPE_REQUEST_AMMO, options1));
                AmmoCubes fee = unitCubes.get(unitCubeIndex);
                try {
                    subject.takeAmmoCubes(fee);
                } catch (CannotAffordException ignored) { } // this theoretically isn't possible

                List<String> options2 = targets.stream()
                        .map(Player::toString)
                        .collect(Collectors.toList());

                int targetId = send(subject, new Mapped(DeliverableEvent.SCOPE_REQUEST_TARGET, options2));
                scopedPlayers.add(targets.get(targetId));
                scopes = subject.getScopes();
            }
        }
        controller.scope(damage, targets, scopedPlayers);
    }

    public void grenade(Player subject, Player originalAttacker) throws AbortedTurnException {
        List<PowerUp> grenades = subject.getGrenades();
        boolean useGrenade = send(subject, new Dual(DeliverableEvent.GRENADE_REQUEST_IF)) != 0;
        if(!useGrenade)
            return;

        List<String> options = grenades.stream()
                .map(PowerUp::toString)
                .collect(Collectors.toList());

        int grenadeIndex = send(subject, new Mapped(DeliverableEvent.GRENADE_REQUEST_WHICH, options));
        PowerUp grenade = grenades.get(grenadeIndex);
        subject.discardPowerUp(grenade);
        controller.grenade(subject, originalAttacker);
    }

    public void newton(Player subject) throws AbortedTurnException {
        List<Player> targetPlayers = game.getParticipants()
                .stream()
                .filter(p -> !p.equals(subject))
                .filter(p -> p.getPosition() != null)
                .collect(Collectors.toList());

        if(targetPlayers.size() == 0)
            return;

        List<String> playerNames = targetPlayers.stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        int targetPlayerId = send(subject, new Mapped(DeliverableEvent.NEWTON_REQUEST_PLAYER, playerNames));
        Player targetPlayer = targetPlayers.get(targetPlayerId);

        List<Cell> targetCells = game.getBoard()
                .getCells()
                .stream()
                .filter(c -> subject.getPosition().canSee(c))
                .filter(c -> {
                    try {
                        return targetPlayer.getPosition().distance(c) <= Newton.getMaxDistance();
                    } catch (NullCellOperationException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if(targetCells.size() == 0) {
            send(subject, new Info(DeliverableEvent.NEWTON_FAILURE));
            return;
        }

        List<String> cellNames = targetCells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());

        List<Integer> cellIds = targetCells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        int targetCellId = send(subject, new Mapped(DeliverableEvent.NEWTON_REQUEST_CELL, cellNames, cellIds));
        Cell targetCell = targetCells.get(targetCellId);

        controller.newton(targetPlayer, targetCell);
    }

    public void teleport(Player subject) throws AbortedTurnException {
        List<Cell> targetCells = game.getBoard().getCells();

        List<String> cellNames = targetCells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());

        List<Integer> cellIds = targetCells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        int cellIndex = send(subject, new Mapped(DeliverableEvent.TELEPORT_REQUEST_CELL, cellNames, cellIds));
        Cell destination = targetCells.get(cellIndex);
        controller.teleport(subject, destination);
    }

    public void announceDamage(Player author, Player target, int amount) {
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_DAMAGE);
        deliverable.overwriteMessage(author.getName() + " dealt " + amount + " damage to " + target.getName() + ".");
        broadcast(deliverable);
    }

    public void announceMarking(Player author, Player target, int amount) {
        String lexeme = amount > 1 ? "marks" : "mark";
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_MARKING);
        deliverable.overwriteMessage(author.getName() + " dealt " + amount + " " + lexeme + " to " + target.getName() + ".");
        broadcast(deliverable);
    }

    public void announceMove(Player author, Player target, Cell destination) {
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_MOVE);
        String message = author.getName() + " moved";
        if(!target.equals(author))
            message += " " + target.getName();
        deliverable.overwriteMessage(message + " to " + destination.toString() + ".");
        broadcast(deliverable);
    }

    public void announceKill(Player author, Player target) {
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_KILL);
        String message = author.getName() +  " ";
        if(target.isOverKilled())
            message += "over";
        message += "killed " + target.getName() + "!";
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }

    public void announceScore(Player source, Player creditor, int amount, boolean firstBloodOrDoubleKill) {
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_SCORE);
        String lexeme = amount == 1 ? "point" : "points";
        String message = creditor + " earned " + amount + " " + lexeme;
        if(source == null) {
            if(firstBloodOrDoubleKill)
                message += " for scoring a doubleKill.";
            else
                message += " from the killshot track.";
        }
        else if (firstBloodOrDoubleKill)
            message += " for drawing first blood on " + source + ".";
        else
            message += " for damaging " + source + ".";
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }

    public void announceFrenzy(Player cause) {
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_FRENZY);
        deliverable.overwriteMessage(cause.toString() + " activated the Final Frenzy!");
        broadcast(deliverable);
    }

    public void announceWinner(List<Player> ranking) {
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_WINNER);
        String message = "\n\nGAME OVER!\n" + ranking.get(0).toString() + " won the game!\n\nRanking:\n" + Table.create(
                ranking.stream()
                        .map(ranking::indexOf)
                        .map(i -> i + 1)
                        .map(s -> "#" + s)
                        .collect(Collectors.toList()),
                ranking.stream()
                        .map(Player::toString)
                        .collect(Collectors.toList()),
                ranking.stream()
                        .map(Player::getScore)
                        .map(i -> i + " " + (i == 1 ? "point" : "points"))
                        .collect(Collectors.toList())
        );
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }

    public void announceDisconnect(Player disconnectedPlayer) {
        Deliverable deliverable = new Info(DeliverableEvent.ANNOUNCE_DISCONNECT);
        String message = disconnectedPlayer + " has lost connection. Skipping to the next turn...";
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }
}
