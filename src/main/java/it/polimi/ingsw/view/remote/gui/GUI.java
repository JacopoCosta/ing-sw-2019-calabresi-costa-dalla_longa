package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.GraphicalInterface;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

//quando premo esc, se sono in selectLobbyLayout devo sloggare l'utente prima di procedere. ENUM di layout in cui l'app si può trovare
//così so dove sono e agisco di conseguenza.

public class GUI extends Application implements GraphicalInterface {
    private enum Layout {
        LOGIN_LAYOUT,
        LOBBY_SELECTION_LAYOUT
    }

    private Layout currentLayout;

    //base components
    private Stage stage;
    private StackPane baseLayout;
    private HBox exitHintHBox;

    //login components
    private VBox loginVBox;
    private HBox errorLabelBox;

    //lobby selection components
    ListView<String> lobbyStatus;
    HBox lobbyBox;

    private static CommunicationHandler communicationHandler;

    public GUI() {
    }

    @Override
    public void setCommunicationHandler(CommunicationHandler communicationHandler) {
        GUI.communicationHandler = communicationHandler;
    }

    @Override
    public void display() {
        launch((String[]) null);
    }

    private void register(String username) {
        try {
            GUI.communicationHandler.register(username);
            stage.getScene().setRoot(createLobbySelectLayout());
        } catch (ConnectionException e) {
            fatalErrorRoutine(e.getMessage());
        } catch (ClientAlreadyRegisteredException e) {
            errorLabelRoutine(e.getMessage());
        }
    }

    private HBox createLoginForeground() {
        String loginButtonText = "LOGIN";
        String nicknameTextFieldHint = "Choose a nickname";
        String errorMessage = "Invalid nickname";

        double loginBoxSpacing = 10;

        //TextField to input the username
        TextField nicknameTextField = new TextField();
        nicknameTextField.setPromptText(nicknameTextFieldHint);
        nicknameTextField.setFocusTraversable(false);

        //login Button
        Button loginButton = new Button(loginButtonText);
        loginButton.setOnAction(actionEvent -> {
            String nickname = nicknameTextField.getText();

            if (nickname == null || nickname.isEmpty() || nickname.isBlank()) {
                nicknameTextField.setText(null);
                errorLabelRoutine(errorMessage);
            } else
                register(nickname);
        });

        //foreground HBox
        HBox loginBox = new HBox();
        loginBox.setSpacing(loginBoxSpacing);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getChildren().addAll(nicknameTextField, loginButton);

        return loginBox;
    }

    private HBox createExitForeground() {
        String exitHint = "[ESC] quit";
        double hintFontSize = 20;
        double hintPadding = 10;
        double hintMargin = 10;

        return Palette.labelBox(exitHint, Palette.OPTION_TEXT_COLOR, Palette.LABEL_BACKGROUND, hintFontSize, hintPadding, hintMargin, Pos.BOTTOM_LEFT);
    }

    private Background createLoginBackground() {
        String backgroundImagePath = "/png/backgrounds/login_bg.png";
        return Palette.background(backgroundImagePath);
    }

    private Background createLobbySelectionBackground() {
        String backgroundImagePath = "/png/backgrounds/lobby_selection_bg.png";
        return Palette.background(backgroundImagePath);
    }

    //creates a hidden error labelBox
    private HBox createLoginErrorHBox() {
        double errorTextFontSize = 20;
        double errorTextPadding = 10;
        double errorLabelMargin = 10;

        HBox hBox = Palette.labelBox(null, Palette.ERROR_TEXT_COLOR, Palette.LABEL_BACKGROUND, errorTextFontSize, errorTextPadding, errorLabelMargin, Pos.CENTER);
        hBox.setVisible(false);
        return hBox;
    }

    private Parent createLoginLayout() {
        currentLayout = Layout.LOGIN_LAYOUT;

        HBox loginHBox = createLoginForeground();
        exitHintHBox = createExitForeground();
        errorLabelBox = createLoginErrorHBox();

        loginVBox = new VBox();
        loginVBox.setAlignment(Pos.CENTER);
        loginVBox.getChildren().addAll(loginHBox, errorLabelBox);

        baseLayout.setBackground(createLoginBackground());
        baseLayout.getChildren().addAll(exitHintHBox, loginVBox);
        return baseLayout;
    }

    private Parent createLobbySelectLayout() {
        currentLayout = Layout.LOBBY_SELECTION_LAYOUT;

        baseLayout.setBackground(createLobbySelectionBackground());
        baseLayout.getChildren().remove(loginVBox);

        /*lobbyStatus = new ListView<>();
        lobbyStatus.getItems().addAll("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");
        lobbyStatus.setOrientation(Orientation.VERTICAL);
        lobbyStatus.setPrefSize(120, 100);

        lobbyBox = new HBox(lobbyStatus);
        lobbyBox.setSpacing(10);
        lobbyBox.setAlignment(Pos.CENTER);

        baseLayout.getChildren().add(lobbyBox);*/
        return baseLayout;
    }

    private void fatalErrorRoutine(String errorMessage) {
        String errorTitle = "Error";

        Palette.errorAlert(errorTitle, null, errorMessage, stage).showAndWait();
        stage.close();
        System.exit(-1);
    }

    private void errorLabelRoutine(String errorMessage) {
        int fadeInTimeout = 3;

        ((Label) errorLabelBox.getChildren().get(0)).setText(errorMessage);
        errorLabelBox.setVisible(true);

        PauseTransition visiblePause = new PauseTransition(Duration.seconds(fadeInTimeout));
        visiblePause.setOnFinished(event -> errorLabelBox.setVisible(false));
        Platform.runLater(visiblePause::play);
    }

    private void exitRoutine() {
        String exitAlertTitle = "Adrenaline: the game!";
        String exitMessage = "Exit Adrenaline?";

        Optional<ButtonType> result = Palette.confirmationAlert(exitAlertTitle, null, exitMessage, stage).showAndWait();

        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            if (currentLayout.equals(Layout.LOBBY_SELECTION_LAYOUT))
                try {
                    communicationHandler.unregister();
                } catch (ConnectionException | ClientNotRegisteredException e) {
                    fatalErrorRoutine(e.getMessage());
                }
            stage.close();
            System.exit(0);
        }
    }

    private Image createStageIcon() {
        String logoImagePath = "/png/logo/logo.png";
        return new Image(logoImagePath);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        baseLayout = new StackPane();

        String stageTitle = "Adrenaline: the game!";

        Scene scene = new Scene(createLoginLayout());
        scene.addEventHandler(KeyEvent.KEY_RELEASED, t -> { //set the ESC button to exit the program
            if (t.getCode() == KeyCode.ESCAPE) {
                exitRoutine();
            }
        });

        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setOnCloseRequest(Event::consume);
        stage.getIcons().add(createStageIcon());

        stage.show();
    }
}
