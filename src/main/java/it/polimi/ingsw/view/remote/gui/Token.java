package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.view.remote.status.PlayerColor;
import javafx.scene.paint.Color;

public class Token {

    //TODO: connect it to RemotePlayer class

    private static final double radius = 20.0;  //TODO: this value is probably wrong

    private Color color;
    private String dropPath;
    private String inventoryPath;   //remember to add either "_0.png" or "_1.png"

    public Token(PlayerColor color) {
        switch (color) {
            case GREEN:
                this.color = Color.rgb(60, 170, 40, 1);
                this.dropPath = "/gui/png/icons.drops/drop_green.png";
                this.inventoryPath = "/gui/png/inventory/inventory_green";
                break;

            case GRAY:
                this.color = Color.rgb(80, 100, 110, 1);
                this.dropPath = "/gui/png/icons.drops/drop_gray.png";
                this.inventoryPath = "/gui/png/inventory/inventory_gray";
                break;

            case TEAL:
                this.color = Color.rgb(10, 190, 230, 1);
                this.dropPath = "/gui/png/icons.drops/drop_teal.png";
                this.inventoryPath = "/gui/png/inventory/inventory_teal";
                break;

            case VIOLET:
                this.color = Color.rgb(210, 0, 240, 1);
                this.dropPath = "/gui/png/icons.drops/drop_violet.png";
                this.inventoryPath = "/gui/png/inventory/inventory_violet";
                break;

            case YELLOW:
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


}
