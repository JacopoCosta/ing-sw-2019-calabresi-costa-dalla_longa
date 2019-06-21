package it.polimi.ingsw.view.remote.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class LobbyCell extends ListCell<String> {
    //private StackPane stackPane;

    private Label label;
    private HBox labelBox;

    public LobbyCell() {
        super();
        setBackground(Palette.backgroundImage("/png/backgrounds/list_item_bg.png"));

        //participants and lobby name label
        label = new Label();
        label.setFont(new Font(20));
        label.setTextFill(Palette.OPTION_TEXT_COLOR);
        label.setPadding(new Insets(10));

        //join button
        Button joinButton = new Button("JOIN");
        joinButton.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

        //spacer
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setHgrow(joinButton, Priority.ALWAYS);

        //cell
        HBox.setMargin(label, new Insets(10, 10, 0, 0));
        labelBox = new HBox(label, spacer, joinButton);
        labelBox.setAlignment(Pos.CENTER_LEFT);
}

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setPrefHeight(0D);
            setGraphic(null);
        } else {

            Image image = new Image("/png/backgrounds/list_item_bg.png");
            setPrefHeight(image.getHeight()/2);
            setPrefWidth(image.getWidth()/2);

            label.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            setGraphic(labelBox);
        }
    }
}
