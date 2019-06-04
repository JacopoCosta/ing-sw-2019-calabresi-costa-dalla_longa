package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.AmmoCell;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.exceptions.NullCellOperationException;
import it.polimi.ingsw.model.player.Player;

import static it.polimi.ingsw.view.remote.WallType.*;

public class BoardGraph {

    private static final int internalWidth = 24;

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
                CLI.print("                        ");  //same as  CLI.print(fillWithSpaces(internalWidth - 4));
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
            } catch (NullCellOperationException ignored) {
                //it never happens, as this method is invoked only after the whole board has been initialised
            }
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
        }//end else (exactly one cell exists)
        return NONE;
        //NONE is returned only if the two cells weren't even touching, its purpose is for robustness of code
    }

    public void printCellCoordinate(Cell cell) {
        if(cell != null)
            if(cell.getId() < 10)
                CLI.print(" <" + cell.getId() + ">" + fillWithSpaces(internalWidth - 4));   //single digit cell ID
            else
                CLI.print(" <" + cell.getId() + ">" + fillWithSpaces(internalWidth - 5));    //double digit cell ID
        else
            CLI.print(fillWithSpaces(internalWidth));   //just spaces
    }

    public void printFirstLine(Cell cell) {
        if(cell == null){
            CLI.print(fillWithSpaces(internalWidth));   //just spaces
            return;
        }

        if(cell.isSpawnPoint()) {
            CLI.print(fillWithSpaces(internalWidth));   //just spaces
        }
        else {
            if(((AmmoCell) cell).getAmmoTile() == null)     //it happens when the cell doesn't contain any ammo, for example
                CLI.print(fillWithSpaces(internalWidth));      //right after a player has grabbed its AmmoTile
            else {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0)
                    CLI.print(" RED: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() + fillWithSpaces(internalWidth - 7));

                else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                    CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + fillWithSpaces(internalWidth - 10));

                else
                    CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + fillWithSpaces(internalWidth - 8));
            }
        }
    }

    public void printSecondLine(Cell cell) {
        if(cell == null){
            CLI.print(fillWithSpaces(internalWidth));
            return;
        }

        if(cell.isSpawnPoint()) {
            switch(((SpawnCell) cell).getAmmoCubeColor().toStringAsColor()) {
                case("red"):
                    CLI.print(" < RED >" + fillWithSpaces(internalWidth - 8));
                    break;
                case("yellow"):
                    CLI.print(" < YELLOW >" + fillWithSpaces(internalWidth - 11));
                    break;
                case("blue"):
                    CLI.print(" < BLUE >" + fillWithSpaces(internalWidth - 9));
                default:
                    break;
            }
        }
        else {  //cell is AmmoCell
            if(((AmmoCell) cell).getAmmoTile() == null)     //it happens when the cell doesn't contain any ammo, for example
                CLI.print(fillWithSpaces(internalWidth));      //right after a player has grabbed its AmmoTile
            else {
                if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getRed() > 0) {
                    //red cubes have been printed by printFirstLine method, so this has to print yellow or blue cubes
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() > 0)
                        CLI.print(" YELLOW: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getYellow() + fillWithSpaces(internalWidth - 10));

                    else if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + fillWithSpaces(internalWidth - 8));

                    else    //if you are here, that means the cell contained only red cubes, so there aren't any more to print
                        CLI.print(fillWithSpaces(internalWidth));   //just spaces
                } //end if(there were red ammocubes)

                else {
                    //yellow cubes has already been printed by printFirstLine, so this must print blue cubes, if any
                    if (((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() > 0)
                        CLI.print(" BLUE: " + ((AmmoCell) cell).getAmmoTile().getAmmoCubes().getBlue() + fillWithSpaces(internalWidth - 8));
                    else
                        CLI.print(fillWithSpaces(internalWidth));   //just spaces
                }
            } //end else (there were some ammocubes on the cell)
        } //end else (cell is AmmoCell)
    }

    public void printThirdLine(Cell cell) {
        if(cell == null){
            CLI.print(fillWithSpaces(internalWidth));   //just spaces
            return;
        }

        if(cell.isSpawnPoint())
            CLI.print(" SPAWN/SHOP" + fillWithSpaces(internalWidth - 11));
        else {
            try {
                //only power-ups may be displayed
                if (((AmmoCell) cell).getAmmoTile().includesPowerUp())
                    CLI.print(" *POWER UP*" + fillWithSpaces(internalWidth - 11));
                else
                    CLI.print(fillWithSpaces(internalWidth));   //just spaces
            }
            catch(NullPointerException e) {
                CLI.print(fillWithSpaces(internalWidth));   //just spaces
            }
        }
    }

    public void printFourthLine(Cell cell) {
        int charCounter = 0;
        if(cell == null){
            CLI.print(fillWithSpaces(internalWidth));   //just spaces
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
        CLI.print(fillWithSpaces(internalWidth - charCounter));
    }

    private String fillWithSpaces(int amount) {
        if(amount == 1)
            return " ";
        else
            return (" " + fillWithSpaces(amount - 1));
    }
}
