package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.model.board.Board;

public class CLI extends View implements Viewable {
    
    public static void print(String message) {
        System.out.print(message); // this is the only system out print call in the entire program
    }
    
    public static void println(String message) {
        print(message + "\n");
    }

    //CLI display method, may be improved
    public void printBoard() {

        Board board = getGame().getBoard();         //SHORTHAND
        BoardGraph graph = new BoardGraph();

        int boardWidth = board.getWidth();
        int boardHeight = board.getHeight();

        for(int h=0; h < boardHeight; h++) {

            //printing upper wall of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.getWallBetweenCells(board, w, h, w, h-1);
                graph.printWall(WallType.ANGLE);

                if(w == boardWidth-1)
                    print("\n");
            }

            //drawing cell coordinates of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printCellCoordinate(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) { //I need to print an extra vertical wall, for this is the last cell of the row
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing first line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printFirstLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing second line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printSecondLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing third line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printThirdLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }

            //drawing fourth line of every cell
            for(int w=0; w < boardWidth; w++) {

                graph.printWall(graph.getWallBetweenCells(board, w, h, w-1, h));
                graph.printFourthLine(board.getCellByCoordinates(w, h));

                if(w == boardWidth-1) {
                    graph.printWall(graph.getWallBetweenCells(board, w, h, w+1, h));
                    print("\n");
                }
            }
        } //end for(Height)
    } //end printBoard

}
