package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.weaponry.Weapon;

import static it.polimi.ingsw.view.remote.WallType.*;

public class BoardGraph {

    public void printWall(WallType wall) {
        switch (wall) {
            case VER_FULL:
                CLI.print("┃");
                break;
            case HOR_FULL:
                CLI.print("━━━━━━━━━━━━━━━━━━━━━━━━");
                break;
            case VER_DOOR:
                CLI.print("┆");
                break;
            case HOR_DOOR:
                CLI.print("╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌╌");
                break;
            case VER_VOID:
                CLI.print(" ");
                break;
            case HOR_VOID:
                CLI.print("                        ");
                break;
            case ANGLE:
                CLI.print("╋");
                break;
            default:    //it covers case NONE
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

    public void printShop(Board board, AmmoCubes ammoCubeColor) {   //prints every weapon in a selected shop

        Cell cell = board.findSpawnPoint(ammoCubeColor);

        for(int i=0; i < ((SpawnCell) cell).getWeaponShop().size(); i++) {
            CLI.println((i + 1) + ". " + ((SpawnCell) cell).getWeaponShop().get(i).getName());
            CLI.println("\tSelling for: " + ((SpawnCell) cell).getWeaponShop().get(i).getPurchaseCost().toString());
        }
    }

    public void printPlayerStatus(Board board, Player player) {

        //given a player, it displays damageboard, list of weapon, owned ammocubes and so on.
        CLI.println("Player number " + player.getID() + ", codename: " + player.getName());
        //printing weapons
        if(player.getWeapons().size() == 0) {
            CLI.println("This player has no weapons!");
        }
        else {
            int index=1;
            for(Weapon w: player.getWeapons()) {
                CLI.print(((char) index) + ". " + w.getName());

                if(w.isLoaded())
                    CLI.println("\tReady to fire!");
                else
                    CLI.println("\tUnloaded");
                index++;
            } //end for
        } //end else

        //printing damageboard
        CLI.print("Damage taken:\t");
        for(Player p: player.getDamagersList()) {
            CLI.print(Integer.toString(p.getID()));
        } //end for

        CLI.println("\nMarkers taken:");
        for(Player author: board.getGame().getParticipants()) {

            int m = player.getMarkingsByAuthor(author);
            if(m > 0)
                CLI.println(m + " by player number " + author.getID() + " [codename: " + author.getName() + "]");
        } //end for
        CLI.println("Dead " + player.getDeathCount() + " times");

        printDamageBoard(board, player);
    }

    public void printCellCoordinate(Cell cell) {
        if(cell != null)
            CLI.print(" <" + (cell.getXCoord() + 1) + "," + ((cell.getYCoord() + 1) + ">      "));
        else
            CLI.print("                        ");
    }

    public void printFirstLine(Cell cell) {
        if(cell == null)
            return;

        if(cell.isSpawnPoint()) {
            CLI.print("            ");   //12 spaces
        }
        else {
            try {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0)
                    CLI.print(" RED: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() + "     ");

                else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                    CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + "  ");

                else
                    CLI.print("BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + "    ");
            }
            catch(NullPointerException e) {
                CLI.print("            ");
            }
        }
    }

    public void printSecondLine(Cell cell) {
        if(cell == null)
            return;

        if(cell.isSpawnPoint()) {
            switch(((SpawnCell) cell).getAmmoCubeColor().toStringAsColor()) {
                case("red"):
                    CLI.print(" < RED >    ");
                    break;
                case("yellow"):
                    CLI.print(" < YELLOW > ");
                    break;
                case("blue"):
                    CLI.print(" < BLUE >   ");
                default:
                    break;
            }
        }
        else {  //cell is AmmoCell
            try {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0) {
                    //red cubes have been printed by printFirstLine method, so this has to print yellow or blue cubes
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                        CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + "  ");

                    else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print("BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + "    ");

                    else    //if you are here, that means the cell contained only red cubes, so there aren't any more to print
                        CLI.print("            ");   //12 spaces
                } else {
                    //yellow cubes has already been printed by printFirstLine, so this must print blue cubes, if any
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print("BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + "    ");
                    else
                        CLI.print("            ");   //12 spaces
                }
            }
            catch(NullPointerException e) {
                CLI.print("            ");
            }
        }
    }

    public void printThirdLine(Cell cell) {
        if(cell == null)
            return;

        if(cell.isSpawnPoint())
            CLI.print(" SPAWN/SHOP ");
        else {
            try {
                //only power-ups may be displayed
                if (((AmmoCell) cell).getAmmoTile().includesPowerUp())
                    CLI.println(" *POWER UP* ");
                else
                    CLI.print("            ");   //12 spaces
            }
            catch(NullPointerException e) {
                CLI.print("            ");
            }
        }
    }

    public void printFourthLine(Cell cell) {
        int charCounter = 0;
        if(cell == null)
            return;
        //this time, it doesn't matter whether the cell is a SpawnCell or not
        for(Player p: cell.getBoard().getGame().getParticipants()) {
            if (p.getPosition() == cell) {
                CLI.print(" " + p.getID());
                charCounter += 2;
            }
        }
        //completes the row with the right number of spaces
        for(int i=0; i < 12 - charCounter; i++)
            CLI.print(" ");
    }

    public void printDamageBoard(Board board, Player player) {
        /*TODO:
            (this is not a priority for basic CLI behaviour)
         */
    }

    //prints killers (being normal or overkill) and doublekillers
    public void printBoardStatus(Board board) {

        //print Kills list
        boolean emptyList = true;
        CLI.print("\nKills list:");
        for(Player p: board.getKillers()) {
            if(p != null) {
                if(board.getKillers().get(board.getKillers().indexOf(p) + 1) == null) {
                    CLI.print(" " + p.getID() + " (x2)");
                }
                else {
                    emptyList = false;
                    CLI.print(" " + p.getID());
                }
            }
        }
        if(emptyList)
            CLI.print("\t<No kills yet>");

        emptyList = true;
        CLI.print("\nDouble kills list:");
        for(Player p: board.getDoubleKillers()) {
            emptyList = false;
            CLI.print(" " + p.getID());
        }
        if(emptyList)
            CLI.print("\t<No doublekills yet>");
    }

    public void printWeaponInfo(Weapon weapon) {
        /*TODO:
            (this is not a priority for basic CLI behaviour)
         */
    }
}
