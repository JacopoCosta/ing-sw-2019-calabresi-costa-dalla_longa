package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.view.remote.status.*;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class CardPrint {

    private static final double INACTIVE_SAT_VALUE = -0.55; //ranging from -1.0 to +1.0, no effect by applying 0.0
    private static final double INACTIVE_BRIGHTNESS_VALUE = -0.7;

    private static final String CARD_RESOURCES_PATH = "/gui/png/decks/";

    public static HBox getWeaponListImage(List<RemoteWeapon> weaponList) {    //given a list of RemoteWeapon, return a HBox containing them

        HBox weaponPane = new HBox();   //main pane

        for(RemoteWeapon weapon : weaponList) {
            String path = weapon.getName().replaceAll("\\s", "");    //deleting spaces
            path = path.substring(0, 1).toLowerCase() + path.substring(1);  //turning to camelCase by lowering the first character

            path = CARD_RESOURCES_PATH + "/weapons/" + path + ".png";    //getting the right path

            ImageView weaponImageView = new ImageView(new Image(path));

            if (!weapon.isLoaded()) {    //on unloaded weapon, desaturate its source image

                ColorAdjust color = new ColorAdjust();
                color.setSaturation(INACTIVE_SAT_VALUE);
                color.setBrightness(INACTIVE_BRIGHTNESS_VALUE);
                weaponImageView.setEffect(color);
            }

            weaponPane.getChildren().add(weaponImageView);
        }

        return weaponPane;
    }

    public static Pane getPowerUpListImage(List<RemotePowerUp> powerUpList) {    //given a remotePowerUp, return its image

        Pane powerUpPane = new HBox();  //main pane

        for(RemotePowerUp pUp : powerUpList) {
            String path = CARD_RESOURCES_PATH + "powerUps/" + pUp.getType() + ("_" + pUp.getColorCube().toLowerCase() + ".png");
            powerUpPane.getChildren().add(new ImageView(new Image(path)));
        }
        return powerUpPane;
    }

    private static Pane getCellImage(RemoteCell cell) { //given the content af a cell, return an image of its content

        String path = CARD_RESOURCES_PATH;
        if(cell.isAmmoCell()) {

            if (cell.getRed() + cell.getYellow() + cell.getBlue() == 0) {
                path += "ammoTileDeck.png"; //path for void ammoTile image
            } else {
                path += "ammoTiles/ammoTile_";  //starts calculating the correct ammoTile image path

                //adjusting the path to get the right image
                path += ("R".repeat(Math.max(0, cell.getRed())));
                path += ("Y".repeat(Math.max(0, cell.getYellow())));
                path += ("B".repeat(Math.max(0, cell.getBlue())));

                if (cell.includesPowerUp())
                    path += ("_P");

                path += (".png");
            }

            return new Pane(new ImageView(new Image(path)));
        }
        else {  //cell is a shop

            return getWeaponListImage(cell.getShop());  //returns HBox
        }
    }

    public static Pane printBoard() {

        Pane pane = new StackPane();
        ImageView boardMap = new ImageView(new Image(RemoteBoard.getBoardImage()));

        //add the main map
        pane.getChildren().add(boardMap);

        //refresh board tokens

        pane.getChildren().add(getCellsContent());

        //TODO pane.getChildren().add(getPlayersOnMap)


        return pane;
    }

    public static Pane getCellsContent() { //works both for ammocell and shops

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
                if(cell.isAmmoCell()) {
                    verAmmoTiles.getChildren().add(getCellImage(cell));
                }
                else {
                    //TODO
                }
            }

            horAmmoTiles.getChildren().add(verAmmoTiles);
        }

        return ammoPane;
    }

    public static Pane getPlayersOnMap() {

        Pane playersPane = new StackPane();
        List<RemotePlayer> participants = RemoteBoard.getParticipants();

        //TODO: finish this

        return playersPane;
    }

    public static Pane getPlayerBoard(RemotePlayer player) {

        Pane playerBoard = new StackPane(); //main pane
        HBox damageBox = new HBox();        //pane containing damage tokens
        HBox markingBox = new HBox();       //pane containing marking tokens
        HBox redAmmoBox = new HBox();
        HBox yellowAmmoBox = new HBox();
        HBox blueAmmoBox = new HBox();
        VBox ammoBox = new VBox();

        //load base image on the main pane
        playerBoard.getChildren().add(new ImageView(new Image(player.getToken().getInventoryPath(player.isOnFrenzy()))));

        //loading ammo
        Rectangle ammoCube = new Rectangle();
        ammoCube.setHeight(2.0);
        ammoCube.setWidth(2.0);

        for(int i=0; i<player.getRedAmmo(); i++)
            redAmmoBox.getChildren().add(getAmmoCube(Color.RED));

        for(int i=0; i<player.getYellowAmmo(); i++)
            yellowAmmoBox.getChildren().add(getAmmoCube(Color.YELLOW));

        for(int i=0; i<player.getBlueAmmo(); i++)
            blueAmmoBox.getChildren().add(getAmmoCube(Color.BLUE));

        redAmmoBox.setSpacing(0.15);
        yellowAmmoBox.setSpacing(0.15);
        blueAmmoBox.setSpacing(0.15);

        ammoBox.getChildren().add(redAmmoBox);
        ammoBox.getChildren().add(yellowAmmoBox);
        ammoBox.getChildren().add(blueAmmoBox);
        ammoBox.setSpacing(0.2);

        //TODO: finish this
        return playerBoard;
    }

    private static Rectangle getAmmoCube(Color color) {

        Rectangle ammoCube = new Rectangle();
        ammoCube.setHeight(2.0);
        ammoCube.setWidth(2.0);

        ammoCube.setFill(color);

        return ammoCube;
    }

}
