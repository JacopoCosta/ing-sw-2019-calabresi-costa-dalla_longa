package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.App;
import it.polimi.ingsw.view.remote.LoginPrompt;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

public class BoardArt {

    public static void displayLogin (Stage window) {

        window.setTitle("ADRENALINE - Login");

        double tempWidth = 400;
        double tempHeight = 300;

        //setting initial window
        Button loginButton = new Button("Login");
        Button exitButton = new Button("Exit");

        Pane startLayout = new Pane();
        startLayout.getChildren().add(loginButton);
        startLayout.getChildren().add(exitButton);

        loginButton.relocate((tempWidth/3)*2, tempHeight/2);
        exitButton.relocate(tempWidth/3, tempHeight/2);

        Scene startScene = new Scene(startLayout, tempWidth, tempHeight);
        //TextInputDialog loginDialog = new TextInputDialog();
        //TextInputDialog pswDialog = new TextInputDialog();

        //setting login Window (NOTE: IT'S A COMPLETE NEW WINDOW THAT WILL BE CLOSED AFTER SUCCESSFUL LOGIN OR BY PRESSING BACK)
        Button back = new Button("Back");
        Button ok = new Button("Ok");

        DialogPane loginLayout = new DialogPane();
        //loginDialog.setTitle("Choose your nickname:");
        //pswDialog.setTitle("Choose a password:");
        loginLayout.getChildren().add(back);
        loginLayout.getChildren().add(ok);

        Scene loginScene = new Scene(loginLayout);

        loginButton.setOnAction(event -> {
            ok.relocate((ok.getScene().getWidth()/3)*2, ok.getScene().getHeight()/2);
            back.relocate(back.getScene().getWidth()/3, back.getScene().getHeight()/2);

            LoginPrompt.display();
        });

        exitButton.setOnAction(event -> System.exit(0));

        back.setOnAction(event -> {
            exitButton.relocate(exitButton.getScene().getWidth()/3, exitButton.getScene().getHeight()/2);
            loginButton.relocate((exitButton.getScene().getWidth()/3)*2, exitButton.getScene().getHeight()/2);
            window.setScene(startScene);
            window.show();
        });

        window.setScene(startScene);
        window.show();
    }

}
