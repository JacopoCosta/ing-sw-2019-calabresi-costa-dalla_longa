package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.view.remote.status.*;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class CardPrint {

    private final double INACTIVE_SAT_VALUE = -1.0; //ranging from -1.0 to +1.0, no effect by applying 0.0

    private final String CARD_RESOURCES_PATH = "/gui/png/decks/";

    public ImageView getSourceImage (RemoteWeapon weapon) {    //given a remoteWeapon, return its image

        String path = weapon.getName().replaceAll("\\s","");    //deleting spaces
        path = path.substring(0, 1).toLowerCase() + path.substring(1);  //turning to camelCase by lowering the first character

        path = CARD_RESOURCES_PATH + "/weapons" + path + ".png";    //getting the right path

        ImageView image = new ImageView(new Image(path));

        if(!weapon.isLoaded()) {    //on unloaded weapon, desaturate its source image

            ColorAdjust color = new ColorAdjust();
            color.setSaturation(INACTIVE_SAT_VALUE);
            image.setEffect(color);
        }

        return image;
    }

    public ImageView getSourceImage(RemotePowerUp powerUp) {    //given a remotePowerUp, return its image

        String path = CARD_RESOURCES_PATH + "powerUps/";

        switch (powerUp.getType()) {
            case SCOPE:
                path += "scope";
            case NEWTON:
                path += "newton";
            case TELEPORT:
                path += "teleport";
            case GRENADE:
                path += "grenade";
        }

        path += "_" + powerUp.getColorCube().toLowerCase() + ".png";

        return new ImageView(new Image(path));
    }

    public ImageView getSourceImage (int red, int yellow, int blue, boolean includesPowerUp) { //given the content af an ammocell, return its image

        String path = CARD_RESOURCES_PATH + "ammoTiles/ammoTile_";  //getting the right path

        //adjusting the path to get the right image
        path += ("R".repeat(Math.max(0, red)));
        path += ("Y".repeat(Math.max(0, yellow)));
        path += ("B".repeat(Math.max(0, blue)));

        if(includesPowerUp)
            path += ("_P");

        path += (".png");

        return new ImageView(new Image(path));
    }

    public Pane printBoard() {

        Pane pane = new StackPane();
        ImageView boardMap = new ImageView(new Image(RemoteBoard.getBoardImage()));

        //add the main map
        pane.getChildren().add(boardMap);

        //refresh board tokens
        /*TODO
            pane.getChildren().add(refreshAmmo)
            pane.getChildren().add(refreshPlayers)
         */

        return pane;
    }

    public Pane refreshAmmo() {

        Pane ammoPane = new StackPane();
        HBox horAmmoTiles = new HBox();
        horAmmoTiles.setSpacing(0.0);   //TODO: calculate this

        for(int h=0; h<RemoteBoard.getHeight(); h++) {

            VBox verAmmoTiles = new VBox();

            verAmmoTiles.setSpacing(0.0);   //TODO: calculate this (it's the horizontal space)
            verAmmoTiles.setBackground(Background.EMPTY);

            for(int w=0; w<RemoteBoard.getWidth(); w++) {

                RemoteCell cell = RemoteBoard.getCells().get(h*RemoteBoard.getHeight() + w*RemoteBoard.getWidth()); //gets the right cell

                //adds the correct ammo tile image
                if(cell.isAmmoCell() && cell.getRed()*cell.getYellow()*cell.getBlue() > 0)
                    verAmmoTiles.getChildren().add(getSourceImage(cell.getRed(), cell.getYellow(), cell.getBlue(), cell.includesPowerUp()));
                else
                    verAmmoTiles.getChildren().add(null);   //TODO: make it add a void image
            }

            horAmmoTiles.getChildren().add(verAmmoTiles);
        }

        return ammoPane;
    }

    public Pane refreshPlayers() {

        Pane playersPane = new StackPane();

        for(RemotePlayer player : RemoteBoard.getParticipants()) {
            //TODO: modify RemoteCell so that it contains a reference to remotePlayers (you'll need to change quite a lot of methods)
        }

        return playersPane;
    }

}
