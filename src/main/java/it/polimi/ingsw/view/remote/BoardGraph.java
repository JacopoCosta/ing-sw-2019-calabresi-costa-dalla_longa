package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.view.remote.status.RemoteBoard;
import it.polimi.ingsw.view.remote.status.RemoteCell;
import it.polimi.ingsw.view.remote.status.RemotePlayer;

import static it.polimi.ingsw.view.remote.ContentType.*;

public abstract class BoardGraph {

    public static void printWall(ContentType wall, int internalWidth) {
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
            default:
                break;
        }
    }

    public static void printFirstLine(int index, int internalWidth) {   //prints the cell ID

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), internalWidth);

            if(index % (RemoteBoard.getWidth()*2 + 1) == 0)
                CLI.print("\n");
            //NOTE: this is the only case where a new line can be displayed here
        }
        else {
            int cellIndex = (index - 1) / 2;
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null)   //cell is actually an existing cell
                if (cellIndex + 1 < 10)
                    CLI.print(" <" + (cellIndex+1) + ">" + fillWith(internalWidth - 4, " "));   //single digit cell ID
                else
                    CLI.print(" <" + (cellIndex+1) + ">" + fillWith(internalWidth - 5, " "));    //double digit cell ID
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
    }

    public static void printSecondLine(int index, int internalWidth) {

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), internalWidth);

            if(index % (RemoteBoard.getWidth()*2 + 1) == 0)
                CLI.print("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {
            int cellIndex = (index - 1) / 2;
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                if(cell.isAmmoCell()) {
                    if (cell.getRed() > 0)
                        CLI.print(" RED: " + cell.getRed() + fillWith(internalWidth - 7, " "));

                    else if (cell.getYellow() > 0)
                        CLI.print(" YELLOW: " + cell.getYellow() + fillWith(internalWidth - 10, " "));

                    else if (cell.getBlue() > 0)
                        CLI.print(" BLUE: " + cell.getBlue() + fillWith(internalWidth - 8, " "));

                    else    //the cell has no ammo; it happens when an ammo has just been picked up, so there are no ammo here until the cell is refreshed
                        CLI.print(fillWith(internalWidth, " "));
                }
                else {  //cell is a spawn/shop cell
                    if(cell.getRed() > 0)
                        CLI.print(" ~RED SHOP~" + fillWith(internalWidth - 11, " "));
                    else if(cell.getYellow() > 0)
                        CLI.print(" ~YELLOW SHOP~" + fillWith(internalWidth - 14, " "));
                    else
                        CLI.print(" ~BLUE SHOP~" + fillWith(internalWidth - 12, " "));
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        } //end else (it's a cell)
    }

    public static void printThirdLine(int index, int internalWidth) {
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

    public static void printFourthLine(int index, int internalWidth) {
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

    public static void printFifthLine(int index, int internalWidth) {
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

    public static void printSixthLine(int index, int internalWidth) {

        /*
        int charCounter = 0;
        if(cell == null){
            CLI.print(fillWith(internalWidth, " "));   //just spaces
            return;
        }
        //this time, it doesn't matter whether the cell is a SpawnCell or not
        for(RemotePlayer p: RemoteBoard.getParticipants()) {
            if (p.getPosition() == cell) {
                CLI.print(" " + p.getId());
                charCounter += 2;
                TODO
            }
        }
        //completes the row with the right number of spaces
        CLI.print(fillWith(internalWidth - charCounter, " "));
        */
    }

    private static String fillWith(int amount, String pattern) {
        if(amount <= 0)
            return "";
        else
            return (pattern + fillWith(amount - 1, pattern));
    }



    private static String truncate (String s, int size) {
        return s.substring(0, s.length() < size ? s.length() : size);

        //TODO: find an elegant way to incorporate this method in CLI.print for code readability
    }

    //TODO: rewrite some methods so that players names are shown when in a cell, instead of just their number
}
