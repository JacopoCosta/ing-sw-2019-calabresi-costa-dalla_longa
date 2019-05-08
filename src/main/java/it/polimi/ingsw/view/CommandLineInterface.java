package it.polimi.ingsw.view;

import it.polimi.ingsw.model.board.Board;

public class CommandLineInterface extends View implements Viewable {

    public void drawBoard() {
        Board board = getGame().getBoard();     //SHORTHAND
        int boardWidth = board.getBoardWidth();
        int boardHeight = board.getBoardHeight();

        //sorts the cell list
        board.sortCells();
        //displays the board
        for(Integer h=0; h < boardHeight; h++) {

            //printing line 1
            for(Integer w=0; w < boardWidth; w++) {
                if(board.isExistingCell(w, h)) {
                    System.out.print("┏━━━━━━━━━━━━┓"); //14 characters in total
                }
                else {  //the cell doesn't exists
                    System.out.print("              ");
                }
            }
            System.out.print("\n");

            //printing line 2
            for(Integer w=0; w < boardWidth; w++) {
                if(board.isExistingCell(w, h)) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 3
            for(Integer w=0; w < boardWidth; w++) {
                if(board.isExistingCell(w, h)) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 4
            for(Integer w=0; w < boardWidth; w++) {
                if(board.isExistingCell(w, h)) {
                    System.out.print("┃            ┃");
                }
                else
                    System.out.print("              ");
            }
            System.out.print("\n");

            //printing line 5
            for(Integer w=0; w < boardWidth; w++) {
                if(board.isExistingCell(w, h)) {
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
}
