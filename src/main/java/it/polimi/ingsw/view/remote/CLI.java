package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.ammo.AmmoCubes;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.cell.Cell;
import it.polimi.ingsw.model.cell.SpawnCell;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.powerups.PowerUp;
import it.polimi.ingsw.model.util.Table;
import it.polimi.ingsw.model.weaponry.Weapon;
import it.polimi.ingsw.view.remote.status.RemoteBoard;

import java.util.List;
import java.util.stream.Collectors;

public class CLI extends View {

    public static void print(String message) {
        System.out.print(message); // this is the only system out print call in the entire program
    }
    
    public static void println(String message) {
        print(message + "\n");
    }

    //CLI display method, may be improved
    public static void printBoard(RemoteBoard board) {

        BoardGraph graph = new BoardGraph();

        int boardWidth = board.getWidth();
        int boardHeight = board.getHeight();

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

    public static List<ContentType> getMorphology(Board board) {
        //TODO
        return null;
    }

    //prints killers (being normal or overkill) and doublekillers
    public static void printBoardStatus(Board board) {

        //print Kills list
        boolean emptyList = true;
        CLI.print("\nKills list:");
        for(Player p: board.getKillers()) {
            if(p != null) {
                if(board.getKillers().get(board.getKillers().indexOf(p) + 1) == null) {
                    CLI.print(" " + p.getId() + " (x2)");
                }
                else {
                    emptyList = false;
                    CLI.print(" " + p.getId());
                }
            }
        }
        if(emptyList)
            CLI.print("\t<No kills yet>");

        emptyList = true;
        CLI.print("\nDouble kills list:");
        for(Player p: board.getDoubleKillers()) {
            emptyList = false;
            CLI.print(" " + p.getId());
        }
        if(emptyList)
            CLI.print("\t<No doublekills yet>");
    }

    public static void printWeaponInfo(Weapon weapon) {
        //TODO: improve it with the right method
    }

    public static void printPlayerStatus(Player player) {

        //given a player, it displays damageboard, list of weapon, owned ammocubes and so on.
        CLI.println("Player number " + player.getId() + ", codename: " + player.getName());
        //printing weapons
        if(player.getWeapons().size() == 0) {
            CLI.println("This player has no weapons!");
        }
        else {
            int index=1;
            CLI.println("Owned weapons:");
            for(Weapon w: player.getWeapons()) {
                CLI.print(((char) index) + ". " + w.getName());

                if(w.isLoaded())
                    CLI.println("\tReady to fire!");
                else
                    CLI.println("\tUnloaded");
                index++;
            } //end for
        } //end else

        //printing damageboard:
        CLI.print("Damage taken:\t");
        CLI.println(Table.list(player.getDamageAsList().stream().map(Player::getId).collect(Collectors.toList())));

        CLI.println("\nMarkers taken:");
        CLI.println(Table.list(player.getMarkingsAsList().stream().map(Player::getId).collect(Collectors.toList())));

        CLI.println("Dead " + player.getDeathCount() + " times");

        CLI.printDamageBoard(player);
    }

    private static void printDamageBoard(Player player) {

        int index = player.getDamage();
        //TODO: finish this. Use abstract class scorelist

    }


    public static void printShop(Board board, AmmoCubes ammoCubeColor) {   //prints every weapon in a selected shop

        Cell cell = board.findSpawnPoint(ammoCubeColor);

        for(int i = 0; i < ((SpawnCell) cell).getWeaponShop().size(); i++) {
            CLI.println((i + 1) + ". " + ((SpawnCell) cell).getWeaponShop().get(i).getName());
            CLI.println("\tSelling for: " + ((SpawnCell) cell).getWeaponShop().get(i).getPurchaseCost().toString());
        }
    }

    public static void printPowerUps(Player player) {
        int index = 0;
        for(PowerUp pwup: player.getPowerUps()) {
            CLI.print((index + 1) + ". " + player.getPowerUps().get(index) + "\n");
            index++;
        }
        if(index == 0)
            CLI.println("No power-up!");
    }

}
