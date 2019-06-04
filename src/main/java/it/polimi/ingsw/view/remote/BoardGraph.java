package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
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
                CLI.print("                        ");  //24 spaces
                break;
            case ANGLE:
                CLI.print("╋");
                break;
            default:    //it covers case NONE
                break;
        }
    }

    public static WallType getWallBetweenCells(Board board, int x1, int y1, int x2, int y2) {
        if (board.getCellByCoordinates(x1, y1) != null && board.getCellByCoordinates(x2, y2) != null) { //they both exist

            try {
                if (board.getCellByCoordinates(x1, y1).isGhostlyAdjacent(board.getCellByCoordinates(x2, y2))) { //the cells may be separated by a wall, a door or nothing
                    if (!board.getCellByCoordinates(x1, y1).isAdjacent(board.getCellByCoordinates(x2, y2))) { //the cells are separated by a wall
                        if (x1 == x2)
                            return HOR_FULL;
                        else if (y1 == y2)
                            return VER_FULL;
                    }
                    else if (board.getCellByCoordinates(x1, y1).getRoom() == board.getCellByCoordinates(x2, y2).getRoom()) { //they're part of the same room
                        if (x1 == x2)
                            return HOR_VOID;
                        else if (y1 == y2)
                            return VER_VOID;
                    }
                    else { //they are separated by a door
                        if(x1 == x2)
                            return HOR_DOOR;
                        else if (y1 == y2)
                            return VER_DOOR;
                    }
                }
            } catch (NullCellOperationException ignored) { }
            /*else //the cells aren't even ghostlyAdjacent, so there isn't any separator between them
                return NONE;

                This case is covered by "return NONE" at the end of the method
             */
        }
        else if(board.getCellByCoordinates(x1, y1) == null && board.getCellByCoordinates(x2, y2) == null) {
            //none of them exist; however, they may be printed if they refers to blank spaces
            if (x1 == x2 && Math.abs(y1 - y2) == 1)
                return HOR_VOID;
            else if (y1 == y2 && Math.abs(x1 - x2) == 1)
                return VER_VOID;
        }
        else
        {
            if(board.getCellByCoordinates(x1, y1) == null) { //cell1 does not exists, while cell2 does
                if(x1 == x2 && Math.abs(y1 - y2) == 1)
                    return HOR_FULL;
                else if(y1 == y2 && Math.abs(x1 - x2) == 1)
                    return VER_FULL;
            }
            else if(board.getCellByCoordinates(x2, y2) == null) { //cell2 does not exists, while cell1 does
                if(x1 == x2 && Math.abs(y1 - y2) == 1)
                    return HOR_FULL;
                else if(y1 == y2 && Math.abs(x1 - x2) == 1) {
                    return VER_FULL;
                }
            }
        }
        return NONE;
    }

    public void printCellCoordinate(Cell cell) {
        if(cell != null)
            if(cell.getId() < 10)
                CLI.print(" <" + cell.getId() + ">                    ");   //single digit
            else
                CLI.print(" <" + cell.getId() + ">                   ");    //double digit
        else
            CLI.print("                        ");
    }

    public void printFirstLine(Cell cell) {
        if(cell == null){
            CLI.print("                        ");
            return;
        }

        if(cell.isSpawnPoint()) {
            CLI.print("                        ");   //24 spaces
        }
        else {
            try {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0)
                    CLI.print(" RED: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() + "                 ");

                else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                    CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + "              ");

                else
                    CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + "                ");
            }
            catch(NullPointerException e) {
                CLI.print("                        ");
            }
        }
    }

    public void printSecondLine(Cell cell) {
        if(cell == null){
            CLI.print("                        ");
            return;
        }

        if(cell.isSpawnPoint()) {
            switch(((SpawnCell) cell).getAmmoCubeColor().toStringAsColor()) {
                case("red"):
                    CLI.print(" < RED >                ");
                    break;
                case("yellow"):
                    CLI.print(" < YELLOW >             ");
                    break;
                case("blue"):
                    CLI.print(" < BLUE >               ");
                default:
                    break;
            }
        }
        else {  //cell is AmmoCell
            try {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0) {
                    //red cubes have been printed by printFirstLine method, so this has to print yellow or blue cubes
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                        CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + "              ");

                    else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + "                ");

                    else    //if you are here, that means the cell contained only red cubes, so there aren't any more to print
                        CLI.print("                        ");   //24 spaces
                } else {
                    //yellow cubes has already been printed by printFirstLine, so this must print blue cubes, if any
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + "                ");
                    else
                        CLI.print("                        ");   //24 spaces
                }
            }
            catch(NullPointerException e) {
                CLI.print("                        ");
            }
        }
    }

    public void printThirdLine(Cell cell) {
        if(cell == null){
            CLI.print("                        ");
            return;
        }

        if(cell.isSpawnPoint())
            CLI.print(" SPAWN/SHOP             ");
        else {
            try {
                //only power-ups may be displayed
                if (((AmmoCell) cell).getAmmoTile().includesPowerUp())
                    CLI.print(" *POWER UP*             ");
                else
                    CLI.print("                        ");   //24 spaces
            }
            catch(NullPointerException e) {
                CLI.print("                        ");
            }
        }
    }

    public void printFourthLine(Cell cell) {
        int charCounter = 0;
        if(cell == null){
            CLI.print("                        ");
            return;
        }
        //this time, it doesn't matter whether the cell is a SpawnCell or not
        for(Player p: cell.getBoard().getGame().getParticipants()) {
            if (p.getPosition() == cell) {
                CLI.print(" " + p.getId());
                charCounter += 2;
            }
        }
        //completes the row with the right number of spaces
        for(int i=0; i < 24 - charCounter; i++)
            CLI.print(" ");
    }
}
