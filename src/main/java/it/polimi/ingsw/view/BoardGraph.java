package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Weapon;

import static it.polimi.ingsw.view.WallType.*;

public class BoardGraph {

    public void printWall(WallType wall) {
        switch (wall) {
            case VER_FULL:
                System.out.print("┃");
                break;
            case HOR_FULL:
                System.out.print("━━━━━━━━━━━━━━━━━━━━━━━━");
                break;
            case VER_DOOR:
                System.out.print("┆");
                break;
            case HOR_DOOR:
                System.out.print("╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌");
                break;
            case VER_VOID:
                System.out.print(" ");
                break;
            case HOR_VOID:
                System.out.print("                        ");
                break;
            case ANGLE:
                System.out.print("╋");
                break;
            default:
                break;
        }
    }

    public WallType getWallBetweenCells(Board board, int x1, int y1, int x2, int y2) {
        if (board.getCellByCoordinates(x1, y1) != null && board.getCellByCoordinates(x2, y2) != null) { //they both exist

            if (board.getCellByCoordinates(x1, y1).isGhostlyAdjacent(board.getCellByCoordinates(x2, y2))) { //the cells may be separated by a wall, a door or nothing
                if (!board.getCellByCoordinates(x1, y1).isAdjacent(board.getCellByCoordinates(x2, y2))) { //the cells are separated by a wall
                    if (x1 == x2)
                        return VER_FULL;
                    else if (y1 == y2)
                        return HOR_FULL;
                }
                else if (board.getCellByCoordinates(x1, y1).getRoom() == board.getCellByCoordinates(x2, y2).getRoom()) { //they're part of the same room
                    if (x1 == x2)
                        return VER_VOID;
                    else if (y1 == y2)
                        return HOR_VOID;
                }
                else { //they are separated by a door
                    if(x1 == x2)
                        return VER_DOOR;
                    else if (y1 == y2)
                        return HOR_DOOR;
                }
            }
            /*else //the cells aren't even ghostlyAdjacent, so there isn't any separator between them
                return NONE;

                This case is covered by "return NONE" at the end of the method
             */
        }
        else if(board.getCellByCoordinates(x1, y1) == null && board.getCellByCoordinates(x2, y2) == null) {
            //none of them exist; however, they may be printed if they refers to blank spaces
            if (x1 == x2 && Math.abs(y1 - y2) == 1)
                return VER_VOID;
            else if (y1 == y2 && Math.abs(x1 - x2) == 1)
                return HOR_VOID;
        }
        else
        {
            if(board.getCellByCoordinates(x1, y1) == null) { //cell1 does not exists, while cell2 does
                if(x1 == x2 && Math.abs(y1 - y2) == 1)
                    return VER_FULL;
                else if(y1 == y2 && Math.abs(x1 - x2) == 1)
                    return HOR_FULL;
            }
            else if(board.getCellByCoordinates(x2, y2) == null) { //cell2 does not exists, while cell1 does
                if(x1 == x2 && Math.abs(y1 - y2) == 1)
                    return VER_FULL;
                else if(y1 == y2 && Math.abs(x1 - x2) == 1) {
                    return HOR_FULL;
                }
            }
        }
        return NONE;
    }

    /*

    May be used instead of the current visualization for the gridboard. May be completely removed soon.

    public String getInternalLineOne(Cell cell) {
        String description;

        if(cell.isSpawnPoint()) {   //this cell contains a shop
            //gets the first weapon
            Weapon weapon = ((SpawnCell) cell).getWeaponShop().get(0);  //casting to SpawnCell is correct since cell.isSpawnPoint() == true
            description = "1. " + weapon.getName();
        }
        else {

        }

        return description;
    }
    */

    public void printShop(Board board, AmmoCubes ammoCubeColor) {   //prints every weapon in a selected shop

        Cell cell = board.findSpawnPoint(ammoCubeColor);

        for(int i=0; i < ((SpawnCell) cell).getWeaponShop().size(); i++) {
            System.out.println((i + 1) + ". " + ((SpawnCell) cell).getWeaponShop().get(i).getName());
            System.out.println("\tSelling for: " + ((SpawnCell) cell).getWeaponShop().get(i).getPurchaseCost().toString());
        }
    }

    public void printPlayerStatus(Board board, Player player) {

        //given a player, it displays damageboard, list of weapon, owned ammocubes and so on.
        System.out.println("Player number " + player.getID() + ", codename: " + player.getName());
        //printing weapons
        if(player.getWeapons().size() == 0) {
            System.out.println("This player has no weapons!");
        }
        else {
            int index=1;
            for(Weapon w: player.getWeapons()) {
                System.out.print(((char) index) + ". " + w.getName());

                if(w.isLoaded())
                    System.out.println("\tReady to fire!");
                else
                    System.out.println("\tUnloaded");
                index++;
            }
        }

        //printing damageboard
        System.out.print("Damage taken:\t");
        for(Player p: player.getDamagersList()) {
            System.out.print(((char) p.getID()));
        }
        System.out.println("\nMarkers taken:");
        for(Player author: board.getGame().getParticipants()) {

            int m = player.getMarkingsByAuthor(author);
            if(m > 0)
                System.out.println(m + " by player number " + author.getID() + " [codename: " + author.getName() + "]");
        }
        System.out.println("Dead " + player.getDeathCount() + " times");

        printDamageBoard(board, player);
    }

    public void printDamageBoard(Board board, Player player) {
        /*TODO:
            set some Player methods for this
         */
    }

    //prints killers, doublekillers etc.
    public void printBoardStatus(Board board) {
        /*
        TODO:
            there isn't a way to determine whether a pair of kills was caused by two kills in a row
            or by a doublekill, need to fix this in the model
         */
    }

    public void printWeaponInfo(Weapon weapon) {
        /*TODO:
            ultimate json files to define how to load weapon info
         */
    }
}
