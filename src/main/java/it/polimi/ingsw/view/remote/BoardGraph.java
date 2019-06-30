package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.view.remote.status.*;

import java.util.List;

import static it.polimi.ingsw.view.remote.ContentType.*;

public abstract class BoardGraph {

    private static final int internalWidth = 28;    //at least 14, for now
    
    private static Console console = Console.getInstance();

    public static void printBoard() {

        int schemeWidth = RemoteBoard.getWidth()*2 + 1;     //width of the morphology grid
        int schemeHeight = RemoteBoard.getHeight()*2 + 1;   //height of the morphology grid

        List<ContentType> morphology = RemoteBoard.getMorphology(); //shorthand

        RemoteBoard.updatePlayersPosition();    //refresh for players' position on the cell scheme (needed for a correct visualization)

        for(RemoteCell c: RemoteBoard.getLogicalCells()) {
            if(c.isAmmoCell()) {
                System.out.println("ammo cell number " + (c.getLogicalIndex()+1) + " contains " + c.getRed() + ", " + c.getYellow() + ", " + c.getBlue() + ", " + c.includesPowerUp());
            }
            else
                System.out.println("spawn cell number " + c.getLogicalIndex() + " contains " + c.getShop());
        }

        for(int h=0; h<schemeHeight; h++) { //cycling on every row

            if(h%2 == 0) {  //the current row is made of angles and HOR_walls only

                for (int w = 0; w < schemeWidth; w++) { //cycling on every element of a row
                    BoardGraph.printWall(morphology.get(h*schemeWidth + w), internalWidth);

                    if(w == schemeWidth-1)  //prints a new line when the last element has just been printed
                        console.tinyPrint("\n");
                }
            } //end if (h is even)

            else {  //there are cells and VER_walls on this row

                for (int w = 0; w < schemeWidth; w++)//cycling on every element of a row
                    BoardGraph.printFirstLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printSecondLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printThirdLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printFourthLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printFifthLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printSixthLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printSeventhLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printEightLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printNinthLine(h, w);

                for (int w = 0; w < schemeWidth; w++)
                    BoardGraph.printTenthLine(h, w);
            } //end else (h is odd)
        }

    }

    //prints killers (being normal or overkill) and doublekillers
    public static void printBoardStatus() {

        //print Kills list
        boolean emptyList = true;
        console.tinyPrintln("\nKills list:");
        for(String killerName: RemoteBoard.getKillers()) { // FIXME NullPointerException when called by GraphicsEventHandler:292
            if(killerName != null) {
                if(RemoteBoard.getKillers().get(RemoteBoard.getKillers().indexOf(killerName) + 1) == null) {
                    console.tinyPrintln("\t" + killerName + " (Overkill!) ");
                }
                else {
                    emptyList = false;
                    console.tinyPrintln("\t" + killerName);
                }
            }
        }
        if(emptyList)
            console.tinyPrintln("\t<No kills yet>");
        
        emptyList = true;
        console.tinyPrintln("\nDouble kills list:");
        for(String doubleKillerName: RemoteBoard.getDoubleKillers()) {
            emptyList = false;
            console.tinyPrintln("\t" + doubleKillerName);
        }
        if(emptyList)
            console.tinyPrintln("\t<No doublekills yet>");
    }

    public static void printWeaponInfo(RemoteWeapon weapon) {
        //TODO: improve it with the right method
    }

    public static void printPlayerStatus(RemotePlayer player, boolean isSelf) { //isSelf is true when the player is the user's one

        //given a player, it displays damageboard, list of weapon, owned ammocubes and so on.
        console.tinyPrintln("Player: " + player.getName());
        //printing weapons
        if(player.getWeapons().size() == 0) {
            console.tinyPrintln("This player has no weapons!");
        }
        else {
            int index=1;
            console.tinyPrintln("Owned weapons:");
            for(RemoteWeapon w: player.getWeapons()) {
                console.tinyPrintln(((char) index) + ". " + w.getName());

                if(w.isLoaded())
                    console.tinyPrintln("\tReady to fire!");
                else
                    console.tinyPrintln("\tUnloaded");
                index++;
            } //end for
        } //end else

        //printing damageboard:
        console.tinyPrintln("Damage taken:");
        for(String author: player.getDamage())
            console.tinyPrintln("\t" + author);

        console.tinyPrintln("\nMarkers taken:");
        for(String author: player.getMarkings())
            console.tinyPrintln("\t" + author);

        console.tinyPrintln("\nDead " + player.getDeathCount() + " times");

        if(isSelf) {
            console.tinyPrintln("\nOwned power-ups:");

            if(player.getPowerUps().size() > 0)
                for(RemotePowerUp pUp : player.getPowerUps()) {
                    console.tinyPrintln("\t" + pUp.getType() + " ~ " + pUp.getColorCube());
                }
            else
                console.tinyPrintln("\t[none]");
        }
        else
            console.tinyPrintln("\nThis player owns " + player.getPowerUps().size() + " power-ups");

        BoardGraph.printDamageBoard(player);
    }

