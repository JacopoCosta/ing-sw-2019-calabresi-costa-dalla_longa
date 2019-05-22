package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Execution;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;
import it.polimi.ingsw.model.weaponry.targets.TargetRoom;
import it.polimi.ingsw.view.remote.Dispatcher;

import java.util.List;
import java.util.stream.Collectors;


// Important note: this class often invokes the Dispatcher in its methods.
// This will no longer be in the final version of the game, as the Dispatcher, unlike this class,
// will be running on the client side, making it unreachable without a network protocol connecting the two.
// For this reason, all of the calls to the Dispatcher will have to be replaced with equivalent network protocol messages.
// In addition to this, the "Deliverable" interface will be used by the client to determine what to display (and how).
public class VirtualView implements Deliverable {

    private Game game;
    private Controller controller;

    public VirtualView(Game game) {
        this.game = game;
        this.controller = game.getController();
    }

    private void sendMessage(Player recipient, Deliverable message) {

    }

    private int sendRequest(Player recipient, Deliverable request) {
        return 0;
    }

    private void broadcast(Deliverable message) {
        game.getParticipants().forEach(p -> sendMessage(p, message));
    }

    public void spawn(Player subject, List<PowerUp> options) {
        int keepIndex = Dispatcher.requestIndex(SPAWN_REQUEST, options);
        PowerUp powerUpToKeep = options.get(keepIndex);
        PowerUp powerUpToRespawn = options.get(1 - keepIndex);
        controller.spawn(subject, powerUpToKeep, powerUpToRespawn);
        Dispatcher.sendMessage(SPAWN_SUCCESS);
    }

    public void discardPowerUp(Player subject) {
        List<PowerUp> powerUps = subject.getPowerUps();
        int discardIndex = Dispatcher.requestIndex(DISCARD_POWER_UP_REQUEST, powerUps);
        PowerUp toDiscard = powerUps.get(discardIndex);
        controller.discardPowerUp(subject, toDiscard);
    }

    public void discardWeapon(Player subject) {
        List<Weapon> weapons = subject.getWeapons();
        int discardIndex = Dispatcher.requestIndex(DISCARD_WEAPON_REQUEST, weapons);
        Weapon toDiscard = weapons.get(discardIndex);
        controller.discardWeapon(subject, toDiscard);
    }

    public Execution chooseExecution(Player subject, List<Execution> options) {
        int choiceIndex = Dispatcher.requestIndex(CHOOSE_EXECUTION_REQUEST, options);
        return options.get(choiceIndex);
    }

    public void move(Player subject, List<Cell> options) {
        int destinationIndex = Dispatcher.requestIndex(MOVE_REQUEST, options);
        Cell destination = options.get(destinationIndex);
        controller.move(subject, destination);
    }

    public void grabAmmo(Player subject) {
        boolean success = controller.grabAmmo(subject);
        if(success)
            Dispatcher.sendMessage(GRAB_AMMO_SUCCESS);
        else
            Dispatcher.sendMessage(GRAB_AMMO_FAILURE);
    }

    public void grabWeapon(Player subject) {
        boolean doPurchase = Dispatcher.requestBoolean(GRAB_WEAPON_REQUEST_IF);
        if(!doPurchase)
            return;
        SpawnCell cell = (SpawnCell) subject.getPosition();
        int weaponIndex = Dispatcher.requestIndex(GRAB_WEAPON_REQUEST_WHICH, cell.getWeaponShop());
        boolean success = controller.grabWeapon(subject, weaponIndex);
        if(success)
            Dispatcher.sendMessage(GRAB_WEAPON_SUCCESS);
        else
            Dispatcher.sendMessage(GRAB_WEAPON_FAILURE);
    }

    public void shoot(Player subject) {
        List<Weapon> availableWeapons = subject.getWeapons()
                .stream()
                .filter(Weapon::isLoaded)
                .collect(Collectors.toList()); // gather all of the player's loaded weapons
                // at the time this method is called and entered, it is assumed that the player is actually able to shoot with at least one weapon

        int weaponIndex = Dispatcher.requestIndex(SHOOT_WEAPON_REQUEST, availableWeapons);
        Weapon weapon = availableWeapons.get(weaponIndex); // choose a weapon

        AttackPattern pattern = weapon.getPattern();
        controller.shoot(subject, pattern);
        weapon.unload();
    }

    public int shootAttackModule(AttackPattern pattern) {
        List<Integer> first = pattern.getFirst();
        List<String> firstNames = first.stream()
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
                    } catch (Exception e) { // -1 ends action
                        return "End action.";
                    }
                })
                .collect(Collectors.toList());
        int moduleIndex = Dispatcher.requestIndex(SHOOT_MODULE_REQUEST, firstNames);
        return first.get(moduleIndex);
    }

    public Player shootPlayer(TargetPlayer target) {
        List<Player> players = target.filter();
        if(players.size() == 0) {
            Dispatcher.sendMessage(SHOOT_PLAYER_FAILURE);
            return null;
        }

        List<String> playerNames = players.stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        int playerIndex = Dispatcher.requestIndex(target.getMessage(), playerNames);
        return players.get(playerIndex);
    }

    public Cell shootCell(TargetCell target) {
        List<Cell> cells = target.filter();
        if(cells.size() == 0) {
            Dispatcher.sendMessage(SHOOT_CELL_FAILURE);
            return null;
        }

        List<String> cellNames = cells.stream()
                .map(Cell::toString)
                .collect(Collectors.toList());
        int cellIndex = Dispatcher.requestIndex(target.getMessage(), cellNames);
        return cells.get(cellIndex);
    }

    public Room shootRoom(TargetRoom target) {
        List<Room> rooms = target.filter();
        if(rooms.size() == 0) {
            Dispatcher.sendMessage(SHOOT_ROOM_FAILURE);
            return null;
        }

        List<String> roomNames = rooms.stream()
                .map(Room::toString)
                .collect(Collectors.toList());
        int roomIndex = Dispatcher.requestIndex(target.getMessage(), roomNames);
        return rooms.get(roomIndex);
    }

    public void reload(Player subject) {
        List<Weapon> weapons = subject.getWeapons().stream()
                .filter(w -> !w.isLoaded())
                .collect(Collectors.toList());
        int weaponCount = weapons.size();

        boolean keepReloading = true;

        while(weaponCount > 0 && keepReloading) {
            keepReloading = Dispatcher.requestBoolean(RELOAD_REQUEST_IF);

            if(keepReloading) {
                int reloadIndex = Dispatcher.requestIndex(RELOAD_REQUEST_WHICH, weapons);
                Weapon weaponToReload = weapons.get(reloadIndex);
                boolean success = controller.reload(subject, weaponToReload);
                if(success)
                    Dispatcher.sendMessage(RELOAD_SUCCESS);
                else
                    Dispatcher.sendMessage(RELOAD_FAILURE);
            }

            // re-evaluate situation before continuing
            weapons = subject.getWeapons().stream()
                    .filter(w -> !w.isLoaded())
                    .collect(Collectors.toList());
            weaponCount = weapons.size();
        }
    }

}
