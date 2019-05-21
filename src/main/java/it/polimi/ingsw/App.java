package it.polimi.ingsw;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.player.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static void main(String[] args) {
    //    launch(args);

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

        game.play();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Drawing Operations Test");

        StackPane layout = new StackPane();
        Button button = new Button("Nope");
        layout.getChildren().add(button);

        Scene scene = new Scene(layout, 300, 300);
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}