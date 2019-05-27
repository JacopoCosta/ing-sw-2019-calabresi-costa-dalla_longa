package it.polimi.ingsw.view.remote;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import org.w3c.dom.Text;

public class LoginManagement {

    public static void display() {

        VBox layout = new VBox();

        ChoiceBox<String> choiceBox = new ChoiceBox<>();

        for(int i=0; i < 4; i++) {
            choiceBox.getItems().add("Item " + i);
        }

        Button newLobby = new Button("Create new");
        Button ok = new Button("Ok");
        Button back = new Button("Back");

        Scene scene = new Scene(layout);
        Stage window = new Stage();

        layout.getChildren().addAll(choiceBox, ok, back, newLobby);
        window.setTitle("Lobbies choice");
        window.setScene(scene);
        window.setWidth(800);
        window.setHeight(300);
        window.initModality(Modality.APPLICATION_MODAL);

        ok.setOnAction(event -> {
            getChoice(choiceBox);
        });
        back.setOnAction(event -> window.close());

        window.showAndWait();
    }

    private static void getChoice(ChoiceBox<String> choiceBox) {
        System.out.println(choiceBox.getValue());
    }
}
