package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.common.exceptions.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LobbyCell extends ListCell<String> {
    private String lobbyName;

    //item components
    private Stage parentStage;
    private Stage passwordSelectionStage;
    private Label label;
    private HBox itemBox;

    //password input components
    private HBox errorLabelBox;

    LobbyCell() {
        super();
        Platform.runLater(() -> parentStage = (Stage) getScene().getWindow());

        setBackground(Palette.backgroundImage(Palette.LIST_ITEM_BACKGROUND_IMAGEPATH));

        //participants and lobby name label
        label = new Label();
        label.setFont(Palette.DEFAULT_FONT);
        label.setTextFill(Palette.ADRENALINE_ORANGE);
        label.setPadding(Palette.DEFAULT_SQUARED_PADDING);

        //join button
        Button joinButton = new Button(Palette.JOIN_TEXT);
        joinButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        joinButton.getStylesheets().add(Palette.BUTTON_ALT_STYLESHEET);
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

    private void fatalErrorRoutine(String errorMessage, boolean terminate) {
        if (passwordSelectionStage != null)
            passwordSelectionStage.close();
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
        errorLabelBox = Palette.labelBox(null, Palette.ADRENALINE_RED, Palette.ADRENALINE_DARK_GRAY_TRANSPARENT,
                Palette.DEFAULT_FONT, Palette.DEFAULT_SQUARED_PADDING, Palette.DEFAULT_MARGIN, Pos.CENTER);
        errorLabelBox.setVisible(false);

        //input password text field
        TextField inputPassword = new TextField();
        inputPassword.getStylesheets().add(Palette.TEXT_FIELD_ALT_STYLESHEET);
        Platform.runLater(inputPassword::requestFocus);

        //join button
        Button joinButton = new Button(Palette.JOIN_TEXT);
        joinButton.getStylesheets().add(Palette.BUTTON_ALT_STYLESHEET);
        joinButton.setOnAction(event -> handleLobbyLogin(lobbyName, inputPassword.getText()));

        //cancel button
        Button cancelButton = new Button(Palette.CANCEL_TEXT);
        cancelButton.getStylesheets().add(Palette.BUTTON_ALT_STYLESHEET);
        cancelButton.setOnAction(event -> {
            event.consume();
            passwordSelectionStage.close();
        });

        passwordSelectionStage = Palette.passwordChoiceStage(parentStage, joinButton, cancelButton, inputPassword, errorLabelBox);
        passwordSelectionStage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                handleLobbyLogin(lobbyName, inputPassword.getText());
            }
        });
        passwordSelectionStage.show();
    }

    private void handleLobbyLogin(String lobbyName, String lobbyPassword) {
        try {
            GUI.communicationHandler.login(lobbyName, lobbyPassword);
            errorRoutine("LOGIN SUCCESS !!!");
        } catch (ConnectionException e) {
            fatalErrorRoutine(e.getMessage(), true);
        } catch (LobbyFullException | GameAlreadyStartedException | LobbyNotFoundException e) {
            fatalErrorRoutine(e.getMessage(), false);

        } catch (InvalidPasswordException e) {
            errorRoutine(e.getMessage());
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
