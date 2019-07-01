package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.view.remote.status.RemotePlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class Token {

    private static final double radius = 20.0;      //TODO: these values are probably wrong
    private static final double dropHeight = 35.0;
    private static final double dropWidth = 20.0;
    private static final double dropOffset = 25.0;

    private Color color;
    private String dropPath;
    private String inventoryPath;

    public Token(int choice) {
        switch (choice) {
            case 0:
                this.color = Color.rgb(60, 170, 40, 1);
                this.dropPath = "/gui/png/icons.drops/drop_green.png";
                this.inventoryPath = "/gui/png/inventory/inventory_green";
                break;

            case 1:
                this.color = Color.rgb(80, 100, 110, 1);
                this.dropPath = "/gui/png/icons.drops/drop_gray.png";
                this.inventoryPath = "/gui/png/inventory/inventory_gray";
                break;

            case 2:
                this.color = Color.rgb(10, 190, 230, 1);
                this.dropPath = "/gui/png/icons.drops/drop_teal.png";
                this.inventoryPath = "/gui/png/inventory/inventory_teal";
                break;

            case 3:
                this.color = Color.rgb(210, 0, 240, 1);
                this.dropPath = "/gui/png/icons.drops/drop_violet.png";
                this.inventoryPath = "/gui/png/inventory/inventory_violet";
                break;

            case 4:
                this.color = Color.rgb(230, 180, 0, 1);
                this.dropPath = "/gui/png/icons.drops/drop_yellow.png";
                this.inventoryPath = "/gui/png/inventory/inventory_yellow";
                break;

            default:    //TODO: this will probably get removed
                this.color = Color.rgb(0, 0, 0, 0.5);
                this.dropPath = "/gui/png/icons.drops/drop_desaturated.png";
                this.inventoryPath = "";
                break;
        }
    }

    public Pane inventoryGenerate(List<RemotePlayer> damagers, List<RemotePlayer> markers, boolean onFrenzy) {

        ImageView inventoryBoard;
        Pane inventory;

        if(onFrenzy)
            inventoryBoard = new ImageView(new Image(inventoryPath + "_1.png"));
        else
            inventoryBoard = new ImageView(new Image(inventoryPath + "_0.png"));

        inventory = new StackPane();
        inventory.getChildren().add(inventoryBoard);

        HBox damageDrops = new HBox();
        HBox markerDrops = new HBox();

        damageDrops.setSpacing(dropOffset);
        markerDrops.setSpacing(0.0);

        for(int i=0; i<damagers.size(); i++) {
            damageDrops.getChildren().add(damagers.get(i).getToken().dropPrint());
        }

        for(int i=0; i<markers.size(); i++) {
            damageDrops.getChildren().add(markers.get(i).getToken().dropPrint());
        }

        inventory.getChildren().add(damageDrops);
        inventory.getChildren().add(markerDrops);
        //TODO: set their position

        return inventory;
    }

    public ImageView dropPrint() {
        return new ImageView(new Image(dropPath));
    }

    public Pane tokenPrint() {
        return new StackPane(new Circle(radius, color));
    }

    public Color getColor() {
        return color;
    }

    public String getInventoryPath(boolean onFrenzy) {
        return inventoryPath + (onFrenzy ? "_1.png" : "_0.png");
    }
}
