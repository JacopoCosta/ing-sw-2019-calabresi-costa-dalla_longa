package it.polimi.ingsw.view.virtual;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.*;
import it.polimi.ingsw.model.player.Execution;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.Newton;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.powerups.PowerUpType;
import it.polimi.ingsw.model.util.Table;
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

import static it.polimi.ingsw.model.Game.*;

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
     * @throws AbortedTurnException When the request routine catches a {@code ConnectionException}
     * and the current turn needs to be ended prematurely.
     * @return the response to the deliverable.
     */
    private int send(Player recipient, Deliverable deliverable) throws AbortedTurnException {
        if(offlineMode) {
            String stampedMessage = "\n<" + recipient.getName() + "> " + deliverable.getMessage();
            String stampedResponse = "\nauto >>> ";
            boolean enableDispatch = !silent;
            boolean enableQuery = enableDispatch && !autoPilot;
            switch (deliverable.getType()) {
                case INFO:
                    if(enableDispatch)
                        Dispatcher.sendMessage(stampedMessage);
                    return 0;

                case DUAL:
                    if(enableQuery)
                        return Dispatcher.requestBoolean(stampedMessage) ? 1 : 0;
                    else {
                        int response = (int) Math.round(Math.random()); // either 0 or 1
                        if(enableDispatch) {
                            Dispatcher.sendMessage(stampedMessage);
                            Dispatcher.sendMessage(stampedResponse + (response == 1 ? "y" : "n"));
                        }
                        return response;
                    }

                case MAPPED:
                    List<String> options = ((Mapped) deliverable).getOptions();
                    List<Integer> keys = ((Mapped) deliverable).getKeys();
                    if(enableQuery)
                        return Dispatcher.requestMappedOption(stampedMessage, options, keys);
                    else {
                        int response = (int) (Math.random() * keys.size()); // a random index
                        if(enableDispatch) {
                            Dispatcher.sendMessage(stampedMessage);
                            Dispatcher.sendMessage(stampedResponse + response);
                        }
                        return response;
                    }

                case BULK:
                    if(enableDispatch)
                        Dispatcher.sendMessage(game.toString());
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
            if(deliverable.getType() != DeliverableType.INFO) {
                try {
                    return ((Response) recipient.nextDeliverable()).getNumber();
                } catch (ConnectionException e) {
                    throw new AbortedTurnException("");
                }
            }
            return 0;
        }
    }

    private void broadcast(Deliverable deliverable) {
        if(!(deliverable.getType().equals(DeliverableType.INFO) || deliverable.getType().equals(DeliverableType.BULK)))
            throw new DeliverableException("Wrong call to send in VirtualView.");

        if(offlineMode) {
            if(!silent)
                Dispatcher.sendMessage("\n<#ALL> " + deliverable.getMessage());
        }
        else {
            game.getParticipants().forEach(recipient -> {
                try {
                    send(recipient, deliverable);
                } catch (AbortedTurnException e) {
                    controller.disconnect(recipient);
                }
            });
        }
    }

    /*
    BULK sender: sends initial info about the game itself: list of participants, board morphology and game settings.
    If the game comes from a previous saved game (isNewGame == true), it also sends all the information about players and board status.
    */
    private void sendStatusInit(Board board, boolean isNewGame) {
        List<Object> content = new ArrayList<>();
        //adds general board structure info
        content.add(game.getBoard().getWidth());
        content.add(game.getBoard().getHeight());
        //NOTE: in every standard configuration, width == 4 and height == 3, however this increases code robustness

        //adds board morphology (i.e. cell positions and walls, but no info about their content, which will be sent via UPDATE_CELL)
        //NOTE: this is useful for CLI only
        content.add(board.getMorphology());

        //adds participants names
        content.add(game.getParticipants()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));

        /*

        This is probably redundant.
        TODO: check its redundancy (useless in case participants ID are used on view-level only, as participants are already
              sent using a sorted list of participants)

        //adds participants ID
        content.add(game.getParticipants()
                .stream()
                .map(Player::getPosition)
                .map(Cell::getId)
                .collect(Collectors.toList()));

         */

        Deliverable deliverable = new Bulk(DeliverableEvent.BOARD_INIT, content);
        broadcast(deliverable);

        if(!isNewGame) {    //the game comes from a saved game, so all the info about the players shall be broadcast as well

            for(Player player: game.getParticipants()) {
                sendUpdateScore(player);
                sendUpdateDamage(player);
                sendUpdateMarking(player);
                sendUpdateMove(player);
                sendUpdatePlayerDeathCount(player);
                sendUpdateInventory(player);
            }

            sendUpdateBoardKill();
            sendUpdateDoubleKill();
        }
    }

    //BULK: sends an updated, new value for a cell content
    private void sendUpdateCell(Cell cell) {
        List <Object> content = new ArrayList<>();

        content.add(cell.getId());
        content.add(cell.isSpawnPoint() ? "0" : "1");   //boolean cast to String

        if(cell.isSpawnPoint()) {
            //adds spawnpoint color
            content.add(((SpawnCell) cell).getAmmoCubeColor().toStringAsColor());

            //adds weapons list (the encoding is similar to sendUpdateInventory)
            content.add(((SpawnCell) cell).getWeaponShop()
                    .stream()
                    .map(w -> {
                        List<String> weapon = new ArrayList<>();
                        weapon.add(w.getName());
                        weapon.add(w.getPurchaseCost().toString());
                        weapon.add(w.getReloadCost().toString());
                        return weapon;
                    })
                    .collect(Collectors.toList()));
        } //end if(isSpawnpoint)
        else {
            //adds its ammo
            content.add(((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed());
            content.add(((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow());
            content.add(((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow());
            content.add(((AmmoCell) cell).getAmmoTile().includesPowerUp() ? "0" : "1"); //boolean cast to String
        }

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_CELL, content);
        broadcast(deliverable);
    }

    //BULK: sends an updated, new value for a player's damage
    private void sendUpdateDamage(Player player) {

        List<Object> content = new ArrayList<>();

        content.add(player.getId());  //indicates the player whose damagelist has to be updated

        content.add(player.getDamageAsList()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_DAMAGE, content);
        broadcast(deliverable);
    }

    //BULK: sends an updated, new value for a player's marking list
    private void sendUpdateMarking(Player player) {

        List<Object> content = new ArrayList<>();

            content.add(player.getMarkingsAsList()
                    .stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_MARKING, content);
        broadcast(deliverable);
    }

    //BULK: sends an updated value for a player's position (sent as index of its cell)
    private void sendUpdateMove(Player player) {


        List<Integer> content = new ArrayList<>();

        content.add(player.getId());
        content.add(player.getPosition().getId());

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_MOVE, content);
        broadcast(deliverable);
    }

    //BULK: sends the updated score of a player
    private void sendUpdateScore(Player player) {

        List<Integer> content = new ArrayList<>();

        content.add(player.getId());
        content.add(player.getScore());

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_SCORE, content);
        broadcast(deliverable);
    }

    private void sendUpdateInventory(Player player) {
        List<Object> content = new ArrayList<>();

        content.add(player.getId());

        //adds player's ammo
        List<Integer> ammo = new ArrayList<>();
        ammo.add(player.getAmmoCubes().getRed());
        ammo.add(player.getAmmoCubes().getYellow());
        ammo.add(player.getAmmoCubes().getBlue());

        content.add(ammo);

        //adds player's powerups (both type and color)
        content.add(player.getPowerUps()
               .stream()
               .map(up -> {
                   List<String> upHand = new ArrayList<>();
                   upHand.add(up.getType().toString());             //e.g. "GRENADE"
                   upHand.add(up.getAmmoCubes().toStringAsColor()); //e.g. "red"
                   return upHand;
               })
               .collect(Collectors.toList()));

        //adds player's weapons
        content.add(player.getWeapons()
                .stream()
                .map(w -> {
                    List<String> weapon = new ArrayList<>();
                    weapon.add(w.getName());
                    weapon.add(w.getPurchaseCost().toString());
                    weapon.add(w.getReloadCost().toString());
                    weapon.add(w.isLoaded() ? "0" : "1");
                    return weapon;
                })
                .collect(Collectors.toList()));

        //sends the bulk deliverable
        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_INVENTORY, content);
        broadcast(deliverable);
        /*
        So, there will be an ArrayList deliverable of 4 elements:
            deliverable [0] contains the player's Id;
            deliverable [1] contains an ArrayList containing the ammo they own, codified as Integer;
            deliverable [2] contains an ArrayList containing the name and the color of each powerUp they own, codified as String;
            deliverable [3] contains an ArrayList containing the name, the cost, the reloadCost and the status (reloaded or not) of each Weapon they own, codified as String.
         */
    }

    private void sendUpdatePlayerDeathCount(Player player) {

        List<Integer> content = new ArrayList<>();

        content.add(player.getId());
        content.add(player.getDeathCount());

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_DEATHCOUNT, content);
        broadcast(deliverable);
    }

    //BULK: sends info about global killers
    private void sendUpdateBoardKill() {

        List<Object> content = new ArrayList<>();

        content.addAll(game.getBoard().getKillers()
                .stream()
                .map(p -> { //that's because some values are set to null
                    if(p != null)
                        return p.getName();
                    else
                        return null;
                })
                .collect(Collectors.toList()));

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_BOARDKILL, content);
        broadcast(deliverable);
    }

    //BULK: sends info about global doublekillers
    private void sendUpdateDoubleKill() {

         List<Object> content = new ArrayList<>();

        content.addAll(game.getBoard().getDoubleKillers()
                .stream()
                .map(Player::getName)
                .collect(Collectors.toList()));

        Deliverable deliverable = new Bulk(DeliverableEvent.UPDATE_BOARDDOUBLEKILL, content);
        broadcast(deliverable);
    }

    private void sendStatusUpdate(Player subject) throws AbortedTurnException {
        if(offlineMode) {
            Deliverable deliverable = new Bulk(DeliverableEvent.STATUS_UPDATE, null);
            deliverable.overwriteMessage(game.toString());
            send(subject, deliverable);
        }
    }

    public void spawn(Player subject) throws AbortedTurnException {
        sendStatusUpdate(subject);

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
        sendStatusUpdate(subject);

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
        sendStatusUpdate(subject);

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
                .filter(c -> {
                    try {
                        return subject.getPosition().canSee(c);
                    } catch (NullCellOperationException e) {
                        return false;
                    }
                })
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

    public void announceTurn(Player subject) {
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_TURN);
        deliverable.overwriteMessage("It is now " + subject.getName() + "'s turn.");
        broadcast(deliverable);
    }

    public void announceDamage(Player author, Player target, int amount) {
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_DAMAGE);
        deliverable.overwriteMessage(author.getName() + " dealt " + amount + " damage to " + target.getName() + ".");
        broadcast(deliverable);
    }

    public void announceMarking(Player author, Player target, int amount) {
        String lexeme = amount > 1 ? "marks" : "mark";
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_MARKING);
        deliverable.overwriteMessage(author.getName() + " dealt " + amount + " " + lexeme + " to " + target.getName() + ".");
        broadcast(deliverable);
    }

    public void announceMove(Player author, Player target, Cell destination) {
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_MOVE);
        String message = author.getName() + " moved";
        if(!target.equals(author))
            message += " " + target.getName();
        deliverable.overwriteMessage(message + " to " + destination.toString() + ".");
        broadcast(deliverable);
    }

    public void announceKill(Player author, Player target) {
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_BOARDKILL);
        String message = author.getName() +  " ";
        if(target.isOverKilled())
            message += "over";
        message += "killed " + target.getName() + "!";
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }

    public void announceScore(Player source, Player creditor, int amount, boolean firstBloodOrDoubleKill) {
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_SCORE);
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
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_FRENZY);
        deliverable.overwriteMessage(cause.toString() + " activated the Final Frenzy!");
        broadcast(deliverable);
    }

    public void announceWinner(List<Player> ranking) {
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_WINNER);
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
        Deliverable deliverable = new Info(DeliverableEvent.UPDATE_DISCONNECT);
        String message = disconnectedPlayer + " has lost connection. Skipping to the next turn...";
        deliverable.overwriteMessage(message);
        broadcast(deliverable);
    }
}
