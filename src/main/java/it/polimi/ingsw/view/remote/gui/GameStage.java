package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameStage {
    private final CommunicationHandler communicationHandler;

    private Stage primaryStage;
    private StackPane baseLayout;
    private BorderPane foregroundLayout;

    GameStage(StackPane baseLayout, BorderPane foregroundLayout, CommunicationHandler communicationHandler) {
        this.primaryStage = (Stage) baseLayout.getScene().getWindow();
        this.baseLayout = baseLayout;
        this.foregroundLayout = foregroundLayout;
        this.communicationHandler = communicationHandler;
    }

    public void display() {
        GUI.currentLayout = GUI.Layout.GAME_STAGE_LAYOUT;

        //label
        String infoText = "\"" + communicationHandler.getUsername() + "\" logged into \"" + communicationHandler.getLobbyName() + "\"";
        HBox infoLabel = Palette.labelBox(infoText, Palette.ADRENALINE_ORANGE);

        foregroundLayout.setCenter(infoLabel);
        Platform.runLater(() -> baseLayout.requestFocus());
    }
}
