package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class BoardArt {

    CommunicationHandler communicationHandler;

    LoginPrompt loginPrompt;

    public BoardArt (CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
        loginPrompt = new LoginPrompt(communicationHandler);
    }

    public void displayLogin (Stage window) {

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

        loginButton.setOnAction(event -> {
            loginPrompt.display();
        });

        exitButton.setOnAction(event -> System.exit(0));
        /*
            TODO: fixing EOFException in server caused by this method
         */


        window.setScene(startScene);
        window.show();
    }

}