    private static void printDamageBoard(RemotePlayer player) {

        int index = player.getDamage().size();
        //TODO: finish this. Use abstract class scorelist

    }


    public static void printPowerUps(RemotePlayer player) {
        console.tinyPrintln(player.getName() + " has " + player.getPowerUps().size() + " power ups");
    }

    public static void printWall(ContentType wall, int internalWidth) {
        switch (wall) {
            case VER_FULL:
                console.tinyPrint("┃");
                break;
            case HOR_FULL:
                console.tinyPrint(fillWith(internalWidth, "━"));
                break;
            case VER_DOOR:
                console.tinyPrint("┆");
                break;
            case HOR_DOOR:
                console.tinyPrint(fillWith(internalWidth, "╌"));
                break;
            case VER_VOID:
                console.tinyPrint(" ");
                break;
            case HOR_VOID:
                console.tinyPrint(fillWith(internalWidth, " "));
                break;
            case ANGLE:
                console.tinyPrint("╋");
                break;
            default:
                break;
        }
    }

    private static void printFirstLine(int h, int w) {   //prints the cell ID

        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            //it's a vertical wall
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        }
        else {
            //it's not a vertical wall, so it's a cell (either CELL or NONE)
            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) {   //cell is actually an existing cell
                int logicalIndex = cell.getLogicalIndex();  //shorthand
                if (logicalIndex + 1 < 10)
                    console.tinyPrint(" <" + (logicalIndex + 1) + ">" + fillWith(BoardGraph.internalWidth - 4, " "));   //single digit cell ID
                else
                    console.tinyPrint(" <" + (logicalIndex + 1) + ">" + fillWith(BoardGraph.internalWidth - 5, " "));    //double digit cell ID
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }
    }

    private static void printSecondLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {
            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                if(cell.isAmmoCell()) {
                    if (cell.getRed() > 0)
                        console.tinyPrint(" RED: " + cell.getRed() + fillWith(BoardGraph.internalWidth - 7, " "));

                    else if (cell.getYellow() > 0)
                        console.tinyPrint(" YELLOW: " + cell.getYellow() + fillWith(BoardGraph.internalWidth - 10, " "));

                    else if (cell.getBlue() > 0)
                        console.tinyPrint(" BLUE: " + cell.getBlue() + fillWith(BoardGraph.internalWidth - 8, " "));

                    else    //the cell has no ammo; it happens when an ammo has just been picked up, so there are no ammo here until the cell is refreshed
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));
                }
                else {  //cell is a spawn/shop cell
                    if(cell.getRed() > 0)
                        console.tinyPrint(" ~RED SHOP~" + fillWith(BoardGraph.internalWidth - 11, " "));
                    else if(cell.getYellow() > 0)
                        console.tinyPrint(" ~YELLOW SHOP~" + fillWith(BoardGraph.internalWidth - 14, " "));
                    else
                        console.tinyPrint(" ~BLUE SHOP~" + fillWith(BoardGraph.internalWidth - 12, " "));
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        } //end else (it's a cell)
    }

    private static void printThirdLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {
            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                if(cell.isAmmoCell()) {
                    if (cell.getRed() > 0) {
                        if(cell.getYellow() > 0)              //red ammo were already printed by printSecondLine, so yellow ammo or blue ones are next
                            console.tinyPrint(" YELLOW: " + cell.getYellow() + fillWith(BoardGraph.internalWidth - 10, " "));
                        else if(cell.getBlue() > 0)       //same, but there aren't yellow ammo, so blue ones are printed instead
                            console.tinyPrint(" BLUE: " + cell.getBlue() + fillWith(BoardGraph.internalWidth - 8, " "));
                        else if (cell.includesPowerUp())    //the ammo tile is made of red ammo and a powerup
                            console.tinyPrint(" *POWER UP*" + fillWith(BoardGraph.internalWidth - 11, " "));
                    }
                    else {  //there were no red ammo, so yellow or blue must have been printed instead
                        if(cell.getYellow() > 0) {        //yellow ammo were already printed by printSecondLine
                            if (cell.getBlue() > 0)
                                console.tinyPrint(" BLUE: " + cell.getBlue() + fillWith(BoardGraph.internalWidth - 8, " "));
                            else if (cell.includesPowerUp())
                                console.tinyPrint(" *POWER UP*" + fillWith(BoardGraph.internalWidth - 11, " "));
                        }
                        else {  //the cell has just yellow ammo AND a powerup, and the yellow ammo was printed by printSecondLine
                            console.tinyPrint(" *POWER UP*" + fillWith(BoardGraph.internalWidth - 11, " "));
                        }
                    }
                }
                else {  //cell is a spawn/shop cell, so its shop needs be printed
                    if(cell.getShop().size() >= 1)
                        console.tinyPrint(truncate(" " + cell.getShop().get(0).getName() + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                    else
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }

        //TODO: this method isn't very robust, can be improved using a List containing all the possible ammo configurations.
        //  It will be improved, but its priority is very low.
    }

    private static void printFourthLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {

            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                if(cell.isAmmoCell())   //there are no more ammo to print, so a blank line will be printed instead
                    console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));    //just spaces
                else {                  //there's a shop, so the second weapon, if existing, will be printed
                    if(cell.getShop().size() >= 2)
                        console.tinyPrint(truncate(" " + cell.getShop().get(1).getName() + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                    else                //there aren't enough weapons to display
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }
    }

    private static void printFifthLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {

            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                if(cell.isAmmoCell())   //there are no more ammo to print, so a blank line will be printed instead
                    console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));    //just spaces
                else {                  //there's a shop, so the second weapon, if existing, will be printed
                    if(cell.getShop().size() >= 3)
                        console.tinyPrint(truncate(" " + cell.getShop().get(2).getName() + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                    else                //there aren't enough weapons to display
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }
    }

    //starts to print players on this cell, if any
    private static void printSixthLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {

            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
                        break;
                    case 5:
                        console.tinyPrint(truncate(cell.getPlayers().get(0) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }
    }

    private static void printSeventhLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {

            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
                        break;
                    case 2:
                    case 3:
                    case 4:
                        console.tinyPrint(truncate(cell.getPlayers().get(0) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                    case 5:
                        console.tinyPrint(truncate(cell.getPlayers().get(1) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }
    }

    private static void printEightLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {

            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                switch (cell.getPlayers().size()) {
                    case 1:
                        console.tinyPrint(truncate(cell.getPlayers().get(0) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                    case 0:
                    case 2:
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
                        break;
                    case 3:
                    case 4:
                        console.tinyPrint(truncate(cell.getPlayers().get(1) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                    case 5:
                        console.tinyPrint(truncate(cell.getPlayers().get(2) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }
    }

    private static void printNinthLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {

            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
                        break;
                    case 2:
                        console.tinyPrint(truncate(cell.getPlayers().get(1) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                    case 3:
                    case 4:
                        console.tinyPrint(truncate(cell.getPlayers().get(2) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                    case 5:
                        console.tinyPrint(truncate(cell.getPlayers().get(3) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
        }
    }

    private static void printTenthLine(int h, int w) {
        int schemeWidth = RemoteBoard.getWidth()*2 +1;
        int index = h*schemeWidth + w;   //index for current morphology element

        if(!RemoteBoard.getMorphology().get(index).equals(CELL) && !RemoteBoard.getMorphology().get(index).equals(NONE)) {
            printWall(RemoteBoard.getMorphology().get(index), BoardGraph.internalWidth);

            if(w == schemeWidth-1)
                console.tinyPrint("\n");
            //NOTE: this is the only case where a new line can be displayed here
        } //end if (not a cell)
        else {

            int cellIndex = (h/2) * RemoteBoard.getWidth() + w/2; //index for current cell element
            RemoteCell cell = RemoteBoard.getCells().get(cellIndex);    //shorthand

            if (cell != null) { //cell is actually an existing cell
                switch (cell.getPlayers().size()) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
                        break;
                    case 4:
                        console.tinyPrint(truncate(cell.getPlayers().get(3) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));
                        break;
                    case 5:
                        console.tinyPrint(truncate(cell.getPlayers().get(4) + fillWith(BoardGraph.internalWidth, " "), BoardGraph.internalWidth));   //just spaces
                        break;
                }
            }
            else    //cell is referring to a void cell
                console.tinyPrint(fillWith(BoardGraph.internalWidth, " "));   //just spaces
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
    }

}
