package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.view.remote.status.RemotePlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class Token {

    private static final double DROP_SCALE_X = 0.3;
    private static final double DROP_SCALE_Y = 0.3;

    private Color color;
    private String dropPath;
    private String inventoryPath;

    public Token(int choice) {
        switch (choice) {
            case 0:
                this.color = Color.rgb(60, 170, 40, 1);
                this.dropPath = "/gui/png/icons/drops/drop_green.png";
                this.inventoryPath = "/gui/png/inventory/inventory_green";
                break;

            case 1:
                this.color = Color.rgb(230, 180, 0, 1);
                this.dropPath = "/gui/png/icons/drops/drop_yellow.png";
                this.inventoryPath = "/gui/png/inventory/inventory_yellow";
                break;

            case 2:
                this.color = Color.rgb(10, 190, 230, 1);
                this.dropPath = "/gui/png/icons/drops/drop_teal.png";
                this.inventoryPath = "/gui/png/inventory/inventory_teal";
                break;

            case 3:
                this.color = Color.rgb(210, 0, 240, 1);
                this.dropPath = "/gui/png/icons/drops/drop_violet.png";
                this.inventoryPath = "/gui/png/inventory/inventory_violet";
                break;

            case 4:
                this.color = Color.rgb(80, 100, 110, 1);
                this.dropPath = "/gui/png/icons/drops/drop_gray.png";
                this.inventoryPath = "/gui/png/inventory/inventory_gray";
                break;
        }
    }

    public ImageView dropPrint() {
        ImageView drop = new ImageView(new Image(dropPath));
        drop.setScaleX(DROP_SCALE_X);
        drop.setScaleY(DROP_SCALE_Y);
        return drop;
    }

    public Color getColor() {
        return color;
    }

    public String getInventoryPath(boolean onFrenzy) {
        return inventoryPath + (onFrenzy ? "_1.png" : "_0.png");
    }
}
