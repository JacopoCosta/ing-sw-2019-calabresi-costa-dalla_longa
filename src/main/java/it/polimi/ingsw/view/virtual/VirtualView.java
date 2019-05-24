package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.DistanceFromNullException;
import it.polimi.ingsw.model.player.Execution;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.Newton;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.powerups.PowerUpType;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.effects.Damage;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;
import it.polimi.ingsw.model.weaponry.targets.TargetRoom;
import it.polimi.ingsw.view.remote.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Important note: this class invokes the Dispatcher several times in its "send..." methods.
// This will no longer be in the final version of the game, as the Dispatcher, unlike this class,
// will be running on the client side, making it unreachable without a network protocol bridging the two.
// For this reason, all of the calls to the Dispatcher will have to be replaced with equivalent network protocol messages.
// In addition to this, the "Deliverable" enum will be used by the client to determine what to display,
// deciding autonomously whether to display it using a CLI or a GUI. Should the preferred output method be a CLI,
// every instance of Deliverable offers, for this purpose, a .toString() method.
public class VirtualView {

    private Game game;
    private Controller controller;

    public VirtualView(Game game) {
        this.game = game;
        this.controller = game.getController();
    }

    private void sendMessage(Player recipient, Deliverable message) {
        System.out.println(game.toString()); // temp
        Dispatcher.sendMessage(message.message);
    }

    private int sendRequest(Player recipient, Deliverable request, List<?> values, List<Integer> keys) {
        System.out.println(game.toString()); // temp
        return Dispatcher.requestNumberedOption(request.message, values, keys);
    }

    private int sendRequest(Player recipient, Deliverable request, List<?> values) {
        System.out.println(game.toString()); // temp
        return Dispatcher.requestIndex(request.message, values);
    }

    private boolean sendRequest(Player recipient, Deliverable request) {
        System.out.println(game.toString()); // temp
        return Dispatcher.requestBoolean(request.message);
    }

    private void broadcast(Deliverable message) {
        game.getParticipants().forEach(p -> sendMessage(p, message));
    }

    public void spawn(Player subject, List<PowerUp> options) {
        int keepIndex = sendRequest(subject, Deliverable.SPAWN_REQUEST, options);
        PowerUp powerUpToKeep = options.get(keepIndex);
        PowerUp powerUpToRespawn = options.get(1 - keepIndex);
        controller.spawn(subject, powerUpToKeep, powerUpToRespawn);
        sendMessage(subject, Deliverable.SPAWN_SUCCESS);
    }

    public void discardPowerUp(Player subject) {
        List<PowerUp> powerUps = subject.getPowerUps();
        int discardIndex = sendRequest(subject, Deliverable.DISCARD_POWERUP_REQUEST, powerUps);
        PowerUp toDiscard = powerUps.get(discardIndex);
        controller.discardPowerUp(subject, toDiscard);
    }

    public void discardWeapon(Player subject) {
        List<Weapon> weapons = subject.getWeapons();
        int discardIndex = sendRequest(subject, Deliverable.DISCARD_WEAPON_REQUEST, weapons);
        Weapon toDiscard = weapons.get(discardIndex);
        controller.discardWeapon(subject, toDiscard);
    }

    public Execution chooseExecution(Player subject, List<Execution> options) {
        int choiceIndex = sendRequest(subject, Deliverable.CHOOSE_EXECUTION_REQUEST, options);
        return options.get(choiceIndex);
    }

