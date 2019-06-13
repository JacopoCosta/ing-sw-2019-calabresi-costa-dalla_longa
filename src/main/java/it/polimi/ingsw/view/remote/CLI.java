package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.view.remote.status.RemoteBoard;
import it.polimi.ingsw.view.remote.status.RemotePlayer;
import it.polimi.ingsw.view.remote.status.RemoteWeapon;

public class CLI extends View {

    public static void print(String message) {
        System.out.print(message); // this is the only system out print call in the entire program
    }
    
    public static void println(String message) {
        print(message + "\n");
    }

    //CLI display method, may be improved
    public static void printBoard(RemoteBoard board) {
        //FIXME: this method is obsolete

        BoardGraph graph = new BoardGraph();

        int boardWidth = RemoteBoard.getWidth();
        int boardHeight = RemoteBoard.getHeight();

        for(int h=0; h < boardHeight; h++) {

            graph.printWall(ContentType.ANGLE);

            //printing upper wall of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w, h-1));
                graph.printWall(ContentType.ANGLE);

                if(w == boardWidth-1)
                    print("\n");
            }

            //drawing cell coordinates of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printCellCoordinate(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) { //I need to print an extra vertical wall, for this is the last cell of the row
                    graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing first line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printFirstLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing second line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printSecondLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing third line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printThirdLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing fourth line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printFourthLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing fifth line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printFifthLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(BoardGraph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

        } //end for(Height)

        //drawing bottom horizontal walls
        graph.printWall(ContentType.ANGLE);
        for(int w=0; w < boardWidth; w++) {
            graph.printWall(BoardGraph.getWallBetweenCells(board, w, boardHeight, w, boardHeight - 1));
            graph.printWall(ContentType.ANGLE);
        }
    } //end printBoard


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
