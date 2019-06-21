package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.common.exceptions.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class LobbyCell extends ListCell<String> {
    private String lobbyName;

    //item components
    private Stage stage;
    private Label label;
    private HBox itemBox;

    //password input components
    private HBox errorLabelBox;

    LobbyCell() {
        super();
        Platform.runLater(() -> stage = (Stage) getScene().getWindow());

        setBackground(Palette.backgroundImage(Palette.LIST_ITEM_BACKGROUND_IMAGEPATH));

        //participants and lobby name label
        label = new Label();
        label.setFont(Palette.DEFAULT_FONT);
        label.setTextFill(Palette.ADRENALINE_ORANGE);
        label.setPadding(Palette.DEFAULT_PADDING);

        //join button
        Button joinButton = new Button(Palette.JOIN_TEXT);
        joinButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);
        joinButton.getStylesheets().add(Palette.BUTTON_REVERSE_STYLESHEET);
        joinButton.setOnAction(event -> {
            event.consume();
            if (GUI.communicationHandler == null)
                fatalErrorRoutine(Palette.GENERIC_ERROR_TEXT);
            passwordChoiceRoutine();
        });

        //spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(joinButton, Priority.ALWAYS);

        //cell
        HBox.setMargin(joinButton, Palette.LARGE_STRETCH_MARGIN);
        HBox.setMargin(label, Palette.MEDIUM_STRETCH_MARGIN);
        itemBox = new HBox(label, spacer, joinButton);
        itemBox.setAlignment(Pos.CENTER_LEFT);
    }

    private void fatalErrorRoutine(String message) {
        Palette.errorAlert(Palette.ERROR_TITLE_TEXT, null, message, stage).showAndWait();
        //stage.close();
        //System.exit(-1);
    }

    private void errorRoutine(String errorMessage) {
        ((Label) errorLabelBox.getChildren().get(0)).setText(errorMessage);
        errorLabelBox.setVisible(true);

        PauseTransition visiblePause = new PauseTransition(Duration.seconds(Palette.DEFAULT_TIMEOUT));
        visiblePause.setOnFinished(event -> errorLabelBox.setVisible(false));
        Platform.runLater(visiblePause::play);
    }

    //TODO: change from Dialog to separate pane
    private Dialog<String> createPasswordDialog() {
        Dialog<String> passwordDialog = Palette.passwordDialog(Palette.TITLE_TEXT, null, Palette.PASSWORD_TEXT, stage);

        errorLabelBox = Palette.labelBox(null, Palette.ADRENALINE_RED, Palette.ADRENALINE_DARK_GRAY,
                Palette.DEFAULT_FONT, Palette.DEFAULT_PADDING, Palette.DEFAULT_MARGIN, Pos.CENTER);
        errorLabelBox.setVisible(false);

        Label passwordLabel = new Label(Palette.PASSWORD_TEXT);

        TextField inputPassword = new TextField();
        Platform.runLater(inputPassword::requestFocus);
        inputPassword.getStylesheets().add(Palette.TEXT_FIELD_STYLESHEET);
        //TODO add ENTER key event filter
        /*inputPassword.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                String lobbyPassword = inputPassword.getText();
                handleLobbyLogin(lobbyName, lobbyPassword);
            }
        });*/

        HBox passwordBox = new HBox();
        passwordBox.getChildren().addAll(passwordLabel, inputPassword);

        VBox base = new VBox();
        base.getChildren().addAll(passwordBox, errorLabelBox);

        passwordDialog.setGraphic(base);
        passwordDialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData().equals(ButtonType.OK.getButtonData())) {
                return inputPassword.getText();
            }
            return null;
        });
        return passwordDialog;
    }

    private void passwordChoiceRoutine() {
        Optional<String> lobbyPassword = createPasswordDialog().showAndWait();
        lobbyPassword.ifPresent(password -> handleLobbyLogin(lobbyName, password));
    }

    private void handleLobbyLogin(String lobbyName, String lobbyPassword) {
        try {
            GUI.communicationHandler.login(lobbyName, lobbyPassword);
            fatalErrorRoutine("LOGIN SUCCESS !!!");
        } catch (ConnectionException | LobbyFullException | InvalidPasswordException | GameAlreadyStartedException | LobbyNotFoundException e) {
            fatalErrorRoutine(e.getMessage());
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
