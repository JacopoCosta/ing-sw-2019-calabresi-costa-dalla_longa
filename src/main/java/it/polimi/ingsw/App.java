package it.polimi.ingsw;

import it.polimi.ingsw.view.remote.BoardArt;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;


public class App extends Application implements EventHandler<javafx.event.ActionEvent> {

    public static void main(String[] args) {
        //launch(args);

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