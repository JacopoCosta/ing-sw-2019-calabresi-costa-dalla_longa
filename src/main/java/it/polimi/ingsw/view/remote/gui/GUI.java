package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ClientAlreadyRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ClientNotRegisteredException;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.exceptions.LobbyAlreadyExistsException;
import it.polimi.ingsw.view.remote.GraphicalInterface;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GUI extends Application implements GraphicalInterface {
    static CommunicationHandler communicationHandler;

    enum Layout {
        LOGIN_LAYOUT,
        LOBBY_SELECTION_LAYOUT,
        GAME_STAGE_LAYOUT
    }

    static Layout currentLayout;

    //base components
    private Stage stage;
    static BorderPane foregroundLayout;
    static StackPane baseLayout;

    //login components
    private VBox loginVBox;
    private HBox errorLabelBox;

    //lobby creation components
    private Button createLobbyButton;
    static BorderPane lobbySelectionLayout;

    private static ObservableList<String> lobbies = FXCollections.observableList(FXCollections.observableArrayList());

    static ScheduledFuture<?> futureUpdate;
    static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static final int UPDATE_REQUEST_PERIOD = 5;

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

    private boolean isInvalid(String string) {
        return string == null || string.isEmpty() || string.isBlank();
    }

    private void register(String username) {
        try {
            GUI.communicationHandler.register(username);
            startLobbyUpdate();
            createLobbySelectionLayout();
        } catch (ConnectionException e) {
            fatalErrorRoutine(e.getMessage());
        } catch (ClientAlreadyRegisteredException e) {
            errorLabelRoutine(e.getMessage());
        }
    }

    private void createLobby(String lobbyName, String password) {
        try {
            GUI.communicationHandler.initLobby(lobbyName, password);
            createGameLayout();
        } catch (ConnectionException e) {
            fatalErrorRoutine(e.getMessage());
        } catch (LobbyAlreadyExistsException e) {
            errorLabelRoutine(e.getMessage());
        }
    }

    private void handleRegistering(String nickname) {
        if (isInvalid(nickname)) {
            errorLabelRoutine(Palette.INVALID_NICKNAME_TEXT);
        } else
            register(nickname);
    }

    private void handleLobbyCreation(String lobbyName, String lobbyPassword) {
        if (isInvalid(lobbyName))
            errorLabelRoutine("invalid lobby name");
        else
            createLobby(lobbyName, lobbyPassword);
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
        TextField nicknameTextField = Palette.textField();
        nicknameTextField.setPromptText(Palette.CHOOSE_NICKNAME_TEXT);
        nicknameTextField.setFocusTraversable(false);
        nicknameTextField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (currentLayout.equals(Layout.LOGIN_LAYOUT)) {
                    String nickname = nicknameTextField.getText();
                    handleRegistering(nickname);
                }
            }
        });

        //login Button
        Button loginButton = Palette.button(Palette.LOGIN_TEXT);
        //disable login button until user inputs something
        loginButton.disableProperty().bind(Bindings.isEmpty(nicknameTextField.textProperty()));
        loginButton.setOnAction(actionEvent -> {
            actionEvent.consume();
            String nickname = nicknameTextField.getText();
            nicknameTextField.clear();
            Platform.runLater(nicknameTextField::requestFocus);
            handleRegistering(nickname);
        });

        //foreground HBox
        HBox loginHBox = new HBox();
        loginHBox.setSpacing(Palette.DEFAULT_SPACING);
        loginHBox.setAlignment(Pos.CENTER);
        loginHBox.getChildren().addAll(nicknameTextField, loginButton);

        return loginHBox;
    }

    private HBox createExitButtonBox() {
        Button exitButton = Palette.button(Palette.QUIT_TEXT);
        exitButton.setOnAction(event -> exitRoutine());

        HBox exitHBox = new HBox(exitButton);
        HBox.setMargin(exitButton, Palette.MEDIUM_SQUARED_MARGIN);
        exitHBox.setAlignment(Pos.BOTTOM_LEFT);

        return exitHBox;
    }

    //creates a hidden error labelBox
    private HBox createLoginErrorHBox() {
        HBox hBox = Palette.labelBox(null, Palette.ADRENALINE_RED);
        hBox.setVisible(false);
        return hBox;
    }

    private Parent createLoginLayout() {
        currentLayout = Layout.LOGIN_LAYOUT;

        HBox loginHBox = createLoginForeground();
        HBox exitButtonBox = createExitButtonBox();
        errorLabelBox = createLoginErrorHBox();

        loginVBox = new VBox();
        loginVBox.setAlignment(Pos.CENTER);
        loginVBox.getChildren().addAll(loginHBox, errorLabelBox, exitButtonBox);

        foregroundLayout = new BorderPane();
        foregroundLayout.setCenter(loginVBox);
        foregroundLayout.setBottom(exitButtonBox);

        baseLayout = new StackPane(foregroundLayout);
        baseLayout.setBackground(Palette.background(Palette.LOGIN_BACKGROUND_IMAGE));

        Platform.runLater(() -> baseLayout.requestFocus());
        return baseLayout;
    }

    private GridPane createLobbyCreationContainer() {
        GridPane lobbyCreationContainer = new GridPane();

        //name label
        Label lobbyNameLabel = Palette.label(Palette.NAME_TEXT, Color.WHITE, Color.TRANSPARENT);

        //password label
        Label passwordLabel = Palette.label(Palette.PASSWORD_TEXT, Color.WHITE, Color.TRANSPARENT);

        //name text field
        TextField inputLobbyName = Palette.textFieldAlt();

        //password text field
        TextField inputLobbyPassword = Palette.textFieldAlt();

        //create & join button
        Button createAndJoinButton = Palette.buttonAlt(Palette.CREATE_JOIN_TEXT);
        createAndJoinButton.setOnAction(event -> {
            String lobbyName = inputLobbyName.getText();
            String lobbyPassword = inputLobbyPassword.getText();

            inputLobbyName.clear();
            inputLobbyPassword.clear();
            Platform.runLater(inputLobbyName::requestFocus);

            handleLobbyCreation(lobbyName, lobbyPassword);
        });
        createAndJoinButton.disableProperty().bind(Bindings.isEmpty(inputLobbyName.textProperty()));

        //cancel button
        Button cancelButton = Palette.buttonAlt(Palette.CANCEL_TEXT);
        cancelButton.setOnAction(event -> {
            lobbyCreationContainer.setVisible(false);
            inputLobbyName.clear();
            inputLobbyPassword.clear();
            createLobbyButton.setDisable(false);
        });

        //error label box
        errorLabelBox.setVisible(false);
        ((Label) errorLabelBox.getChildren().get(0)).setBackground(Palette.backgroundColor(Color.TRANSPARENT));

        //base container layout
        GridPane.setMargin(createAndJoinButton, Palette.LARGE_TOP_RIGHT_MARGIN);
        GridPane.setMargin(cancelButton, Palette.LARGE_TOP_RIGHT_MARGIN);
        GridPane.setMargin(errorLabelBox, Palette.LARGE_TOP_RIGHT_MARGIN);
        lobbyCreationContainer.addRow(0, lobbyNameLabel, inputLobbyName);
        lobbyCreationContainer.addRow(1, passwordLabel, inputLobbyPassword);
        lobbyCreationContainer.add(errorLabelBox, 1, 2);
        lobbyCreationContainer.addRow(3, createAndJoinButton, cancelButton);
        lobbyCreationContainer.setHgap(Palette.DEFAULT_SPACING);
        lobbyCreationContainer.setVgap(Palette.DEFAULT_SPACING);
        lobbyCreationContainer.setBackground(Palette.backgroundColor(Palette.ADRENALINE_DARK_GRAY_FILL));
        lobbyCreationContainer.setPadding(Palette.DEFAULT_SQUARED_PADDING);
        lobbyCreationContainer.setVisible(false);

        return lobbyCreationContainer;
    }

    private ListView<String> createLobbyListView() {
        ListView<String> listView = Palette.listView(lobbies);
        listView.setCellFactory(param -> new LobbyCell());
        listView.prefHeightProperty().bind(stage.heightProperty());
        return listView;
    }

    private void createLobbySelectionLayout() {
        currentLayout = Layout.LOBBY_SELECTION_LAYOUT;
        GridPane lobbyCreationContainer = createLobbyCreationContainer();

        baseLayout.setBackground(Palette.background(Palette.LOBBY_SELECTION_BACKGROUND_IMAGE));
        foregroundLayout.getChildren().remove(loginVBox);

        //available lobbies list
        ListView<String> lobbyStatusListView = createLobbyListView();

        //lobbies list container
        VBox lobbyListVBox = new VBox();
        lobbyListVBox.getChildren().add(lobbyStatusListView);

        //lobby creation button
        createLobbyButton = Palette.buttonAlt(Palette.CREATE_LOBBY_TEXT);
        createLobbyButton.setOnAction(event -> {
            lobbyCreationContainer.setVisible(true);
            createLobbyButton.setDisable(true);
        });

        //welcome text
        Label welcomeLabel = Palette.label(Palette.WELCOME_TEXT, Palette.ADRENALINE_DARK_GRAY_FILL, Color.TRANSPARENT);

        //username label text
        Label usernameLabel = Palette.label(communicationHandler.getUsername(), Palette.ADRENALINE_ORANGE, Color.TRANSPARENT);

        //welcome text container
        HBox welcomeHBox = new HBox();
        welcomeHBox.getChildren().addAll(welcomeLabel, usernameLabel);

        //left box container
        VBox leftVBox = new VBox();
        leftVBox.getChildren().addAll(welcomeHBox, createLobbyButton, lobbyCreationContainer);
        VBox.setMargin(welcomeHBox, Palette.MEDIUM_SQUARED_MARGIN);
        VBox.setMargin(createLobbyButton, Palette.MEDIUM_SQUARED_MARGIN);

        //foreground pane
        lobbySelectionLayout = new BorderPane();
        lobbySelectionLayout.setRight(lobbyListVBox);
        lobbySelectionLayout.setLeft(leftVBox);
        lobbySelectionLayout.setPadding(Palette.MEDIUM_SQUARED_PADDING);

        foregroundLayout.setCenter(lobbySelectionLayout);
        Platform.runLater(() -> baseLayout.requestFocus());
    }

    private void createGameLayout() {
        foregroundLayout.getChildren().remove(lobbySelectionLayout);
        GameStage gameStage = new GameStage(baseLayout, foregroundLayout, communicationHandler);
        gameStage.display();
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
        Optional<ButtonType> result = Palette.confirmationDialog(Palette.TITLE_TEXT, null, Palette.EXIT_MESSAGE_TEXT, stage).showAndWait();

        if (result.isPresent() && result.get().getButtonData().equals(ButtonType.OK.getButtonData())) {
            if (currentLayout.equals(Layout.LOBBY_SELECTION_LAYOUT)) {
                stopLobbyUpdate();
                try {
                    communicationHandler.unregister();
                } catch (ConnectionException | ClientNotRegisteredException e) {
                    fatalErrorRoutine(e.getMessage());
                }
            } else if (currentLayout.equals(Layout.GAME_STAGE_LAYOUT)) {
                try {
                    communicationHandler.logout();
                } catch (ConnectionException e) {
                    fatalErrorRoutine(e.getMessage());
                }
            }
            stage.close();
            System.exit(0);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(createLoginLayout());

        stage = primaryStage;
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