    public void move(Player subject, List<Cell> options) {
        List<Integer> cellIds = options.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());
        int destinationIndex = sendRequest(subject, Deliverable.MOVE_REQUEST, options, cellIds);
        Cell destination = options.get(destinationIndex);
        controller.move(subject, destination);
    }

    public void grabAmmo(Player subject) {
        boolean success = controller.grabAmmo(subject);
        if(success)
            sendMessage(subject, Deliverable.GRAB_AMMO_SUCCESS);
        else
            sendMessage(subject, Deliverable.GRAB_AMMO_FAILURE);
    }

    public void grabWeapon(Player subject) {
        boolean doPurchase = sendRequest(subject, Deliverable.GRAB_WEAPON_REQUEST_IF);
        if(!doPurchase)
            return;
        SpawnCell cell = (SpawnCell) subject.getPosition();
        int weaponIndex = sendRequest(subject, Deliverable.GRAB_WEAPON_REQUEST_WHICH, cell.getWeaponShop());
        boolean success = controller.grabWeapon(subject, weaponIndex);
        if(success)
            sendMessage(subject, Deliverable.GRAB_WEAPON_SUCCESS);
        else
            sendMessage(subject, Deliverable.GRAB_WEAPON_FAILURE);
    }

    public void shoot(Player subject) {
        List<Weapon> availableWeapons = subject.getWeapons()
                .stream()
                .filter(Weapon::isLoaded)
                .collect(Collectors.toList()); // gather all of the player's loaded weapons
                // at the time this method is called and entered, it is assumed that the player is actually able to shoot with at least one weapon

        int weaponIndex = sendRequest(subject, Deliverable.SHOOT_WEAPON_REQUEST, availableWeapons);
        Weapon weapon = availableWeapons.get(weaponIndex);

        AttackPattern pattern = weapon.getPattern();
        controller.prepareForShoot(subject, pattern);

        shootAttackModule(subject, pattern, pattern.getFirst());
        weapon.unload();
    }

    public void shootAttackModule(Player subject, AttackPattern pattern, List<Integer> next) {
        List<String> nextNames = next.stream()
                .filter(i -> {
                    try {
                        return !pattern.getModule(i).isUsed();
                    } catch (IndexOutOfBoundsException e) { // -1 needs to pass
                        return true;
                    }
                })
                .map(i -> {
                    try {
                        return pattern.getModule(i).getName() + ": " + pattern.getModule(i).getDescription();
                    } catch (IndexOutOfBoundsException e) { // -1 ends action
                        return "End action.";
                    }
                })
                .collect(Collectors.toList());
        int moduleIndex = sendRequest(subject, Deliverable.SHOOT_MODULE_REQUEST, nextNames);
        int moduleId = next.get(moduleIndex);
        controller.shoot(subject, pattern, moduleId);
    }

    public void acquireTargets(Player subject, AttackModule attackModule, List<Target> targets) {
        for(Target target : targets) {
            switch(target.getType()) {
                case PLAYER:
                    Player acquiredPlayer = shootPlayer(subject, (TargetPlayer) target);
                        ((TargetPlayer) target).setPlayer(acquiredPlayer);
                    break;

                case CELL:
                    Cell acquiredCell = shootCell(subject, (TargetCell) target);
                        ((TargetCell) target).setCell(acquiredCell);
                    break;

                case ROOM:
                    Room acquiredRoom = shootRoom(subject, (TargetRoom) target);
                        ((TargetRoom) target).setRoom(acquiredRoom);
                    break;

                default:
                    break;
            }
        }
        controller.shootTargets(subject, attackModule, targets);
    }

    private Player shootPlayer(Player subject, TargetPlayer target) {
        List<Player> players = target.filter();
        if(players.size() == 0) {
            sendMessage(subject, Deliverable.SHOOT_PLAYER_FAILURE);
            return null;
        }

        List<String> playerNames = players.stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        Deliverable.SHOOT_TARGET_GENERIC.message = target.getMessage();
        int playerIndex = sendRequest(subject, Deliverable.SHOOT_TARGET_GENERIC, playerNames);
        return players.get(playerIndex);
    }

    private Cell shootCell(Player subject, TargetCell target) {
        List<Cell> cells = target.filter();
        if(cells.size() == 0) {
            sendMessage(subject, Deliverable.SHOOT_CELL_FAILURE);
            return null;
        }

        List<String> cellNames = cells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());
        List<Integer> cellIds = cells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());
        Deliverable.SHOOT_TARGET_GENERIC.message = target.getMessage();
        int cellIndex = sendRequest(subject, Deliverable.SHOOT_TARGET_GENERIC, cellNames, cellIds);
        return cells.get(cellIndex);
    }

    private Room shootRoom(Player subject, TargetRoom target) {
        List<Room> rooms = target.filter();
        if (rooms.size() == 0) {
            sendMessage(subject, Deliverable.SHOOT_ROOM_FAILURE);
            return null;
        }

        List<String> roomNames = rooms.stream()
                .map(Room::toString)
                .collect(Collectors.toList());
        Deliverable.SHOOT_TARGET_GENERIC.message = target.getMessage();
        int roomIndex = sendRequest(subject, Deliverable.SHOOT_TARGET_GENERIC, roomNames);
        return rooms.get(roomIndex);
    }

    public void reload(Player subject) {
        List<Weapon> weapons = subject.getWeapons().stream()
                .filter(w -> !w.isLoaded())
                .collect(Collectors.toList());
        boolean keepReloading = true;

        while(weapons.size() > 0 && keepReloading) {
            keepReloading = sendRequest(subject, Deliverable.RELOAD_REQUEST_IF);

            if(keepReloading) {
                int reloadIndex = sendRequest(subject, Deliverable.RELOAD_REQUEST_WHICH, weapons);
                Weapon weaponToReload = weapons.get(reloadIndex);
                boolean success = controller.reload(subject, weaponToReload);
                if(success)
                    sendMessage(subject, Deliverable.RELOAD_SUCCESS);
                else
                    sendMessage(subject, Deliverable.RELOAD_FAILURE);
            }

            // re-evaluate situation before continuing
            weapons = subject.getWeapons().stream()
                    .filter(w -> !w.isLoaded())
                    .collect(Collectors.toList());
        }
    }

    public void usePowerUp(Player subject) {
        List<PowerUp> powerUps = subject.getPowerUps()
                .stream()
                .filter(p -> p.getType() != PowerUpType.GRENADE) // can't use grenade arbitrarily
                .filter(p -> p.getType() != PowerUpType.SCOPE) // can't use scope arbitrarily
                .collect(Collectors.toList());

        if(powerUps.size() == 0)
            return;

        boolean usePowerUp = sendRequest(subject, Deliverable.POWERUP_REQUEST_IF);
        if(!usePowerUp)
            return;

        int powerUpIndex = sendRequest(subject, Deliverable.POWERUP_REQUEST_WHICH, powerUps);
        PowerUp powerUp = powerUps.get(powerUpIndex);
        subject.discardPowerUp(powerUp);
        controller.usePowerUp(subject, powerUp);
    }

    public void scope(Damage damage, List<PowerUp> scopes, List<Player> targets) {
        Player subject = damage.getAuthor();
        List<Player> scopedPlayers = new ArrayList<>();
        boolean useScope = true;
        while(scopes.size() > 0 && useScope) {
            useScope = sendRequest(subject, Deliverable.SCOPE_REQUEST_IF);
            if(useScope) {
                int scopeId = sendRequest(subject, Deliverable.SCOPE_REQUEST_WHICH, scopes);
                PowerUp scope = scopes.get(scopeId);
                subject.discardPowerUp(scope);

                int targetId = sendRequest(subject, Deliverable.SCOPE_REQUEST_TARGET, targets);
                scopedPlayers.add(targets.get(targetId));
            }
        }
        controller.scope(damage, targets, scopedPlayers);
    }

    public void grenade(Player subject, List<PowerUp> grenades, Player originalAttacker) {
        boolean useGrenade = sendRequest(subject, Deliverable.GRENADE_REQUEST_IF);
        if(!useGrenade)
            return;

        int grenadeIndex = sendRequest(subject, Deliverable.GRENADE_REQUEST_WHICH, grenades);
        PowerUp grenade = grenades.get(grenadeIndex);
        subject.discardPowerUp(grenade);
        controller.grenade(subject, originalAttacker);
    }

    public void newton(Player subject) {
        List<Player> targetPlayers = game.getParticipants()
                .stream()
                .filter(p -> !p.equals(subject))
                .collect(Collectors.toList());
        List<String> playerNames = targetPlayers.stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        int targetPlayerId = sendRequest(subject, Deliverable.NEWTON_REQUEST_PLAYER, playerNames);
        Player targetPlayer = targetPlayers.get(targetPlayerId);

        List<Cell> targetCells = game.getBoard()
                .getCells()
                .stream()
                .filter(c -> subject.getPosition().canSee(c))
                .filter(c -> {
                    try {
                        return targetPlayer.getPosition().distance(c) <= Newton.NEWTON_MAX_DISTANCE;
                    } catch (DistanceFromNullException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if(targetCells.size() == 0) {
            sendMessage(subject, Deliverable.NEWTON_FAILURE);
            return;
        }

        List<Integer> cellIds = targetCells.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());

        int targetCellId = sendRequest(subject, Deliverable.NEWTON_REQUEST_CELL, targetCells, cellIds);
        Cell targetCell = targetCells.get(targetCellId);

        controller.newton(targetPlayer, targetCell);
    }

    public void teleport(Player subject) {
        List<Cell> options = game.getBoard().getCells();
        List<Integer> cellIds = options.stream()
                .map(Cell::getId)
                .collect(Collectors.toList());
        int cellIndex = sendRequest(subject, Deliverable.TELEPORT_REQUEST_CELL, options, cellIds);
        Cell destination = options.get(cellIndex);
        controller.teleport(subject, destination);
    }

    public void announceDamage(Player author, Player target, int amount) {
        
    }

    public void announceMarking(Player author, Player target, int amount) {

    }

    public void announceMove(Player author, Player target, Cell destination) {

    }
}
