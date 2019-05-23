package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.App;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class BoardArt {

    private final double windowWidth = 400;
    private final double windowHeight = 300;

    public static void displayLogin (Stage stage) {

        stage.setTitle("ADRENALINE - Login");

        Button loginButton = new Button("Login");
        Button exitButton = new Button("Exit");
        Pane layout = new Pane();

        layout.getChildren().add(loginButton);
        layout.getChildren().add(exitButton);

        loginButton.relocate(100, 200);
        exitButton.relocate(300, 200);
        Scene scene = new Scene(layout, 400, 300);

        loginButton.setOnAction(event -> System.out.println("Logging in..."));
        exitButton.setOnAction(event -> System.exit(0));
        //NOTE: can be implemented either this way or by modifying _handle_ in App

        stage.setScene(scene);
        stage.show();
    }
}
