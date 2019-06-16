package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.view.remote.status.RemoteBoard;
import it.polimi.ingsw.view.remote.status.RemotePlayer;
import it.polimi.ingsw.view.remote.status.RemoteWeapon;

import java.util.List;

public class CLI extends View {

    private static final int internalWidth = 28;    //at least 14, for now

    public static void print(String message) {
        System.out.print(message); // this is the only system out print call in the entire program
    }
    
    public static void println(String message) {
        print(message + "\n");
    }

    public static void printBoard() {

        int schemeWidth = RemoteBoard.getWidth()*2 + 1;     //width of the morphology
        int schemeHeight = RemoteBoard.getHeight()*2 + 1;   //height of the morphology

        List<ContentType> morphology = RemoteBoard.getMorphology(); //shorthand

        RemoteBoard.updatePlayersPosition();

        for(int h=0; h<schemeHeight; h++) { //cycling on every row

            if(h%2 == 0) {  //the current row is made of angles and HOR_walls only

                for (int w = 0; w < schemeWidth; w++) { //cycling on every element of a row
                BoardGraph.printWall(morphology.get(h*schemeHeight + w), internalWidth);

                    if(w == schemeWidth-1)  //prints a new line when the last element has just been printed
                        CLI.print("\n");
                }
            } //end if (h is even)

            else {  //there are cells and VER_walls on this row

                for (int w = 0; w < schemeWidth; w++) { //cycling on every element of a row
                    BoardGraph.printFirstLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printSecondLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printThirdLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printFourthLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printFifthLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printSixthLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printSeventhLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printEigthLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printNinthLine(h*schemeHeight + w, internalWidth);
                    BoardGraph.printTenthLine(h*schemeHeight + w, internalWidth);
                }
            } //end else (h is odd)
        }

    }

    //prints killers (being normal or overkill) and doublekillers
    public static void printBoardStatus() {

        //print Kills list
        boolean emptyList = true;
        CLI.println("\nKills list:");
        for(String killerName: RemoteBoard.getKillers()) {
            if(killerName != null) {
                if(RemoteBoard.getKillers().get(RemoteBoard.getKillers().indexOf(killerName) + 1) == null) {
                    CLI.println("\t" + killerName + " (Overkill!) ");
                }
                else {
                    emptyList = false;
                    CLI.println("\t" + killerName);
                }
            }
        }
        if(emptyList)
            CLI.println("\t<No kills yet>");

        emptyList = true;
        CLI.println("\nDouble kills list:");
        for(String doubleKillerName: RemoteBoard.getDoubleKillers()) {
            emptyList = false;
            CLI.println("\t" + doubleKillerName);
        }
        if(emptyList)
            CLI.println("\t<No doublekills yet>");
    }

    public static void printWeaponInfo(RemoteWeapon weapon) {
        //TODO: improve it with the right method
    }

    public static void printPlayerStatus(RemotePlayer player) {

        //given a player, it displays damageboard, list of weapon, owned ammocubes and so on.
        CLI.println("Player: " + player.getName());
        //printing weapons
        if(player.getWeapons().size() == 0) {
            CLI.println("This player has no weapons!");
        }
        else {
            int index=1;
            CLI.println("Owned weapons:");
            for(RemoteWeapon w: player.getWeapons()) {
                CLI.print(((char) index) + ". " + w.getName());

                if(w.isLoaded())
                    CLI.println("\tReady to fire!");
                else
                    CLI.println("\tUnloaded");
                index++;
            } //end for
        } //end else

        //printing damageboard:
        CLI.println("Damage taken:");
        for(String author: player.getDamage())
            CLI.println("\t" + author);

        CLI.println("\nMarkers taken:");
        for(String author: player.getMarkings())
            CLI.println("\t" + author);

        CLI.println("\nDead " + player.getDeathCount() + " times");

        CLI.printDamageBoard(player);
    }

    private static void printDamageBoard(RemotePlayer player) {

        int index = player.getDamage().size();
        //TODO: finish this. Use abstract class scorelist

    }


    public static void printPowerUps(RemotePlayer player) {
        CLI.println(player.getName() + " has " + player.getPowerUps().size() + " power ups");
    }
}
