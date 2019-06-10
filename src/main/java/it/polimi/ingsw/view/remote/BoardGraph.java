package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.view.remote.status.RemoteBoard;
import it.polimi.ingsw.view.remote.status.RemoteCell;
import it.polimi.ingsw.view.remote.status.RemotePlayer;

import static it.polimi.ingsw.view.remote.ContentType.*;

public class BoardGraph {

    private static final int internalWidth = 28;

    public void printWall(ContentType wall) {
        switch (wall) {
            case VER_FULL:
                CLI.print("┃");
                break;
            case HOR_FULL:
                CLI.print(fillWith(internalWidth, "━"));
                break;
            case VER_DOOR:
                CLI.print("┆");
                break;
            case HOR_DOOR:
                CLI.print(fillWith(internalWidth, "╌"));
                break;
            case VER_VOID:
                CLI.print(" ");
                break;
            case HOR_VOID:
                CLI.print(fillWith(internalWidth, " "));
                break;
            case ANGLE:
                CLI.print("╋");
                break;
            default:    //it covers case NONE
                break;
        }
    }

    public static ContentType getWallBetweenCells(RemoteBoard board, int x1, int y1, int x2, int y2) {
        return null;
        //TODO: gently delete this
    }

    //TODO: delete this (need to fix TestBoardGraph first)
    public static ContentType getWallBetweenCells(Board board, int x1, int y1, int x2, int y2) {
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
            } catch (NullCellOperationException ignored) {
                //it never happens, as this method is invoked only after the whole board has been initialised
            }
            //the cells aren't even ghostlyAdjacent, so there isn't any separator between them
                //return NONE;

