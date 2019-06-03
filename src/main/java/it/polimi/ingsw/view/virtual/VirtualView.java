package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.AbortedTurnException;
import it.polimi.ingsw.model.exceptions.DeliverableException;
import it.polimi.ingsw.model.exceptions.NoValidTargetsException;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
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

public class VirtualView {
    private Game game;
    private Controller controller;

    public VirtualView(Game game) {
        this.game = game;
        this.controller = new Controller(this);
    }

    public Controller getController() {
        return controller;
    }

    private void sendInfo(Player recipient, Deliverable deliverable) throws AbortedTurnException {
        if(!deliverable.getType().equals(DeliverableType.INFO))
            throw new DeliverableException("Wrong call to sendInfo in VirtualView.");

        if(godMode) {
            Dispatcher.sendMessage(game.toString());
            Dispatcher.sendMessage("<" + recipient.getName() + "> " + deliverable.getMessage());
        }
        else {
            try {
                recipient.deliver(deliverable);
            } catch (ConnectionException e) {
                throw new AbortedTurnException("");
            }
        }
    }

    private boolean sendDual(Player recipient, Deliverable deliverable) throws AbortedTurnException {
        if(!deliverable.getType().equals(DeliverableType.DUAL))
            throw new DeliverableException("Wrong call to sendDual in VirtualView.");

        if(godMode) {
            Dispatcher.sendMessage(game.toString());
            return Dispatcher.requestBoolean("<" + recipient.getName() + "> " + deliverable.getMessage());
        } else {
            try {
                recipient.deliver(deliverable);
            } catch (ConnectionException e) {
                throw new AbortedTurnException("");
            }
            return recipient.nextDeliverable().unpack() != 0;
        }
    }

    private int sendListed(Player recipient, Deliverable deliverable) throws AbortedTurnException {
        if(!deliverable.getType().equals(DeliverableType.LISTED))
            throw new DeliverableException("Wrong call to sendListed in VirtualView.");

        if(godMode) {
            Dispatcher.sendMessage(game.toString());
            return Dispatcher.requestListedOption("<" + recipient.getName() + "> " + deliverable.getMessage(), deliverable.getOptions());
        }
        else {
            try {
                recipient.deliver(deliverable);
            } catch (ConnectionException e) {
                throw new AbortedTurnException("");
            }
            return recipient.nextDeliverable().unpack();
        }
    }


    private int sendMapped(Player recipient, Deliverable deliverable) throws AbortedTurnException {
        if(!deliverable.getType().equals(DeliverableType.MAPPED))
            throw new DeliverableException("Wrong call to sendMapped in VirtualView.");

        if(godMode) {
            Dispatcher.sendMessage(game.toString());
            return Dispatcher.requestMappedOption("<" + recipient.getName() + "> " + deliverable.getMessage(), deliverable.getOptions(), deliverable.getKeys());
        }
        else {
            try {
                recipient.deliver(deliverable);
            } catch (ConnectionException e) {
                throw new AbortedTurnException("");
            }
            return recipient.nextDeliverable().unpack();
        }
    }

    private void broadcast(Deliverable deliverable) {
        game.getParticipants().forEach(recipient -> {
            try {
                sendInfo(recipient, deliverable);
            } catch (AbortedTurnException ignored) { } // just don't send
        });
    }

