package it.polimi.ingsw.view.remote.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Palette {
    public static HBox label(String text, Color textColor, Color backgroundColor, double fontSize, double padding, double margin, Pos alignment) {
        //quit hint label
        Label label = new Label(text);

        if (fontSize > 0D)
            label.setFont(new Font(fontSize));

        if (textColor != null)
            label.setTextFill(textColor);

        if (padding > 0D)
            label.setPadding(new Insets(padding));

        if (backgroundColor != null)
            label.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox exitHintHBox = new HBox();

        if (margin >= 0D)
            HBox.setMargin(label, new Insets(margin));

        exitHintHBox.getChildren().addAll(label);
        exitHintHBox.setAlignment(alignment);
        return exitHintHBox;
    }

    public static Alert confirmationAlert(String title, String headerText, String contentText, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.setResizable(false);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(owner);
        return alert;
    }

    public static Alert errorAlert(String title, String headerText, String contentText, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.setResizable(false);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(owner);
        return alert;
    }
}
