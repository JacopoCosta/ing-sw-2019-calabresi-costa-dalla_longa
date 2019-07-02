package it.polimi.ingsw.view.remote.gui;

import it.polimi.ingsw.view.remote.status.*;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public abstract class CardPrint {

    private static final double INACTIVE_SAT_VALUE = -0.55; //ranging from -1.0 to +1.0, no effect by applying 0.0
    private static final double INACTIVE_BRIGHTNESS_VALUE = -0.7;

    private static final double HORIZONTAL_AMMO_GAP = 2.0;
    private static final double VERTICAL_AMMO_GAP = 2.0;

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

    private static Pane getCellsContent() { //works both for ammocell and shops

        GridPane ammoPane = new GridPane();
        //ammoPane.setHgap(HORIZONTAL_AMMO_GAP);
        //ammoPane.setVgap(VERTICAL_AMMO_GAP);
        ammoPane.setBackground(Background.EMPTY);

        double cellHeight = new Image(RemoteBoard.getBoardImage()).getHeight() / RemoteBoard.getHeight();   //needed for spacing calc
        double cellWidth = new Image(RemoteBoard.getBoardImage()).getWidth() / RemoteBoard.getWidth();

        for(int h=0; h<RemoteBoard.getHeight(); h++) {

            for(int w=0; w<RemoteBoard.getWidth(); w++) {

                RemoteCell cell = RemoteBoard.getCells().get(h*RemoteBoard.getWidth() + w); //gets the right cell

                Pane content = getCellImage(cell);
                content.setTranslateX(0.0); //not sure about these
                content.setTranslateY(0.0);
                content.relocate(w*cellWidth - content.getWidth(), h*cellHeight - content.getHeight());

                ammoPane.getChildren().add(content);
            }
        }

        return ammoPane;
    }

    public static Pane getPlayersOnMap() {

        Pane playersPane = new StackPane();
        List<RemotePlayer> participants = RemoteBoard.getParticipants();

        //TODO: finish this

        return playersPane;
    }

    public static StackPane getPlayerBoard(RemotePlayer player) {

        StackPane playerBoard = new StackPane(); //main pane
        HBox damageBox = new HBox();        //pane containing damage tokens
        HBox markingBox = new HBox();       //pane containing marking tokens
        HBox redAmmoBox = new HBox();
        HBox yellowAmmoBox = new HBox();
        HBox blueAmmoBox = new HBox();
        VBox ammoBox = new VBox();

        //load base image on the main pane
        playerBoard.getChildren().add(new ImageView(new Image(player.getToken().getInventoryPath(player.isOnFrenzy()))));

        //adding ammo
        for(int i=0; i<player.getRedAmmo(); i++)
            redAmmoBox.getChildren().add(getAmmoCube(Color.RED));

        for(int i=0; i<player.getYellowAmmo(); i++)
            yellowAmmoBox.getChildren().add(getAmmoCube(Color.YELLOW));

        for(int i=0; i<player.getBlueAmmo(); i++)
            blueAmmoBox.getChildren().add(getAmmoCube(Color.BLUE));

        redAmmoBox.setSpacing(4.0);
        yellowAmmoBox.setSpacing(4.0);
        blueAmmoBox.setSpacing(4.0);

        //collecting all ammo in an ammoBox
        ammoBox.getChildren().add(redAmmoBox);
        ammoBox.getChildren().add(yellowAmmoBox);
        ammoBox.getChildren().add(blueAmmoBox);
        ammoBox.setSpacing(5.0);

        playerBoard.getChildren().add(ammoBox);

        //loading damage drops
        for(String s: player.getDamage())
            damageBox.getChildren().add(RemoteBoard.getPlayerByName(s).getToken().dropPrint());    //will never produce NPE
        playerBoard.getChildren().add(damageBox);

        //loading marking drops
        for(String s: player.getMarkings())
            markingBox.getChildren().add(RemoteBoard.getPlayerByName(s).getToken().dropPrint());   //will never produce NPE
        playerBoard.getChildren().add(markingBox);

        damageBox.setSpacing(-80.6);
        markingBox.setSpacing(-120.0);

        damageBox.setTranslateX(55.5);
        damageBox.setTranslateY(3.0);
        markingBox.setTranslateX(450.0);
        markingBox.setTranslateY(-92.0);
        ammoBox.setTranslateX(900.0);   //TODO: these are hardcoded values
        ammoBox.setTranslateY(100.0);
        return playerBoard;
    }

    public static Rectangle getAmmoCube(Color color) {

        Rectangle ammoCube = new Rectangle();
        ammoCube.setHeight(40.0);
        ammoCube.setWidth(40.0);

        ammoCube.setFill(color);

        return ammoCube;
    }

}
