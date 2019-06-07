package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.GraphicalInterface;
import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class GUI extends Application implements GraphicalInterface {
    private Stage stage;
    private StackPane stackPane;
    private CommunicationHandler communicationHandler;

    public GUI() {
    }

    @Override
    public void setCommunicationHandler(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    @Override
    public void display() {
        launch((String[]) null);
    }

    private void register(String username) {
        try {
            communicationHandler.register(username);
        } catch (ConnectionException e) {
            fatalErrorRoutine(e.getMessage());
        } catch (ClientAlreadyRegisteredException e) {
            errorLabelRoutine(e.getMessage());
        }
    }

    private HBox createLoginForeground() {
        String loginButtonText = "LOGIN";
        String nicknameTextFieldHint = "Choose a nickname";

        double loginBoxSpacing = 10;

        //TextField to input the username
        TextField nicknameTextField = new TextField();
        nicknameTextField.setPromptText(nicknameTextFieldHint);
        nicknameTextField.setFocusTraversable(false);

        //login Button
        Button loginButton = new Button(loginButtonText);
        loginButton.setOnAction(actionEvent -> {
            String username = nicknameTextField.getText();
            register(username);
        });

        //foreground HBox
        HBox loginBox = new HBox();
        loginBox.setSpacing(loginBoxSpacing);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getChildren().addAll(nicknameTextField, loginButton);

        return loginBox;
    }

    private ImageView createBackground() {
        String backgroundImagePath = "/png/background/background.png";
        Image backgroundImage = new Image(backgroundImagePath);

        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setPreserveRatio(true);

        return backgroundImageView;
    }

    private Scene toScene(ImageView imageView, HBox hBox1, HBox hBox2) {
        stackPane = new StackPane();
        stackPane.getChildren().addAll(imageView, hBox1, hBox2);
        //adjust image proportions to the screen size, aspect ratio and resolution
        stackPane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
            double w = newValue.getWidth();
            double h = newValue.getHeight();
            imageView.setFitWidth(w);
            imageView.setFitHeight(h);
            double ratio = h / w;
            Image image = imageView.getImage();
            double ih = image.getHeight();
            double iw = image.getWidth();
            double vR = ih / iw;
            imageView.setViewport((ratio < vR) ? new Rectangle2D(0, 0, iw, iw * ratio) : new Rectangle2D(0, 0, ih / ratio, ih));
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
        Color errorTextColor = new Color(208.0 / 255, 100.0 / 255, 88.0 / 255, 1);
        Color exitBackgroundColor = new Color(34.0 / 255, 32.0 / 255, 43.0 / 255, 0.8);
        double errorTextFontSize = 20;
        double errorTextPadding = 10;
        double errorLabelMargin = 10;
        Pos errorAlignment = Pos.CENTER;

        HBox errorLabel = Palette.label(errorMessage, errorTextColor, exitBackgroundColor, errorTextFontSize, errorTextPadding, errorLabelMargin, errorAlignment);

        stackPane.getChildren().add(errorLabel);

        /*PauseTransition visiblePause = new PauseTransition(
                Duration.seconds(3)
        );
        visiblePause.setOnFinished(
                event -> errorLabel.setVisible(false)
        );
        Platform.runLater(visiblePause::play);*/
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

    private Image createIcon() {
        String logoImagePath = "/png/logo/logo.png";
        return new Image(logoImagePath);
    }

    private HBox createExitForeground() {
        String exitHint = "Press [ESC] to quit";
        Color textHintColor = new Color(189.0 / 255, 103.0 / 255, 56.0 / 255, 1);
        Color backgroundHintColor = new Color(34.0 / 255, 32.0 / 255, 43.0 / 255, 0.8);
        double hintFontSize = 20;
        double hintPadding = 10;
        double hintMargin = 10;
        Pos hintAlignment = Pos.BOTTOM_LEFT;

        return Palette.label(exitHint, textHintColor, backgroundHintColor, hintFontSize, hintPadding, hintMargin, hintAlignment);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        String stageTitle = "Adrenaline: the game!";

        HBox loginHBox = createLoginForeground();
        HBox exitHintHBox = createExitForeground();
        ImageView backgroundImageView = createBackground();

        Scene scene = toScene(backgroundImageView, exitHintHBox, loginHBox);

        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setOnCloseRequest(Event::consume);
        stage.getIcons().add(createIcon());

        stage.show();
    }
}