                //This case is covered by "return NONE" at the end of the method

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
        }//end else (exactly one cell exists)
        return NONE;
        //NONE is returned only if the two cells weren't even touching, its purpose is for robustness of code
    }

    public void printCellCoordinate(RemoteCell cell) {
        /*if(cell != null)
            if(cell.getId() < 10)
                CLI.print(" <" + cell.getId() + ">" + fillWith(internalWidth - 4, " "));   //single digit cell ID
            else
                CLI.print(" <" + cell.getId() + ">" + fillWith(internalWidth - 5, " "));    //double digit cell ID
        else
            CLI.print(fillWith(internalWidth, " "));   //just spaces

            TODO
            */
    }

    public void printFirstLine(RemoteCell cell) {
        /*
        if(cell == null){
            CLI.print(fillWith(internalWidth, " "));   //just spaces
            return;
        }

        if(cell.isSpawnPoint()) {
            switch(((SpawnCell) cell).getAmmoCubeColor().toStringAsColor()) {
                case("red"):
                    CLI.print(" ~RED SHOP~" + fillWith(internalWidth - 11, " "));
                    break;
                case("yellow"):
                    CLI.print(" ~YELLOW SHOP~" + fillWith(internalWidth - 14, " "));
                    break;
                case("blue"):
                    CLI.print(" ~BLUE SHOP~" + fillWith(internalWidth - 12, " "));
                default:
                    break;
            }
        }
        else {
            if(((AmmoCell) cell).getAmmoTile() == null)     //it happens when the cell doesn't contain any ammo, for example
                CLI.print(fillWith(internalWidth, " "));      //right after a player has grabbed its AmmoTile
            else {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0)
                    CLI.print(" RED: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() + fillWith(internalWidth - 7, " "));

                else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                    CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + fillWith(internalWidth - 10, " "));

                else
                    CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + fillWith(internalWidth - 8, " "));
            }
        }
        TODO
         */
    }

    public void printSecondLine(RemoteCell cell) {
        /*
        if(cell == null){
            CLI.print(fillWith(internalWidth, " "));
            return;
        }

        if(cell.isSpawnPoint()) {   //prints the first weapon in the relative shop
            try {
                CLI.print(truncate(((SpawnCell) cell).getWeaponShop().get(0).getName() + fillWith(internalWidth, " "), internalWidth));
            }
            catch(IndexOutOfBoundsException e) {
                CLI.print(fillWith(internalWidth, " "));
            }
        }

        else {  //cell is AmmoCell
            if(((AmmoCell) cell).getAmmoTile() == null)     //it happens when the cell doesn't contain any ammo, for example
                CLI.print(fillWith(internalWidth, " "));      //right after a player has grabbed its AmmoTile
            else {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0) {
                    //red cubes have been printed by printFirstLine method, so this has to print yellow or blue cubes
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                        CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + fillWith(internalWidth - 10, " "));

                    else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + fillWith(internalWidth - 8, " "));

                    else    //if you are here, that means the cell contained only red cubes, so there aren't any more to print. However, it might be a powerup
                        try {
                            //only power-ups may be displayed
                            if (((AmmoCell) cell).getAmmoTile().includesPowerUp())
                                CLI.print(" *POWER UP*" + fillWith(internalWidth - 11, " "));
                            else
                                CLI.print(fillWith(internalWidth, " "));   //just spaces
                        }
                        catch(NullPointerException e) {
                            CLI.print(fillWith(internalWidth, " "));   //just spaces
                        }
                } //end if(there were red ammocubes)

                else {
                    //yellow cubes has already been printed by printFirstLine, so this must print blue cubes, if any
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + fillWith(internalWidth - 8, " "));
                    else
                        try {
                            //only power-ups may be displayed
                            if (((AmmoCell) cell).getAmmoTile().includesPowerUp())
                                CLI.print(" *POWER UP*" + fillWith(internalWidth - 11, " "));
                            else
                                CLI.print(fillWith(internalWidth, " "));   //just spaces
                        }
                        catch(NullPointerException e) {
                            CLI.print(fillWith(internalWidth, " "));   //just spaces
                        }
                }
            } //end else (there were some ammocubes on the cell)
        } //end else (cell is AmmoCell)
        TODO
         */
    }

    public void printThirdLine(RemoteCell cell) {
        /*
        if(cell == null) {
            CLI.print(fillWith(internalWidth, " "));   //just spaces
            return;
        }
        if(cell.isSpawnPoint()) {   //prints the second weapon in the relative shop
            try {
                CLI.print(truncate(((SpawnCell) cell).getWeaponShop().get(1).getName() + fillWith(internalWidth, " "), internalWidth));
            }
            catch(IndexOutOfBoundsException e) {
                CLI.print(fillWith(internalWidth, " "));
            }
        }
        else
            CLI.print(fillWith(internalWidth, " "));

            TODO
            */
    }

    public void printFourthLine(RemoteCell cell) {
        /*
        if(cell == null) {
            CLI.print(fillWith(internalWidth, " "));   //just spaces
            return;
        }
        if(cell.isSpawnPoint()) {   //prints the third weapon in the relative shop
            try {
                CLI.print(truncate(((SpawnCell) cell).getWeaponShop().get(2).getName() + fillWith(internalWidth, " "), internalWidth));
            }
            catch(IndexOutOfBoundsException e) {
                CLI.print(fillWith(internalWidth, " "));
            }
        }
        else
            CLI.print(fillWith(internalWidth, " "));
            TODO
         */
    }

    public void printFifthLine(RemoteCell cell) {

        int charCounter = 0;
        if(cell == null){
            CLI.print(fillWith(internalWidth, " "));   //just spaces
            return;
        }
        //this time, it doesn't matter whether the cell is a SpawnCell or not
        for(RemotePlayer p: cell.getRemoteBoard().getParticipants()) {
            /*if (p.getPosition() == cell) {
                CLI.print(" " + p.getId());
                charCounter += 2;
                TODO
            }*/
        }
        //completes the row with the right number of spaces
        CLI.print(fillWith(internalWidth - charCounter, " "));
    }

    private String fillWith(int amount, String pattern) {
        if(amount <= 0)
            return "";
        else
            return (pattern + fillWith(amount - 1, pattern));
    }



    private String truncate (String s, int size) {
        return s.substring(0, s.length() < size ? s.length() : size);

        //TODO: find an elegant way to incorporate this method in CLI.print for code readability
    }

    //TODO: rewrite some methods so that players names are shown when in a cell, instead of just their number
}
