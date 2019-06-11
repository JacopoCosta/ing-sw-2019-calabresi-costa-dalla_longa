package it.polimi.ingsw.view.remote.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Palette {
    public static final Color LABEL_BACKGROUND = new Color(34.0 / 255, 32.0 / 255, 43.0 / 255, 0.8);
    public static final Color OPTION_TEXT_COLOR = new Color(189.0 / 255, 103.0 / 255, 56.0 / 255, 1);
    public static final Color ERROR_TEXT_COLOR = new Color(208.0 / 255, 81.0 / 255, 67.0 / 255, 1);

    public static HBox labelBox(String text, Color textColor, Color backgroundColor, double fontSize, double padding, double margin, Pos alignment) {
        //quit hint labelBox
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

        if (alignment != null)
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
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
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
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        return alert;
    }

    public static Background background(String imagePath) {
        Image image = new Image(imagePath);
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        return new Background(backgroundImage);
    }
}