    public void spawn(Player subject) throws AbortedTurnException {
        List<String> options = subject.getPowerUps()
                .stream()
                .map(PowerUp::toString)
                .collect(Collectors.toList());

        int discardIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.SPAWN_REQUEST, options));
        PowerUp powerUpToDiscard = subject.getPowerUps().get(discardIndex);
        controller.spawn(subject, powerUpToDiscard);
        sendInfo(subject, Deliverable.info(DeliverableEvent.SPAWN_SUCCESS));
    }

    public void discardPowerUp(Player subject) throws AbortedTurnException {
        List<PowerUp> powerUps = subject.getPowerUps();

        List<String> options = powerUps.stream()
                .map(PowerUp::toString)
                .collect(Collectors.toList());

        int discardIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.DISCARD_POWERUP_REQUEST, options));
        PowerUp powerUpToDiscard = powerUps.get(discardIndex);
        controller.discardPowerUp(subject, powerUpToDiscard);
    }

    public void discardWeapon(Player subject) throws AbortedTurnException {
        List<Weapon> weapons = subject.getWeapons();

        List<String> options = weapons.stream()
                .map(Weapon::toString)
                .collect(Collectors.toList());

        int discardIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.DISCARD_WEAPON_REQUEST, options));
        Weapon weaponToDiscard = weapons.get(discardIndex);
        controller.discardWeapon(subject, weaponToDiscard);
    }

    public Execution chooseExecution(Player subject, List<Execution> executions) throws AbortedTurnException {

        List<String> options = executions.stream()
                .map(Execution::toString)
                .collect(Collectors.toList());

        int choiceIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.CHOOSE_EXECUTION_REQUEST, options));
        return executions.get(choiceIndex);
    }

    public void move(Player subject, List<Cell> cells) throws AbortedTurnException {
        List<String> options = cells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());

        List<Integer> cellIds = cells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        int destinationIndex = sendMapped(subject, Deliverable.mapped(DeliverableEvent.MOVE_REQUEST, options, cellIds));
        Cell destination = cells.get(destinationIndex);
        controller.move(subject, destination);
    }

    public void grabAmmo(Player subject) throws AbortedTurnException {
        boolean success = controller.grabAmmo(subject);
        if(success)
            sendInfo(subject, Deliverable.info(DeliverableEvent.GRAB_AMMO_SUCCESS));
        else
            sendInfo(subject, Deliverable.info(DeliverableEvent.GRAB_AMMO_FAILURE));
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

        boolean doPurchase = sendDual(subject, Deliverable.dual(DeliverableEvent.GRAB_WEAPON_REQUEST_IF));
        if(!doPurchase)
            return;

        int purchaseIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.GRAB_WEAPON_REQUEST_WHICH, options));
        Weapon weaponToPurchase = weapons.get(purchaseIndex);
        List<PowerUp> powerUps = new ArrayList<>(); // power-ups can be used to cover the costs of buying a weapon

        while(!subject.getAmmoCubes().augment(powerUps).covers(weaponToPurchase.getPurchaseCost())) {

            List<PowerUp> suitableForPaymentPowerUps = subject.getAmmoCubes()
                    .filterValidAugmenters(subject.getPowerUps(), weaponToPurchase.getPurchaseCost());

            List<String> options1 = suitableForPaymentPowerUps.stream().map(PowerUp::toString).collect(Collectors.toList());

            int suitableForPaymentPowerUpIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.GRAB_WEAPON_NEEDS_POWERUP, options1));
            PowerUp payablePowerUp = suitableForPaymentPowerUps.get(suitableForPaymentPowerUpIndex);
            powerUps.add(payablePowerUp);
        }

        sendInfo(subject, Deliverable.info(DeliverableEvent.GRAB_WEAPON_SUCCESS));
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

        int weaponIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.SHOOT_WEAPON_REQUEST, options));
        Weapon weapon = availableWeapons.get(weaponIndex);

        AttackPattern pattern = weapon.getPattern();
        controller.prepareForShoot(subject, pattern);

        shootAttackModule(subject, pattern, pattern.getFirst());
        weapon.unload();
    }

    public void shootAttackModule(Player subject, AttackPattern pattern, List<Integer> next) throws AbortedTurnException {
        List<String> options = next.stream()
                .map(i -> {
                    if(i == -1) // -1 ends action
                        return "End action";
                    return pattern.getModule(i).toString();
                })
                .collect(Collectors.toList());



        int moduleIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.SHOOT_MODULE_REQUEST, options));
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
            sendInfo(subject, Deliverable.info(DeliverableEvent.SHOOT_PLAYER_FAILURE));
            throw new NoValidTargetsException("No rooms available to target");
        }

        List<String> playerNames = players.stream()
                .map(Player::getName)
                .collect(Collectors.toList());

        Deliverable deliverable = Deliverable.listed(DeliverableEvent.TARGET_REQUEST, playerNames);
        deliverable.overwriteMessage(target.getMessage());
        int playerIndex = sendListed(subject, deliverable);
        return players.get(playerIndex);
    }

    private Cell shootCell(Player subject, TargetCell target) throws NoValidTargetsException, AbortedTurnException {
        List<Cell> cells = target.filter();
        if(cells.size() == 0) {
            sendInfo(subject, Deliverable.info(DeliverableEvent.SHOOT_CELL_FAILURE));
            throw new NoValidTargetsException("No rooms available to target");
        }

        List<String> cellNames = cells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());

        List<Integer> cellIds = cells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        Deliverable deliverable = Deliverable.mapped(DeliverableEvent.TARGET_REQUEST, cellNames, cellIds);
        deliverable.overwriteMessage(target.getMessage());

        int cellIndex = sendMapped(subject, deliverable);
        return cells.get(cellIndex);
    }

    private Room shootRoom(Player subject, TargetRoom target) throws NoValidTargetsException, AbortedTurnException {
        List<Room> rooms = target.filter();
        if (rooms.size() == 0) {
            sendInfo(subject, Deliverable.info(DeliverableEvent.SHOOT_ROOM_FAILURE));
            throw new NoValidTargetsException("No rooms available to target");
        }

        List<String> roomNames = rooms.stream()
                .map(Room::toString)
                .collect(Collectors.toList());

        Deliverable deliverable = Deliverable.listed(DeliverableEvent.TARGET_REQUEST, roomNames);
        deliverable.overwriteMessage(target.getMessage());

        int roomIndex = sendListed(subject, deliverable);
        return rooms.get(roomIndex);
    }

    public void reload(Player subject) throws AbortedTurnException {
        List<Weapon> weapons = subject.getWeapons().stream()
                .filter(w -> !w.isLoaded())
                .filter(w -> subject.canAffordWithPowerUps(w.getReloadCost()))
                .collect(Collectors.toList());

        boolean keepReloading = true;

        while(weapons.size() > 0 && keepReloading) {
            keepReloading = sendDual(subject, Deliverable.dual(DeliverableEvent.RELOAD_REQUEST_IF));

            if(keepReloading) {
                List<String> options = weapons.stream().map(Weapon::toString).collect(Collectors.toList());
                int reloadIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.RELOAD_REQUEST_WHICH, options));
                Weapon weaponToReload = weapons.get(reloadIndex);
                List<PowerUp> powerUps = new ArrayList<>(); // power-ups can be used to cover the costs of reloading a weapon

                while(!subject.getAmmoCubes().augment(powerUps).covers(weaponToReload.getReloadCost())) {

                    List<PowerUp> suitableForPaymentPowerUps = subject.getAmmoCubes()
                            .filterValidAugmenters(subject.getPowerUps(), weaponToReload.getReloadCost());

                    List<String> options1 = suitableForPaymentPowerUps.stream()
                            .map(PowerUp::toString)
                            .collect(Collectors.toList());

                    int suitableForPaymentPowerUpIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.RELOAD_NEEDS_POWERUP, options1));
                    PowerUp payablePowerUp = suitableForPaymentPowerUps.get(suitableForPaymentPowerUpIndex);
                    powerUps.add(payablePowerUp);
                }

                sendInfo(subject, Deliverable.info(DeliverableEvent.RELOAD_SUCCESS));
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

        boolean usePowerUp = sendDual(subject, Deliverable.dual(DeliverableEvent.POWERUP_REQUEST_IF));
        if(!usePowerUp)
            return;

        int powerUpIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.POWERUP_REQUEST_WHICH, options));
        PowerUp powerUp = powerUps.get(powerUpIndex);
        subject.discardPowerUp(powerUp);
        controller.usePowerUp(subject, powerUp);
    }

    public void scope(Damage damage, List<Player> targets) throws AbortedTurnException {
        Player subject = damage.getAuthor();
        List<PowerUp> scopes = subject.getScopes();
        List<Player> scopedPlayers = new ArrayList<>();
        boolean useScope = true;
        while(scopes.size() > 0 && useScope) {
            useScope = sendDual(subject, Deliverable.dual(DeliverableEvent.SCOPE_REQUEST_IF));
            if(useScope) {
                List<String> options = scopes.stream()
                        .map(PowerUp::toString)
                        .collect(Collectors.toList());

                int scopeId = sendListed(subject, Deliverable.listed(DeliverableEvent.SCOPE_REQUEST_WHICH, options));
                PowerUp scope = scopes.get(scopeId);
                subject.discardPowerUp(scope);

                List<String> options1 = targets.stream()
                        .map(Player::toString)
                        .collect(Collectors.toList());

                int targetId = sendListed(subject, Deliverable.listed(DeliverableEvent.SCOPE_REQUEST_TARGET, options1));
                scopedPlayers.add(targets.get(targetId));
                scopes = subject.getScopes();
            }
        }
        controller.scope(damage, targets, scopedPlayers);
    }

    public void grenade(Player subject, Player originalAttacker) throws AbortedTurnException {
        List<PowerUp> grenades = subject.getGrenades();
        boolean useGrenade = sendDual(subject, Deliverable.dual(DeliverableEvent.GRENADE_REQUEST_IF));
        if(!useGrenade)
            return;

        List<String> options = grenades.stream()
                .map(PowerUp::toString)
                .collect(Collectors.toList());

        int grenadeIndex = sendListed(subject, Deliverable.listed(DeliverableEvent.GRENADE_REQUEST_WHICH, options));
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

        int targetPlayerId = sendListed(subject, Deliverable.listed(DeliverableEvent.NEWTON_REQUEST_PLAYER, playerNames));
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
            sendInfo(subject, Deliverable.info(DeliverableEvent.NEWTON_FAILURE));
            return;
        }

        List<String> cellNames = targetCells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());

        List<Integer> cellIds = targetCells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        int targetCellId = sendMapped(subject, Deliverable.mapped(DeliverableEvent.NEWTON_REQUEST_CELL, cellNames, cellIds));
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

        int cellIndex = sendMapped(subject, Deliverable.mapped(DeliverableEvent.TELEPORT_REQUEST_CELL, cellNames, cellIds));
        Cell destination = targetCells.get(cellIndex);
        controller.teleport(subject, destination);
    }

    public void announceDamage(Player author, Player target, int amount) {
        Deliverable deliverable = Deliverable.info(DeliverableEvent.ANNOUNCE);
        deliverable.overwriteMessage(author.getName() + " dealt " + amount + " damage to " + target.getName() + ".");
        broadcast(deliverable);
    }

    public void announceMarking(Player author, Player target, int amount) {
        String lexeme = amount > 1 ? "marks" : "mark";
        Deliverable deliverable = Deliverable.info(DeliverableEvent.ANNOUNCE);
        deliverable.overwriteMessage(author.getName() + " dealt " + amount + " " + lexeme + " to " + target.getName() + ".");
        broadcast(deliverable);
    }

    public void announceMove(Player author, Player target, Cell destination) {
        Deliverable deliverable = Deliverable.info(DeliverableEvent.ANNOUNCE);
        String message = author.getName() + " moved";
        if(!target.equals(author))
            message += " " + target.getName();
        deliverable.overwriteMessage(message + " to " + destination.toString() + ".");
        broadcast(deliverable);
    }

    public void announceKill(Player author, Player target) {
        Deliverable deliverable = Deliverable.info(DeliverableEvent.ANNOUNCE);
        String message = author.getName() +  " ";
        if(target.isOverKilled())
            message += "over";
        message += "killed " + target.getName() + "!";
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }

    public void announceFrenzy(Player cause) {
        Deliverable deliverable = Deliverable.info(DeliverableEvent.ANNOUNCE);
        deliverable.overwriteMessage(cause.toString() + " activated the Final Frenzy!");
        broadcast(deliverable);
    }

    public void announceWinner(List<Player> ranking) {
        Deliverable deliverable = Deliverable.info(DeliverableEvent.ANNOUNCE);
        String message = ranking.get(0).toString() + " won the game!\n\nRanking:\n" + Table.create(
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
                        .map(s -> s + " points")
                        .collect(Collectors.toList())
        );
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }

    public void announceDisconnect(Player disconnectedPlayer) {
        Deliverable deliverable = Deliverable.info(DeliverableEvent.ANNOUNCE);
        String message = disconnectedPlayer + " has lost connection. Skipping to the next turn.";
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }
}
