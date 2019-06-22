package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.view.remote.status.RemotePowerUp;
import it.polimi.ingsw.view.remote.status.RemoteWeapon;

import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardPrint {

    private final double INACTIVE_SAT_VALUE = -1.0; //ranging from -1.0 to +1.0, no effect by applying 0.0

    public ImageView getSourceImage (RemoteWeapon weapon) {    //given a remoteWeapon, return its image

        String path = weapon.getName().replaceAll("\\s","");    //deleting spaces
        path = path.substring(0, 1).toLowerCase() + path.substring(1);  //turning to camelCase by lowering the first character

        path = System.getProperty("user.dir") +     //getting the right path
                File.separator + "src" +
                File.separator + "gui" +
                File.separator + "png" +
                File.separator + "decks" +
                File.separator + "weapons" +
                File.separator + path + ".png";

        ImageView image = new ImageView(new Image(path));

        if(!weapon.isLoaded()) {    //on unloaded weapon, desaturates its source image

            ColorAdjust color = new ColorAdjust();
            color.setSaturation(INACTIVE_SAT_VALUE);
            image.setEffect(color);
        }

        return image;
    }

    public ImageView getSourceImage(RemotePowerUp powerUp) {    //given a remotePowerUp, return its image

        String path = System.getProperty("user.dir") +     //getting the right path
                File.separator + "src" +
                File.separator + "gui" +
                File.separator + "png" +
                File.separator + "decks" +
                File.separator + "powerUps";

        switch (powerUp.getType()) {
            case SCOPE:
                path += File.separator + "scope";
            case NEWTON:
                path += File.separator + "newton";
            case TELEPORT:
                path += File.separator + "teleport";
            case GRENADE:
                path += File.separator + "grenade";
        }

        path += "_" + powerUp.getColorCube().toLowerCase() + ".png";

        return new ImageView(new Image(path));
    }

    public ImageView getSourceImage (int red, int yellow, int blue, boolean includesPowerUp) { //given the content af an ammocell, return its image

        StringBuilder path = new StringBuilder(System.getProperty("user.dir") +     //getting the right path
                File.separator + "src" +
                File.separator + "gui" +
                File.separator + "png" +
                File.separator + "decks" +
                File.separator + "ammoTiles" +
                File.separator + "ammoTile_");

        //adjusting the path to get the right image
        path.append("R".repeat(Math.max(0, red)));
        path.append("Y".repeat(Math.max(0, yellow)));
        path.append("B".repeat(Math.max(0, blue)));

        if(includesPowerUp)
            path.append("_P");

        path.append(".png");

        return new ImageView(new Image(path.toString()));
    }
}
