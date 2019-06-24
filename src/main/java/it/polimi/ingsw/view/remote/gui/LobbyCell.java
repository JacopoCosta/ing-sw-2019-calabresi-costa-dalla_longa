package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.common.exceptions.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LobbyCell extends ListCell<String> {
    private String lobbyName;

    //item components
    private Stage parentStage;
    private Stage passwordChoiceStage;
    private Label label;
    private HBox itemBox;

    //password input components
    private HBox errorLabelBox;

    LobbyCell() {
        super();
        Platform.runLater(() -> parentStage = (Stage) getScene().getWindow());

        setBackground(Palette.backgroundImage(Palette.LIST_ITEM_BACKGROUND_IMAGE));

        //participants and lobby name label
        label = Palette.label(null, Palette.ADRENALINE_ORANGE, Color.TRANSPARENT);
        label.setPadding(Palette.DEFAULT_SQUARED_PADDING);

        //join button
        Button joinButton = Palette.buttonAlt(Palette.JOIN_TEXT);
        joinButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        joinButton.setOnAction(event -> {
            event.consume();
            if (GUI.communicationHandler == null)
                fatalErrorRoutine(Palette.GENERIC_ERROR_TEXT, true);
            passwordChoiceRoutine();
        });

        //spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(joinButton, Priority.ALWAYS);

        //cell
        HBox.setMargin(joinButton, Palette.LARGE_RIGHT_MARGIN);
        HBox.setMargin(label, Palette.MEDIUM_LEFT_MARGIN);
        itemBox = new HBox(label, spacer, joinButton);
        itemBox.setAlignment(Pos.CENTER_LEFT);
    }

    private void createGameLayout(){
        GUI.foregroundLayout.getChildren().remove(GUI.lobbySelectionLayout);
        GameStage gameStage = new GameStage(GUI.baseLayout, GUI.foregroundLayout, GUI.communicationHandler);
        gameStage.display();
    }

    private void fatalErrorRoutine(String errorMessage, boolean terminate) {
        if (passwordChoiceStage != null)
            passwordChoiceStage.close();

        Palette.errorAlert(Palette.ERROR_TITLE_TEXT, null, errorMessage, parentStage).showAndWait();

        if (terminate) {
            parentStage.close();
            System.exit(-1);
        }
    }

    private void errorRoutine(String errorMessage) {
        ((Label) errorLabelBox.getChildren().get(0)).setText(errorMessage);
        errorLabelBox.setVisible(true);

        PauseTransition visiblePause = new PauseTransition(Duration.seconds(Palette.DEFAULT_TIMEOUT));
        visiblePause.setOnFinished(event -> errorLabelBox.setVisible(false));
        Platform.runLater(visiblePause::play);
    }


    private void passwordChoiceRoutine() {
        //error label
        errorLabelBox = Palette.labelBox(null, Palette.ADRENALINE_RED);
        errorLabelBox.setVisible(false);

        //input password text field
        TextField inputPassword = Palette.textFieldAlt();
        Platform.runLater(inputPassword::requestFocus);

        //join button
        Button joinButton = Palette.buttonAlt(Palette.JOIN_TEXT);
        joinButton.setOnAction(event -> handleLobbyLogin(lobbyName, inputPassword.getText()));

        //cancel button
        Button cancelButton = Palette.buttonAlt(Palette.CANCEL_TEXT);
        cancelButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            passwordChoiceStage.close();
        });

        passwordChoiceStage = Palette.passwordChoiceStage(parentStage, joinButton, cancelButton, inputPassword, errorLabelBox);
        passwordChoiceStage.showAndWait();
    }

    private void handleLobbyLogin(String lobbyName, String lobbyPassword) {
        try {
            GUI.communicationHandler.login(lobbyName, lobbyPassword);
            stopLobbyUpdate();
            createGameLayout();

            if (passwordChoiceStage != null)
                passwordChoiceStage.close();
        } catch (ConnectionException | PlayerAlreadyAddedException e) {
            fatalErrorRoutine(e.getMessage(), true);
        } catch (LobbyFullException | GameAlreadyStartedException | LobbyNotFoundException e) {
            fatalErrorRoutine(e.getMessage(), false);
        } catch (InvalidPasswordException e) {
            errorRoutine(e.getMessage());
        }
    }

    //stops the update and print process
    private void stopLobbyUpdate() {
        if (!GUI.futureUpdate.isDone()) {
            GUI.futureUpdate.cancel(true);
            GUI.executor.shutdown();
        }
    }

    @Override
    protected void updateItem(String value, boolean isEmpty) {
        super.updateItem(value, isEmpty);
        setText(null);

        if (isEmpty) {
            setPrefHeight(0D);
            setGraphic(null);
        } else {
            lobbyName = value.substring(value.indexOf(']') + 2);

            setPrefHeight(Palette.LIST_VIEW_ITEM_HEIGHT / 2);
            setPrefWidth(Palette.LIST_VIEW_ITEM_WIDTH / 2);

            label.setText(value);
            setGraphic(itemBox);
        }
    }
}
