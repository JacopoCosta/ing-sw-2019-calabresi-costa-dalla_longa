package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.GraphicalInterface;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GUI extends Application implements GraphicalInterface {
    private enum Layout {
        LOGIN_LAYOUT,
        LOBBY_SELECTION_LAYOUT
    }

    private Layout currentLayout;

    //base components
    private Stage stage;
    private StackPane baseLayout;

    //login components
    private VBox loginVBox;
    private HBox errorLabelBox;
    private TextField nicknameTextField;

    private ObservableList<String> lobbies = FXCollections.observableList(FXCollections.observableArrayList());

    private static CommunicationHandler communicationHandler;
    private ScheduledFuture<?> futureUpdate;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

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
            //startLobbyUpdate();
            stage.getScene().setRoot(createLobbySelectLayout());
        } catch (ConnectionException e) {
            fatalErrorRoutine(e.getMessage());
        } catch (ClientAlreadyRegisteredException e) {
            errorLabelRoutine(e.getMessage());
        }
    }

    private void handleRegistering(String nickname) {
        String errorMessage = "Invalid nickname";

        if (nickname == null || nickname.isEmpty() || nickname.isBlank()) {
            nicknameTextField.setText(null);
            errorLabelRoutine(errorMessage);
        } else
            register(nickname);
    }

    //update the Lobby list and print them
    private void startLobbyUpdate() {
        final int UPDATE_REQUEST_PERIOD = 5;

        Runnable updateTask = () -> {

            Map<String, String> lobbyInfo;

            try {
                lobbyInfo = communicationHandler.requestUpdate();
            } catch (ConnectionException e) {
                fatalErrorRoutine(e.getMessage());
                return;
            }

            Platform.runLater(() -> {
                lobbies.clear();
                lobbies.addAll(lobbyInfo.entrySet().stream().map(entry -> entry.getValue() + " " + entry.getKey()).collect(Collectors.toList()));
            });
        };
        futureUpdate = executor.scheduleAtFixedRate(updateTask, 0, UPDATE_REQUEST_PERIOD, TimeUnit.SECONDS);
    }

    //stops the update and print process
    private void stopLobbyUpdate() {
        if (!futureUpdate.isDone()) {
            futureUpdate.cancel(true);
            executor.shutdown();
        }
    }

    private HBox createLoginForeground() {
        String loginButtonText = "LOGIN";
        String nicknameTextFieldHint = "Choose a nickname";

        double loginBoxSpacing = 10;

        //TextField to input the username
        nicknameTextField = new TextField();
        nicknameTextField.setPromptText(nicknameTextFieldHint);
        nicknameTextField.setFocusTraversable(false);
        nicknameTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String nickname = nicknameTextField.getText();
                handleRegistering(nickname);
                event.consume();
            }
        });

        //login Button
        Button loginButton = new Button(loginButtonText);
        loginButton.getStylesheets().add("/css/button.css");
        loginButton.setOnAction(actionEvent -> {
            String nickname = nicknameTextField.getText();
            handleRegistering(nickname);
            actionEvent.consume();
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
        HBox exitHintHBox = createExitForeground();
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


        //lobby selection components
        ListView<String> lobbyStatusListView = new ListView<>(lobbies);
        lobbyStatusListView.setOrientation(Orientation.VERTICAL);
        lobbyStatusListView.setCellFactory(param -> new LobbyCell());
        lobbyStatusListView.prefHeightProperty().bind(stage.heightProperty());
        Image image = new Image("/png/backgrounds/list_item_bg.png");
        lobbyStatusListView.setPrefWidth(image.getWidth() / 2 + 2);
        lobbyStatusListView.getStylesheets().add("/css/list_view_bg.css");

        Button addButton = new Button("Add");
        addButton.setOnAction(actionEvent -> {
            lobbies.add("test lobby cell");
            actionEvent.consume();
        });
        VBox vBox = new VBox();
        vBox.getChildren().add(lobbyStatusListView);


        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(addButton);
        borderPane.setRight(vBox);
        borderPane.setPadding(new Insets(20));

        baseLayout.getChildren().add(borderPane);
        StackPane.setAlignment(borderPane, Pos.CENTER);
        baseLayout.requestFocus();
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
            if (currentLayout.equals(Layout.LOBBY_SELECTION_LAYOUT)) {
                //stopLobbyUpdate();
                try {
                    communicationHandler.unregister();
                } catch (ConnectionException | ClientNotRegisteredException e) {
                    fatalErrorRoutine(e.getMessage());
                }
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

        Font.loadFont(GUI.class.getResource("/fonts/Gravedigger.otf").toExternalForm(), 10);

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
