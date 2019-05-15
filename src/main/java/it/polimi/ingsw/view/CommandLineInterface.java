package it.polimi.ingsw.view;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.view.BoardGraph;

public class CommandLineInterface extends View implements Viewable {

    public void drawBoard() {
        Board board = getGame().getBoard();     //SHORTHAND
        int boardWidth = board.getBoardWidth();
        int boardHeight = board.getBoardHeight();

        //sorts the cell list
        board.sortCells();
        //displays the board
        for(int h = 0; h < boardHeight; h++) {

            //printing line 1
            for(int w=0; w < boardWidth; w++) {
                if(board.getCellByCoordinates(w,h) != null) {
                    System.out.print("┏━━━━━━━━━━━━┓"); //14 characters in total
                }
                else {  //the cell doesn't exists
                    System.out.print("              ");
                }
            }
            System.out.print("\n");

            //printing line 2
            for(int w=0; w < boardWidth; w++) {
                if(board.getCellByCoordinates(w,h) != null) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 3
            for(int w=0; w < boardWidth; w++) {
                if(board.getCellByCoordinates(w,h) != null) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 4
            for(int w=0; w < boardWidth; w++) {
                if(board.getCellByCoordinates(w,h) != null) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 5
            for(int w=0; w < boardWidth; w++) {
                if(board.getCellByCoordinates(w,h) != null) {
                    System.out.print("┗━━━━━━━━━━━━┛");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");
            /*TODO
                create walls and doors
                make it print all the info about the cell;
                delete the double walls between two adjacent cells
             */
        }

    }

    //CLI display method, may be improved
    public void printBoard() {

        Board board = getGame().getBoard();         //SHORTHAND
        BoardGraph graph = new BoardGraph();

        int boardWidth = board.getBoardWidth();
        int boardHeight = board.getBoardHeight();

        for(int h=0; h < boardHeight; h++) {

            //printing upper wall of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.getWallBetweenCells(board, w, h, w, h-1);
                graph.printWall(WallType.ANGLE);

                if(w == boardWidth-1)
                    System.out.print("\n");
            }

            //drawing cell coordinates of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printCellCoordinate(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) { //I need to print an extra vertical wall, for this is the last cell of the row
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    System.out.print("\n");
                }
            }

            //drawing first line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printFirstLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    System.out.print("\n");
                }
            }

            //drawing second line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printSecondLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    System.out.print("\n");
                }
            }

            //drawing third line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printThirdLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    System.out.print("\n");
                }
            }

            //drawing fourth line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printFourthLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    System.out.print("\n");
                }
            }
        } //end for(Height)
    } //end printBoard

}
