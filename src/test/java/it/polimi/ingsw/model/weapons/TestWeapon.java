package it.polimi.ingsw.model.weapons;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.Room;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.exceptions.EmptyDeckException;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.AttackModule;
import it.polimi.ingsw.model.weaponry.AttackPattern;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.model.weaponry.targets.Target;
import it.polimi.ingsw.model.weaponry.targets.TargetCell;
import it.polimi.ingsw.model.weaponry.targets.TargetPlayer;
import it.polimi.ingsw.model.weaponry.targets.TargetRoom;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestWeapon {

    private Board board;
    private Controller controller;

    private Player author = new Player("Aldo");
    private Player targetSameCell = new Player("Giovanni sulla stessa cella");
    private Player targetAdjacent = new Player("Giovanni adiacente");
    private Player targetSameRoom = new Player("Giovanni nella stessa stanza");
    private Player targetOtherRoom = new Player("Giovanni nell'altra stanza");
    private Player targetAlignedNotVisible = new Player("Giovanni allineato ma non visibile");
    private Player targetFarAway = new Player("Giovanni molto lontano");
    private Player targetNullPosition = new Player("Giacomo");

    private Cell authorStartingPosition;
    private Cell targetSameCellStartingPosition;
    private Cell targetAdjacentStartingPosition;
    private Cell targetSameRoomStartingPosition;
    private Cell targetOtherRoomStartingPosition;
    private Cell targetAlignedNotVisibleStartingPosition;
    private Cell targetFarAwayStartingPosition;
    private Cell targetNullPositionStartingPosition;

    @Before
    public void setUp() {
        List<Player> participants = new ArrayList<>();
        participants.add(author);
        participants.add(targetSameCell);
        participants.add(targetAdjacent);
        participants.add(targetSameRoom);
        participants.add(targetOtherRoom);
        participants.add(targetAlignedNotVisible);
        participants.add(targetFarAway);
        participants.add(targetNullPosition);

        Game game = Game.create(true, 5, 2, participants);
        board = game.getBoard();
        controller = game.getVirtualView().getController();

        int[] positions = {7, 7, 6, 9, 3, 4, 0};
        for(int i = 0; i < positions.length; i ++)
            participants.get(i).setPosition(board.getCells().get(positions[i]));

        authorStartingPosition = author.getPosition();
        targetSameCellStartingPosition = targetSameCell.getPosition();
        targetAdjacentStartingPosition = targetAdjacent.getPosition();
        targetSameRoomStartingPosition = targetSameRoom.getPosition();
        targetOtherRoomStartingPosition = targetOtherRoom.getPosition();
        targetAlignedNotVisibleStartingPosition = targetAlignedNotVisible.getPosition();
        targetFarAwayStartingPosition = targetFarAway.getPosition();
        targetNullPositionStartingPosition = targetNullPosition.getPosition();
    }

    @Test
    public void lockRifle() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Lock Rifle"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameCell);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(1, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetAdjacent);
        expected1.add(targetSameRoom);
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetAdjacent);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(1, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(1, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void machineGun() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Machine Gun"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameCell);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetAdjacent);
        expected1.add(targetSameRoom);
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetAdjacent);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();
        
        assertEquals(0, targets2.size());
        
        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module3 = attackPattern.getModule(3);

        List<Target> targets3 = module3.getTargets();

        TargetPlayer target30 = (TargetPlayer) targets3.get(0);
        List<Player> actual3 = target30.filter();
        List<Player> expected3 = new ArrayList<>();
        expected3.add(targetAdjacent);
        expected3.add(targetSameRoom);
        expected3.add(targetOtherRoom);
        assertTrue(actual3.containsAll(expected3));
        assertTrue(expected3.containsAll(actual3));
        target30.setPlayer(targetSameRoom);

        module3.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(1, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void thor() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("T.H.O.R."));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetOtherRoom);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetSameCell);
        expected1.add(targetAdjacent);
        expected1.add(targetSameRoom);
        expected1.add(targetFarAway);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetFarAway);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(1, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetPlayer target20 = (TargetPlayer) targets2.get(0);
        List<Player> actual2 = target20.filter();
        List<Player> expected2 = new ArrayList<>();
        expected2.add(targetAlignedNotVisible);
        assertTrue(actual2.containsAll(expected2));
        assertTrue(expected2.containsAll(actual2));
        target20.setPlayer(targetAlignedNotVisible);

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(2, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(1, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void plasmaGun() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Plasma Gun"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetOtherRoom);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetCell target10 = (TargetCell) targets1.get(0);
        List<Cell> actual1 = target10.filter();
        List<Cell> expected1 = new ArrayList<>();
        expected1.add(board.getCells().get(2));
        expected1.add(board.getCells().get(3));
        expected1.add(board.getCells().get(6));
        expected1.add(board.getCells().get(7));
        expected1.add(board.getCells().get(9));
        expected1.add(board.getCells().get(10));
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setCell(board.getCells().get(10));

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(board.getCells().get(10), author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        assertEquals(0, targets2.size());

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(3, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(board.getCells().get(10), author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void whisper() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Whisper"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameRoom);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(3, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(1, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void electroscythe() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Electroscythe"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        assertEquals(0, targets0.size());

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        assertEquals(0, targets1.size());

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(3, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void tractorBeam() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Tractor Beam"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        expected0.add(targetAlignedNotVisible);
        expected0.add(targetFarAway);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameCell);

        TargetCell target01 = (TargetCell) targets0.get(1);
        List<Cell> actual01 = target01.filter();
        List<Cell> expected01 = new ArrayList<>();
        expected01.add(board.getCells().get(7));
        expected01.add(board.getCells().get(6));
        expected01.add(board.getCells().get(9));
        expected01.add(board.getCells().get(10));
        expected01.add(board.getCells().get(3));
        assertTrue(actual01.containsAll(expected01));
        assertTrue(expected01.containsAll(actual01));
        target01.setCell(board.getCells().get(7));

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetSameCell);
        expected1.add(targetAdjacent);
        expected1.add(targetSameRoom);
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetSameCell);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(4, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void vortexCannon() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Vortex Cannon"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetCell target00 = (TargetCell) targets0.get(0);
        List<Cell> actual0 = target00.filter();
        List<Cell> expected0 = new ArrayList<>();
        expected0.add(board.getCells().get(3));
        expected0.add(board.getCells().get(6));
        expected0.add(board.getCells().get(9));
        expected0.add(board.getCells().get(10));
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setCell(board.getCells().get(6));

        TargetPlayer target01 = (TargetPlayer) targets0.get(1);
        List<Player> actual01 = target01.filter();
        List<Player> expected01 = new ArrayList<>();
        expected01.add(targetAdjacent);
        expected01.add(targetSameCell);
        expected01.add(targetSameRoom);
        assertTrue(actual01.containsAll(expected01));
        assertTrue(expected01.containsAll(actual01));
        target01.setPlayer(targetAdjacent);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(2, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetSameCell);
        expected1.add(targetSameRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetSameCell);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(2, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());


        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetPlayer target20 = (TargetPlayer) targets2.get(0);
        List<Player> actual2 = target20.filter();
        List<Player> expected2 = new ArrayList<>();
        expected2.add(targetSameRoom);
        assertTrue(actual2.containsAll(expected2));
        assertTrue(expected2.containsAll(actual2));
        target20.setPlayer(targetSameRoom);

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(2, targetAdjacent.getDamageByAuthor(author));
        assertEquals(1, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void furnace() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Furnace"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetRoom target00 = (TargetRoom) targets0.get(0);
        List<Room> actual0 = target00.filter();
        List<Room> expected0 = new ArrayList<>();
        expected0.add(targetOtherRoomStartingPosition.getRoom());
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setRoom(targetOtherRoomStartingPosition.getRoom());

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetCell target10 = (TargetCell) targets1.get(0);
        List<Cell> actual1 = target10.filter();
        List<Cell> expected1 = new ArrayList<>();
        expected1.add(board.getCells().get(3));
        expected1.add(board.getCells().get(6));
        expected1.add(board.getCells().get(10));
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setCell(board.getCells().get(6));

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(1, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void heatseeker() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Heatseeker"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetAlignedNotVisible);
        expected0.add(targetFarAway);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetFarAway);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(3, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void hellion() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Hellion"));

        // for a more thorough testing -- it's still adjacent so the name is technically not wrong
        targetAdjacent.setPosition(targetOtherRoomStartingPosition);

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetOtherRoom);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(1, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(1, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetAdjacent);
        expected1.add(targetSameRoom);
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetAdjacent);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        // take into account marks from the previous round
        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(2, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(2, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(3, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void flamethrower() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Flamethrower"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetAdjacent);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetAdjacent);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        assertEquals(0, actual1.size());

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        // for a more thorough testing
        author.setPosition(board.getCells().get(10));

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetCell target20 = (TargetCell) targets2.get(0);
        List<Cell> actual2 = target20.filter();
        List<Cell> expected2 = new ArrayList<>();
        expected2.add(targetSameCellStartingPosition);
        expected2.add(targetSameRoomStartingPosition);
        assertTrue(actual2.containsAll(expected2));
        assertTrue(expected2.containsAll(actual2));
        target20.setCell(targetSameCellStartingPosition);

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(board.getCells().get(10), author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module3 = attackPattern.getModule(3);

        List<Target> targets3 = module3.getTargets();

        TargetCell target30 = (TargetCell) targets3.get(0);
        List<Cell> actual3 = target30.filter();
        List<Cell> expected3 = new ArrayList<>();
        expected3.add(targetOtherRoomStartingPosition);
        assertTrue(actual3.containsAll(expected3));
        assertTrue(expected3.containsAll(actual3));
        target30.setCell(targetOtherRoomStartingPosition);

        module3.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(board.getCells().get(10), author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void grenadeLauncher() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Grenade Launcher"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetOtherRoom);

        TargetCell target01 = (TargetCell) targets0.get(1);
        List<Cell> actual01 = target01.filter();
        List<Cell> expected01 = new ArrayList<>();
        expected01.add(board.getCells().get(2));
        expected01.add(board.getCells().get(3));
        expected01.add(board.getCells().get(7));
        assertTrue(actual01.containsAll(expected01));
        assertTrue(expected01.containsAll(actual01));
        target01.setCell(board.getCells().get(7));

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetSameCellStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetCell target10 = (TargetCell) targets1.get(0);
        List<Cell> actual1 = target10.filter();
        List<Cell> expected1 = new ArrayList<>();
        expected1.add(board.getCells().get(3));
        expected1.add(board.getCells().get(6));
        expected1.add(board.getCells().get(7));
        expected1.add(board.getCells().get(9));
        expected1.add(board.getCells().get(10));
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setCell(board.getCells().get(7));

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetSameCellStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void rocketLauncher() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Rocket Launcher"));

        // for a more thorough testing -- this will be important later
        targetSameRoom.setPosition(targetFarAwayStartingPosition);
        
        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetAdjacent);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetOtherRoom);

        TargetCell target01 = (TargetCell) targets0.get(1);
        List<Cell> actual01 = target01.filter();
        List<Cell> expected01 = new ArrayList<>();
        expected01.add(board.getCells().get(2));
        expected01.add(board.getCells().get(3));
        expected01.add(board.getCells().get(7));
        assertTrue(actual01.containsAll(expected01));
        assertTrue(expected01.containsAll(actual01));
        target01.setCell(board.getCells().get(7));

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetSameRoom.getPosition());
        assertEquals(authorStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetCell target10 = (TargetCell) targets1.get(0);
        List<Cell> actual1 = target10.filter();
        List<Cell> expected1 = new ArrayList<>();
        expected1.add(board.getCells().get(2));
        expected1.add(board.getCells().get(3));
        expected1.add(board.getCells().get(6));
        expected1.add(board.getCells().get(7));
        expected1.add(board.getCells().get(9));
        expected1.add(board.getCells().get(10));
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setCell(board.getCells().get(2));

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(board.getCells().get(2), author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetSameRoom.getPosition());
        assertEquals(authorStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetPlayer target20 = (TargetPlayer) targets2.get(0);
        List<Player> actual2 = target20.filter();
        List<Player> expected2 = new ArrayList<>();
        expected2.add(targetSameCell);
        expected2.add(targetAdjacent);
        expected2.add(targetSameRoom);
        expected2.add(targetOtherRoom);
        expected2.add(targetFarAway);
        assertTrue(actual2.containsAll(expected2));
        assertTrue(expected2.containsAll(actual2));
        target20.setPlayer(targetFarAway);

        TargetCell target21 = (TargetCell) targets2.get(1);
        List<Cell> actual21 = target21.filter();
        List<Cell> expected21 = new ArrayList<>();
        expected21.add(board.getCells().get(0));
        expected21.add(board.getCells().get(1));
        expected21.add(board.getCells().get(4));
        assertTrue(actual21.containsAll(expected21));
        assertTrue(expected21.containsAll(actual21));
        target21.setCell(board.getCells().get(4));
        
        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        // there's still a base 2 damage on targetOtherRoom due to the first module
        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(1, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(3, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(board.getCells().get(2), author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetSameRoom.getPosition());
        assertEquals(authorStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void railgun() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Railgun"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetOtherRoom);
        expected0.add(targetAlignedNotVisible);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetAdjacent);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(3, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        // for a more thorough testing
        
        targetSameRoom.setPosition(targetAdjacentStartingPosition);
        
        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetSameCell);
        expected1.add(targetAdjacent);
        expected1.add(targetSameRoom);
        expected1.add(targetOtherRoom);
        expected1.add(targetAlignedNotVisible);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetAdjacent);

        TargetPlayer target11 = (TargetPlayer) targets1.get(1);
        List<Player> actual11 = target11.filter();
        List<Player> expected11 = new ArrayList<>();
        expected11.add(targetSameRoom);
        expected11.add(targetAlignedNotVisible);
        assertTrue(actual11.containsAll(expected11));
        assertTrue(expected11.containsAll(actual11));
        target11.setPlayer(targetAlignedNotVisible);
        
        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(5, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(2, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void cyberblade() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Cyberblade"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameCell);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetCell target10 = (TargetCell) targets1.get(0);
        List<Cell> actual1 = target10.filter();
        List<Cell> expected1 = new ArrayList<>();
        expected1.add(board.getCells().get(3));
        expected1.add(board.getCells().get(6));
        expected1.add(board.getCells().get(7));
        expected1.add(board.getCells().get(10));
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setCell(board.getCells().get(7));

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetPlayer target20 = (TargetPlayer) targets2.get(0);
        List<Player> actual2 = target20.filter();
        assertEquals(0, actual2.size());
        // cheating a bit to test this case too
        target20.setPlayer(targetSameCell);

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(4, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void zx2() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("ZX-2"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        expected0.add(targetAdjacent);
        expected0.add(targetSameRoom);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameCell);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(2, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetSameCell);
        expected1.add(targetAdjacent);
        expected1.add(targetSameRoom);
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetSameCell);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(3, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetPlayer target20 = (TargetPlayer) targets2.get(0);
        List<Player> actual2 = target20.filter();
        List<Player> expected2 = new ArrayList<>();
        expected2.add(targetAdjacent);
        expected2.add(targetSameRoom);
        expected2.add(targetOtherRoom);
        assertTrue(actual2.containsAll(expected2));
        assertTrue(expected2.containsAll(actual2));
        target20.setPlayer(targetAdjacent);

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(3, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(1, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module3 = attackPattern.getModule(3);

        List<Target> targets3 = module3.getTargets();

        TargetPlayer target30 = (TargetPlayer) targets3.get(0);
        List<Player> actual3 = target30.filter();
        List<Player> expected3 = new ArrayList<>();
        expected3.add(targetSameRoom);
        expected3.add(targetOtherRoom);
        assertTrue(actual3.containsAll(expected3));
        assertTrue(expected3.containsAll(actual3));
        target30.setPlayer(targetSameRoom);

        module3.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(1, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(3, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(1, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(1, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void shotgun() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Shotgun"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameCell);

        TargetCell target01 = (TargetCell) targets0.get(1);
        List<Cell> actual01 = target01.filter();
        List<Cell> expected01 = new ArrayList<>();
        expected01.add(board.getCells().get(3));
        expected01.add(board.getCells().get(6));
        expected01.add(board.getCells().get(7));
        expected01.add(board.getCells().get(10));
        assertTrue(actual01.containsAll(expected01));
        assertTrue(expected01.containsAll(actual01));
        target01.setCell(board.getCells().get(6));

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(3, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetSameCell);
        expected1.add(targetAdjacent);
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetSameCell);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(5, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void powerGlove() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Power Glove"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetAdjacent);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetAdjacent);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(2, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(targetAdjacentStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        // cheating a bit to test a wider array of cases
        author.setPosition(board.getCells().get(2));
        author.savePosition(); // this is important because this weapon uses position history

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetAdjacent);
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetAdjacent);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        // taking into account marks from the previous round
        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(5, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(targetAdjacentStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetPlayer target20 = (TargetPlayer) targets2.get(0);
        List<Player> actual2 = target20.filter();
        List<Player> expected2 = new ArrayList<>();
        expected2.add(targetSameRoom);
        assertTrue(actual2.containsAll(expected2));
        assertTrue(expected2.containsAll(actual2));
        target20.setPlayer(targetSameRoom);

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        // taking into account marks from the previous round
        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(5, targetAdjacent.getDamageByAuthor(author));
        assertEquals(2, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(targetSameRoomStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void shockwave() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Shockwave"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetAdjacent);
        expected0.add(targetOtherRoom);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetAdjacent);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetOtherRoom);

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module2 = attackPattern.getModule(2);

        List<Target> targets2 = module2.getTargets();

        TargetPlayer target20 = (TargetPlayer) targets2.get(0);
        List<Player> actual2 = target20.filter();
        assertEquals(0, actual2.size());
        // cheating a bit to test more stuff
        target20.setPlayer(targetFarAway);

        module2.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(1, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(1, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(1, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        AttackModule module3 = attackPattern.getModule(3);

        List<Target> targets3 = module3.getTargets();

        assertEquals(0, targets3.size());

        targetFarAway.setPosition(targetAdjacentStartingPosition);
        targetAlignedNotVisible.setPosition(targetAdjacentStartingPosition);

        module3.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(0, targetSameCell.getDamageByAuthor(author));
        assertEquals(2, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(2, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(1, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(2, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }

    @Test
    public void sledgehammer() {
        Weapon weapon = null;
        do try {
            weapon = board.getWeaponDeck().draw();
        } catch (EmptyDeckException e) {
            fail();
        } while(!weapon.getName().equals("Sledgehammer"));

        AttackPattern attackPattern = weapon.getPattern();
        controller.prepareForShoot(author, attackPattern);
        AttackModule module0 = attackPattern.getModule(0);

        List<Target> targets0 = module0.getTargets();

        TargetPlayer target00 = (TargetPlayer) targets0.get(0);
        List<Player> actual0 = target00.filter();
        List<Player> expected0 = new ArrayList<>();
        expected0.add(targetSameCell);
        assertTrue(actual0.containsAll(expected0));
        assertTrue(expected0.containsAll(actual0));
        target00.setPlayer(targetSameCell);

        module0.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(0, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(authorStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(targetOtherRoomStartingPosition, targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());

        // let's test the alignment constraint
        author.setPosition(targetOtherRoomStartingPosition);

        AttackModule module1 = attackPattern.getModule(1);

        List<Target> targets1 = module1.getTargets();

        TargetPlayer target10 = (TargetPlayer) targets1.get(0);
        List<Player> actual1 = target10.filter();
        List<Player> expected1 = new ArrayList<>();
        expected1.add(targetOtherRoom);
        assertTrue(actual1.containsAll(expected1));
        assertTrue(expected1.containsAll(actual1));
        target10.setPlayer(targetOtherRoom);

        TargetCell target11 = (TargetCell) targets1.get(1);
        List<Cell> actual11 = target11.filter();
        List<Cell> expected11 = new ArrayList<>();
        expected11.add(board.getCells().get(1));
        expected11.add(board.getCells().get(2));
        expected11.add(board.getCells().get(3));
        expected11.add(board.getCells().get(7));
        expected11.add(board.getCells().get(10));
        assertTrue(actual11.containsAll(expected11));
        assertTrue(expected11.containsAll(actual11));
        target11.setCell(board.getCells().get(1));

        module1.getEffects().forEach(e -> {
            e.setAuthor(author);
            e.apply();
        });

        assertEquals(0, author.getDamageByAuthor(author));
        assertEquals(2, targetSameCell.getDamageByAuthor(author));
        assertEquals(0, targetAdjacent.getDamageByAuthor(author));
        assertEquals(0, targetSameRoom.getDamageByAuthor(author));
        assertEquals(3, targetOtherRoom.getDamageByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getDamageByAuthor(author));
        assertEquals(0, targetFarAway.getDamageByAuthor(author));
        assertEquals(0, targetNullPosition.getDamageByAuthor(author));

        assertEquals(0, author.getMarkingsByAuthor(author));
        assertEquals(0, targetSameCell.getMarkingsByAuthor(author));
        assertEquals(0, targetAdjacent.getMarkingsByAuthor(author));
        assertEquals(0, targetSameRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetOtherRoom.getMarkingsByAuthor(author));
        assertEquals(0, targetAlignedNotVisible.getMarkingsByAuthor(author));
        assertEquals(0, targetFarAway.getMarkingsByAuthor(author));
        assertEquals(0, targetNullPosition.getMarkingsByAuthor(author));

        assertEquals(targetOtherRoomStartingPosition, author.getPosition());
        assertEquals(targetSameCellStartingPosition, targetSameCell.getPosition());
        assertEquals(targetAdjacentStartingPosition, targetAdjacent.getPosition());
        assertEquals(targetSameRoomStartingPosition, targetSameRoom.getPosition());
        assertEquals(board.getCells().get(1), targetOtherRoom.getPosition());
        assertEquals(targetAlignedNotVisibleStartingPosition, targetAlignedNotVisible.getPosition());
        assertEquals(targetFarAwayStartingPosition, targetFarAway.getPosition());
        assertEquals(targetNullPositionStartingPosition, targetNullPosition.getPosition());
    }
}
