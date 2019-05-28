package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LoginManagement {
    private CommunicationHandler communicationHandler;

    private static final List<String> lobbies = Collections.synchronizedList(new ArrayList<>());

    private static Runnable updateTask;
    private static ScheduledExecutorService executor;
    private static ScheduledFuture<?> futureUpdate;
    private static final int UPDATE_REQUEST_PERIOD = 5;


    public LoginManagement(CommunicationHandler communicationHandler) {
        this.communicationHandler = communicationHandler;
    }

    private void requestUpdate() {
        updateTask = () -> {

            Map<String, String> lobbyInfo;

            try {
                lobbyInfo = communicationHandler.requestUpdate();
            } catch (ConnectionException e) {
                System.exit(-1);
                return;
            }
            synchronized (lobbies) {
                lobbies.clear();
                lobbies.addAll(lobbyInfo.entrySet().stream().map(l -> l.getKey() + " " + l.getValue()).collect(Collectors.toList()));

                choiceBox.getItems().clear();
                choiceBox.getItems().add(0, "<Select a lobby>");
                choiceBox.getItems().addAll(lobbies);
            }
        };
        executor = Executors.newSingleThreadScheduledExecutor();
        futureUpdate = executor.scheduleAtFixedRate(updateTask, 0, UPDATE_REQUEST_PERIOD, TimeUnit.SECONDS);
    }

    private void stopUpdate() {
        if (!futureUpdate.isDone()) {
            futureUpdate.cancel(true);
            executor.shutdown();
        }
    }

    private ChoiceBox<String> choiceBox = new ChoiceBox<>();

    public void display() {

        requestUpdate();

        VBox layout = new VBox();

        Button newLobby = new Button("Create new");
        Button ok = new Button("Ok");

        Scene scene = new Scene(layout);
        Stage window = new Stage();

        layout.getChildren().addAll(choiceBox, ok, newLobby);
        window.setTitle("Lobbies choice");
        window.setScene(scene);
        window.setWidth(800);
        window.setHeight(300);
        window.initModality(Modality.APPLICATION_MODAL);

        ok.setOnAction(event -> {
            stopUpdate();
            //getChoice(choiceBox); DEBUG ONLY

            String lobbyName = choiceBox.getValue();
            if (!lobbyName.equals("<Select a lobby>")) {

                lobbyName = lobbyName.substring(0, lobbyName.length() - 6);
                String lobbyPassword = "password";

                try {
                    communicationHandler.login(lobbyName, lobbyPassword);
                } catch (ConnectionException | LobbyNotFoundException | LobbyFullException | InvalidPasswordException e) {
                    System.exit(-1);
                }
            }

        });

        newLobby.setOnAction(event -> {
            stopUpdate();

            String lobbyName = communicationHandler.getUsername() + "'s lobby";
            String lobbyPassword = "password";  //hardcoded for now

            try {
                communicationHandler.initLobby(lobbyName, lobbyPassword);
            } catch (ConnectionException e) {
                System.exit(-1);
            } catch (LobbyAlreadyExistsException e) {
            }
        });

        window.showAndWait();
    }

    private static void getChoice(ChoiceBox<String> choiceBox) {
        System.out.println(choiceBox.getValue());
    }
}
