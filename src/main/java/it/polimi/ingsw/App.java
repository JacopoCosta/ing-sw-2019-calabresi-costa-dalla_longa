package it.polimi.ingsw;

import it.polimi.ingsw.view.remote.Viewable;
import it.polimi.ingsw.view.remote.BoardArt;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;


public class App extends Application implements EventHandler<javafx.event.ActionEvent> {

    public static void main(String[] args) {
        //launch(args);

        /*
        List<Player> participants = new ArrayList<>();
        participants.add(new Player("Aldo"));
        participants.add(new Player("Giovanni sulla stessa cella"));
        participants.add(new Player("Giovanni adiacente"));
        participants.add(new Player("Giovanni nella stessa stanza"));
        participants.add(new Player("Giovanni nell'altra stanza"));
        participants.add(new Player("Giovanni allineato ma non visibile"));
        participants.add(new Player("Giovanni molto lontano"));
        participants.add(new Player("Giacomo"));

        Game game = Game.create(true, 5, 2, participants);

        ((CLI) game.getView()).printBoard();
        */

        //game.play();
    }

    @Override
    public void start(Stage primaryStage) {

        BoardArt.displayLogin(primaryStage);
    }

    @Override
    public void handle(javafx.event.ActionEvent event) {
        //nothing for now
    }

}