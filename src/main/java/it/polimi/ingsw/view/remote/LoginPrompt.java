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

public class LoginPrompt {

    public static void display() {

        //setting nodes
        Label nickLabel = new Label("Choose your nickname:");
        Label pswLabel = new Label("Choose a password:");
        Button close = new Button("Close");
        Button done = new Button("Done");
        TextField nickText = new TextField();
        TextField pswText = new TextField();

        HBox layout = new HBox();
        layout.getChildren().addAll(nickLabel, nickText, pswLabel, pswText, close, done);
        layout.setSpacing(10);

        Scene scene = new Scene(layout);
        Stage window = new Stage();
        window.setTitle("Login");
        window.setScene(scene);
        window.setWidth(800);
        window.setHeight(300);
        window.initModality(Modality.APPLICATION_MODAL);

        //setting buttons actions
        close.setOnAction(event -> window.close());
        done.setOnAction(event -> {
            //TODO: handle entered text for login routine
        });

        window.showAndWait();
    }

}
