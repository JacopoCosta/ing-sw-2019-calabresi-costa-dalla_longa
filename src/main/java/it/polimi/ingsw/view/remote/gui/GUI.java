package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.GraphicalInterface;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class GUI extends Application implements GraphicalInterface {
    private Stage stage;
    private HBox errorHBox;

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
            //TODO: start "lobbySelect" scene
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

    private HBox createExitLoginForeground() {
        String exitHint = "[ESC] quit";
        double hintFontSize = 20;
        double hintPadding = 10;
        double hintMargin = 10;

        return Palette.labelBox(exitHint, Palette.OPTION_TEXT_COLOR, Palette.LABEL_BACKGROUND, hintFontSize, hintPadding, hintMargin, Pos.BOTTOM_LEFT);
    }

    private ImageView createLoginBackground() {
        String backgroundImagePath = "/png/background/background.png";
        Image backgroundImage = new Image(backgroundImagePath);

        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);

        return backgroundImageView;
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

    private Scene createLoginScene() {
        HBox loginHBox = createLoginForeground();
        HBox exitHintHBox = createExitLoginForeground();
        ImageView backgroundImageView = createLoginBackground();
        errorHBox = createLoginErrorHBox();

        VBox errorVBox = new VBox();
        errorVBox.setAlignment(Pos.CENTER);
        errorVBox.getChildren().addAll(loginHBox, errorHBox);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(backgroundImageView, exitHintHBox, errorVBox);
        stackPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> { //adjust image proportions to the screen size, aspect ratio and resolution
            double w = newValue.getWidth();
            double h = newValue.getHeight();
            backgroundImageView.setFitWidth(w);
            backgroundImageView.setFitHeight(h);
            double ratio = h / w;
            Image image = backgroundImageView.getImage();
            double ih = image.getHeight();
            double iw = image.getWidth();
            double vR = ih / iw;
            backgroundImageView.setViewport((ratio < vR) ? new Rectangle2D(0, 0, iw, iw * ratio) : new Rectangle2D(0, 0, ih / ratio, ih));
        });

        Scene scene = new Scene(stackPane);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, t -> { //set the ESC button to exit the program
            if (t.getCode() == KeyCode.ESCAPE) {
                exitRoutine();
            }
        });

        return scene;
    }

    private void fatalErrorRoutine(String errorMessage) {
        String errorTitle = "Error";

        Palette.errorAlert(errorTitle, null, errorMessage, stage).showAndWait();
        stage.close();
        System.exit(0);
    }

    private void errorLabelRoutine(String errorMessage) {
        int fadeInTimeout = 3;

        ((Label) errorHBox.getChildren().get(0)).setText(errorMessage);
        errorHBox.setVisible(true);

        PauseTransition visiblePause = new PauseTransition(Duration.seconds(fadeInTimeout));
        visiblePause.setOnFinished(event -> errorHBox.setVisible(false));
        Platform.runLater(visiblePause::play);
    }

    private void exitRoutine() {
        String exitAlertTitle = "Adrenaline: the game!";
        String exitMessage = "Exit Adrenaline?";

        Optional<ButtonType> result = Palette.confirmationAlert(exitAlertTitle, null, exitMessage, stage).showAndWait();

        if (result.isPresent() && result.get().equals(ButtonType.OK)) {
            stage.close();
            System.exit(0);
        }
    }

    private Image createStageIcon() {
        String logoImagePath = "/png/logo/logo.png";
        return new Image(logoImagePath);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        String stageTitle = "Adrenaline: the game!";
        Scene loginScene = createLoginScene();

        stage.setScene(loginScene);
        stage.setTitle(stageTitle);
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setOnCloseRequest(Event::consume);
        stage.getIcons().add(createStageIcon());

        stage.show();
    }
}
