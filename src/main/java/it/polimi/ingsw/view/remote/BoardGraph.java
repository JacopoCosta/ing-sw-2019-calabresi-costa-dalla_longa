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
                    if (cell.getRed() > 0) {
                        if(cell.getBlue() > 0)              //red ammo were already printed by printSecondLine, so blue ammo or yellow ones are next
                            CLI.print(" BLUE: " + cell.getBlue() + fillWith(internalWidth - 8, " "));
                        else if(cell.getYellow() > 0)       //same, but there aren't blue ammo, so yellow ones are printed instead
                            CLI.print(" YELLOW: " + cell.getYellow() + fillWith(internalWidth - 10, " "));
                        else if (cell.includesPowerUp())    //the ammo tile is made of red ammo and a powerup
                            CLI.print(" *POWER UP*" + fillWith(internalWidth - 11, " "));
                    }
                    else {  //there were no red ammo, so blue or yellow must have been printed instead
                        if(cell.getBlue() > 0) {        //blue ammo were already printed by printSecondLine
                            if (cell.getYellow() > 0)
                                CLI.print(" YELLOW: " + cell.getYellow() + fillWith(internalWidth - 10, " "));
                            else if (cell.includesPowerUp())
                                CLI.print(" *POWER UP*" + fillWith(internalWidth - 11, " "));
                        }
                        else {  //the cell has just yellow ammo AND a powerup, and the yellow ammo was printed by printSecondLine
                            CLI.print(" *POWER UP*" + fillWith(internalWidth - 11, " "));
                        }
                    }
                }
                else {  //cell is a spawn/shop cell, so its shop needs be printed
                    if(cell.getShop().size() >= 1)
                        CLI.print(truncate(cell.getShop().get(0).getName() + fillWith(internalWidth, " "), internalWidth));
                    else
                        CLI.print(fillWith(internalWidth, " "));
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }

        //TODO: this method isn't very robust, can be improved using a List containing all the possible ammo configurations.
        //  It will be improved, but its priority is very low.
    }

    public static void printFourthLine(int index, int internalWidth) {

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
                if(cell.isAmmoCell())   //there are no more ammo to print, so a blank line will be printed instead
                    CLI.print(fillWith(internalWidth, " "));    //just spaces
                else {                  //there's a shop, so the second weapon, if existing, will be printed
                    if(cell.getShop().size() >= 2)
                        CLI.print(truncate(cell.getShop().get(1).getName() + fillWith(internalWidth, " "), internalWidth));
                    else                //there aren't enough weapons to display
                        CLI.print(fillWith(internalWidth, " "));
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
    }

    public static void printFifthLine(int index, int internalWidth) {

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
                if(cell.isAmmoCell())   //there are no more ammo to print, so a blank line will be printed instead
                    CLI.print(fillWith(internalWidth, " "));    //just spaces
                else {                  //there's a shop, so the second weapon, if existing, will be printed
                    if(cell.getShop().size() >= 3)
                        CLI.print(truncate(cell.getShop().get(2).getName() + fillWith(internalWidth, " "), internalWidth));
                    else                //there aren't enough weapons to display
                        CLI.print(fillWith(internalWidth, " "));
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
    }

    //starts to print players on this cell, if any
    public static void printSixthLine(int index, int internalWidth) {

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
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        CLI.print(fillWith(internalWidth, " "));   //just spaces
                        break;
                    case 5:
                        CLI.print(truncate(cell.getPlayers().get(0) + fillWith(internalWidth, " "), internalWidth));
                        break;
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
    }

    public static void printSeventhLine(int index, int internalWidth) {

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
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                        CLI.print(fillWith(internalWidth, " "));   //just spaces
                        break;
                    case 2:
                    case 3:
                    case 4:
                        CLI.print(truncate(cell.getPlayers().get(0) + fillWith(internalWidth, " "), internalWidth));
                    case 5:
                        CLI.print(truncate(cell.getPlayers().get(1) + fillWith(internalWidth, " "), internalWidth));
                        break;
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
    }

    public static void printEigthLine(int index, int internalWidth) {

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
                switch (cell.getPlayers().size()) {
                    case 1:
                        CLI.print(truncate(cell.getPlayers().get(0) + fillWith(internalWidth, " "), internalWidth));
                        break;
                    case 0:
                    case 2:
                        CLI.print(fillWith(internalWidth, " "));   //just spaces
                        break;
                    case 3:
                    case 4:
                        CLI.print(truncate(cell.getPlayers().get(1) + fillWith(internalWidth, " "), internalWidth));
                        break;
                    case 5:
                        CLI.print(truncate(cell.getPlayers().get(2) + fillWith(internalWidth, " "), internalWidth));
                        break;
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
    }

    public static void printNinthLine(int index, int internalWidth) {

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
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                        CLI.print(fillWith(internalWidth, " "));   //just spaces
                        break;
                    case 2:
                        CLI.print(truncate(cell.getPlayers().get(1) + fillWith(internalWidth, " "), internalWidth));
                        break;
                    case 3:
                    case 4:
                        CLI.print(truncate(cell.getPlayers().get(2) + fillWith(internalWidth, " "), internalWidth));
                    case 5:
                        CLI.print(truncate(cell.getPlayers().get(3) + fillWith(internalWidth, " "), internalWidth));   //just spaces
                        break;
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
    }

    public static void printTenthLine(int index, int internalWidth) {

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
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        CLI.print(fillWith(internalWidth, " "));   //just spaces
                        break;
                    case 4:
                        CLI.print(truncate(cell.getPlayers().get(3) + fillWith(internalWidth, " "), internalWidth));
                        break;
                    case 5:
                        CLI.print(truncate(cell.getPlayers().get(4) + fillWith(internalWidth, " "), internalWidth));   //just spaces
                        break;
                }
            }
            else    //cell is referring to a void cell
                CLI.print(fillWith(internalWidth, " "));   //just spaces
        }
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
