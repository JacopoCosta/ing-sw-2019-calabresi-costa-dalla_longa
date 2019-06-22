package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.view.remote.GraphicalInterface;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class GUI extends Application implements GraphicalInterface {
    static CommunicationHandler communicationHandler;

    private enum Layout {
        LOGIN_LAYOUT,
        LOBBY_SELECTION_LAYOUT
    }

    private Layout currentLayout;

    //base components
    private Stage stage;
    private StackPane baseLayout;

    //exit routine components
    private boolean exitMessageDisplayed;
    private Dialog<ButtonType> exitDialog;

    //login components
    private VBox loginVBox;
    private HBox errorLabelBox;
    private TextField nicknameTextField;

    private ObservableList<String> lobbies = FXCollections.observableList(FXCollections.observableArrayList());

    private ScheduledFuture<?> futureUpdate;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final int UPDATE_REQUEST_PERIOD = 5;

    public GUI() {
        exitMessageDisplayed = false;
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
            startLobbyUpdate();
            stage.getScene().setRoot(createLobbySelectLayout());
        } catch (ConnectionException e) {
            fatalErrorRoutine(e.getMessage());
        } catch (ClientAlreadyRegisteredException e) {
            errorLabelRoutine(e.getMessage());
        }
    }

    private void handleRegistering(String nickname) {
        if (nickname == null || nickname.isEmpty() || nickname.isBlank()) {
            nicknameTextField.setText(null);
            errorLabelRoutine(Palette.INVALID_NICKNAME_TEXT);
        } else
            register(nickname);
    }

    //update the Lobby list and print them
    private void startLobbyUpdate() {
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

        //TextField to input the username
        nicknameTextField = new TextField();
        nicknameTextField.getStylesheets().add(Palette.TEXT_FIELD_STYLESHEET);
        nicknameTextField.setPromptText(Palette.CHOOSE_NICKNAME_TEXT);
        nicknameTextField.setFocusTraversable(false);
        nicknameTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                String nickname = nicknameTextField.getText();
                handleRegistering(nickname);
            }
        });

        //login Button
        Button loginButton = new Button(Palette.LOGIN_TEXT);
        loginButton.getStylesheets().add(Palette.BUTTON_STYLESHEET);
        loginButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            String nickname = nicknameTextField.getText();
            handleRegistering(nickname);
        });

        //foreground HBox
        HBox loginBox = new HBox();
        loginBox.setSpacing(Palette.DEFAULT_SPACING);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getChildren().addAll(nicknameTextField, loginButton);

        return loginBox;
    }

    private HBox createExitHBox() {
        return Palette.labelBox(Palette.QUIT_TEXT, Palette.ADRENALINE_ORANGE, Palette.ADRENALINE_DARK_GRAY_TRANSPARENT,
                Palette.DEFAULT_FONT, Palette.DEFAULT_SQUARED_PADDING, Palette.DEFAULT_MARGIN, Pos.BOTTOM_LEFT);
    }

    //creates a hidden error labelBox
    private HBox createLoginErrorHBox() {
        HBox hBox = Palette.labelBox(null, Palette.ADRENALINE_RED, Palette.ADRENALINE_DARK_GRAY_TRANSPARENT,
                Palette.DEFAULT_FONT, Palette.DEFAULT_SQUARED_PADDING, Palette.DEFAULT_MARGIN, Pos.CENTER);
        hBox.setVisible(false);
        return hBox;
    }

    private Parent createLoginLayout() {
        currentLayout = Layout.LOGIN_LAYOUT;

        HBox loginHBox = createLoginForeground();
        HBox exitHintHBox = createExitHBox();
        errorLabelBox = createLoginErrorHBox();

        loginVBox = new VBox();
        loginVBox.setAlignment(Pos.CENTER);
        loginVBox.getChildren().addAll(loginHBox, errorLabelBox, exitHintHBox);

        baseLayout.setBackground(Palette.background(Palette.LOGIN_BACKGROUND_IMAGE));
        baseLayout.getChildren().addAll(exitHintHBox, loginVBox);
        return baseLayout;
    }

    private Parent createLobbySelectLayout() {
        currentLayout = Layout.LOBBY_SELECTION_LAYOUT;

        baseLayout.setBackground(Palette.background(Palette.LOBBY_SELECTION_BACKGROUND_IMAGE));
        baseLayout.getChildren().remove(loginVBox);

        //lobby selection components
        ListView<String> lobbyStatusListView = new ListView<>(lobbies);
        lobbyStatusListView.setOrientation(Orientation.VERTICAL);
        lobbyStatusListView.setCellFactory(param -> new LobbyCell());
        lobbyStatusListView.prefHeightProperty().bind(stage.heightProperty());
        lobbyStatusListView.setPrefWidth(Palette.LIST_VIEW_ITEM_WIDTH / 2 + Palette.MEDIUM_SPACING);
        lobbyStatusListView.getStylesheets().add(Palette.LIST_VIEW_STYLESHEET);

        VBox vBox = new VBox();
        vBox.getChildren().add(lobbyStatusListView);

        /*Button addButton = new Button("Add");
        addButton.setOnAction(actionEvent -> {
            lobbies.add("[1/5] test lobby cell");
            actionEvent.consume();
        });
        borderPane.setLeft(addButton);*/

        BorderPane borderPane = new BorderPane();
        borderPane.setRight(vBox);
        borderPane.setPadding(Palette.MEDIUM_SQUARED_PADDING);

        baseLayout.getChildren().add(borderPane);
        StackPane.setAlignment(borderPane, Pos.CENTER);
        baseLayout.requestFocus();
        return baseLayout;
    }

    private void fatalErrorRoutine(String errorMessage) {
        Palette.errorAlert(Palette.ERROR_TITLE_TEXT, null, errorMessage, stage).showAndWait();
        stage.close();
        System.exit(-1);
    }

    private void errorLabelRoutine(String errorMessage) {
        ((Label) errorLabelBox.getChildren().get(0)).setText(errorMessage);
        errorLabelBox.setVisible(true);

        PauseTransition visiblePause = new PauseTransition(Duration.seconds(Palette.DEFAULT_TIMEOUT));
        visiblePause.setOnFinished(event -> errorLabelBox.setVisible(false));
        Platform.runLater(visiblePause::play);
    }

    private void exitRoutine() {
        exitDialog = Palette.confirmationDialog(Palette.TITLE_TEXT, null, Palette.EXIT_MESSAGE_TEXT, stage);
        Optional<ButtonType> result = exitDialog.showAndWait();
        exitMessageDisplayed = false;

        if (result.isPresent() && result.get().getButtonData().equals(ButtonType.OK.getButtonData())) {
            if (currentLayout.equals(Layout.LOBBY_SELECTION_LAYOUT)) {
                stopLobbyUpdate();
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

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        baseLayout = new StackPane();

        Scene scene = new Scene(createLoginLayout());
        scene.addEventHandler(KeyEvent.KEY_PRESSED, t -> { //set the ESC button to exit the program
            if (t.getCode() == KeyCode.ESCAPE) {
                if(!exitMessageDisplayed) {
                    exitMessageDisplayed = true;
                    exitRoutine();
                } else {
                    exitDialog.close();
                    exitMessageDisplayed = false;
                }
            }
        });

        stage.setScene(scene);
        stage.setTitle(Palette.TITLE_TEXT);
        stage.setResizable(false);
        stage.setFullScreen(true);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setOnCloseRequest(Event::consume);
        stage.getIcons().add(Palette.ADRENALINE_LOGO_IMAGE.getImage());

        stage.show();
    }
}
