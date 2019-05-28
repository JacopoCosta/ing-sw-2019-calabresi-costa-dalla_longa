package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginPrompt {
    CommunicationHandler communicationHandler;
    LoginManagement loginManagement;

    public LoginPrompt(CommunicationHandler communicationHandler){
        this.communicationHandler = communicationHandler;
        loginManagement = new LoginManagement(communicationHandler);
    }

    public void display() {

        //setting nodes
        Label nickLabel = new Label("Choose your nickname:");
        Button done = new Button("Done");
        TextField nickText = new TextField();

        HBox layout = new HBox();
        layout.getChildren().addAll(nickLabel, nickText, done);
        layout.setSpacing(10);

        Scene scene = new Scene(layout);
        Stage window = new Stage();
        window.setTitle("Login");
        window.setScene(scene);
        window.setWidth(800);
        window.setHeight(300);
        window.initModality(Modality.APPLICATION_MODAL);

        done.setOnAction(event -> {

            //requesting access
            String username;
                username = nickText.getText();

                try {
                    communicationHandler.register(username);
                    loginManagement.display();

                } catch (ConnectionException e) {
                    System.exit(-1);
                } catch (ClientAlreadyRegisteredException e) { }
        });

        window.showAndWait();
    }

}
