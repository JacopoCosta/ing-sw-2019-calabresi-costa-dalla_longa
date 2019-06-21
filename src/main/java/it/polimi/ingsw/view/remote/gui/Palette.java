package it.polimi.ingsw.view.remote.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

class Palette {
    //colors
    static final Color ADRENALINE_DARK_GRAY = new Color(13.0 / 255, 22.0 / 255, 25.0 / 255, 0.8);
    static final Color ADRENALINE_ORANGE = new Color(189.0 / 255, 103.0 / 255, 56.0 / 255, 1);
    static final Color ADRENALINE_RED = new Color(208.0 / 255, 81.0 / 255, 67.0 / 255, 1);

    //images
    static final String LIST_ITEM_BACKGROUND_IMAGEPATH = "/gui/png/backgrounds/list_item_bg.png";
    static final String LOGIN_BACKGROUND_IMAGEPATH = "/gui/png/backgrounds/login_bg.png";
    static final String LOBBY_SELECTION_BACKGROUND_IMAGEPATH = "/gui/png/backgrounds/lobby_selection_bg.png";
    static final String ADRENALINE_LOGO_IMAGEPATH = "/gui/png/logo/logo.png";
    static final String ERROR_ICON = "/gui/png/error/error.png";

    //css stylesheets
    static final String BUTTON_STYLESHEET = "/gui/css/button.css";
    static final String BUTTON_REVERSE_STYLESHEET = "/gui/css/button_reverse.css";
    static final String LIST_VIEW_STYLESHEET = "/gui/css/list_view.css";
    static final String TEXT_FIELD_STYLESHEET = "/gui/css/text_field.css";
    static final String DIALOG_STYLESHEET = "/gui/css/dialog.css";

    //font sizes
    static final double DEFAULT_FONT_SIZE = 20.0;

    //fonts
    static final Font DEFAULT_FONT = Font.loadFont(Palette.class.getResource(Palette.DEFAULT_FONT_PATH).toExternalForm(), DEFAULT_FONT_SIZE);

    //font paths
    static final String DEFAULT_FONT_PATH = "/gui/fonts/orbitron.otf";

    //padding sizes
    static final double DEFAULT_PADDING_SIZE = 10.0;
    static final double LARGE_PADDING_SIZE = 20.0;

    //paddings
    static final Insets DEFAULT_PADDING = new Insets(DEFAULT_PADDING_SIZE);
    static final Insets ENLARGED_PADDING = new Insets(LARGE_PADDING_SIZE);

    //margin sizes
    static final double DEFAULT_MARGIN_SIZE = 10.0;
    static final double LARGE_MARGIN_SIZE = 30.0;
    static final double MEDIUM_MARGIN_SIZE = 20.0;

    //margins
    static final Insets DEFAULT_MARGIN = new Insets(DEFAULT_MARGIN_SIZE);
    static final Insets LARGE_STRETCH_MARGIN = new Insets(0.0, LARGE_MARGIN_SIZE, 0.0, 0.0);
    static final Insets MEDIUM_STRETCH_MARGIN = new Insets(0.0, 0.0, 0.0, MEDIUM_MARGIN_SIZE);

    //dimensions
    static final double LIST_VIEW_ITEM_HEIGHT = new Image(LIST_ITEM_BACKGROUND_IMAGEPATH).getHeight();
    static final double LIST_VIEW_ITEM_WIDTH = new Image(LIST_ITEM_BACKGROUND_IMAGEPATH).getWidth();
    static final double ICON_FIT_WIDTH = 64.0;
    static final double ICON_FIT_HEIGHT = 64.0;

    //spacings
    static final double DEFAULT_SPACING = 10.0;

    //strings
    static final String JOIN_TEXT = "JOIN";
    static final String LOGIN_TEXT = "LOGIN";
    static final String OK_TEXT = "OK";
    static final String CANCEL_TEXT = "CANCEL";
    static final String QUIT_TEXT = "[ESC] quit";
    static final String INVALID_NICKNAME_TEXT = "Invalid nickname";
    static final String CHOOSE_NICKNAME_TEXT = "Choose a nickname";
    static final String ERROR_TITLE_TEXT = "Error";
    static final String GENERIC_ERROR_TEXT = "An error occurred";
    static final String TITLE_TEXT = "Adrenaline: the game!";
    static final String EXIT_MESSAGE_TEXT = "Exit Adrenaline?";
    static final String PASSWORD_TEXT = "Password";

    //timeouts
    static final double DEFAULT_TIMEOUT = 3.0;

    static HBox labelBox(String text, Color textColor, Color backgroundColor, Font font, Insets padding, Insets margin, Pos alignment) {
        //quit hint labelBox
        Label label = new Label(text);

        if (font != null)
            label.setFont(font);

        if (textColor != null)
            label.setTextFill(textColor);

        if (padding != null)
            label.setPadding(padding);

        if (backgroundColor != null)
            label.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox hbox = new HBox();

        if (margin != null)
            HBox.setMargin(label, margin);

        hbox.getChildren().add(label);

        if (alignment != null)
            hbox.setAlignment(alignment);
        return hbox;
    }

    static Dialog<ButtonType> confirmationDialog(String title, String headerText, String contentText, Stage owner) {
        Dialog<ButtonType> dialog = new Dialog<>();

        ButtonType okButtonType = new ButtonType(OK_TEXT, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(CANCEL_TEXT, ButtonBar.ButtonData.CANCEL_CLOSE);

        return initDialog(dialog, okButtonType, cancelButtonType, title, headerText, contentText, owner);
    }

    static Dialog<String> passwordDialog(String title, String headerText, String contentText, Stage owner) {
        Dialog<String> dialog = new Dialog<>();

        ButtonType okButtonType = new ButtonType(JOIN_TEXT, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType(CANCEL_TEXT, ButtonBar.ButtonData.CANCEL_CLOSE);

        return initDialog(dialog, okButtonType, cancelButtonType, title, headerText, contentText, owner);
    }

    private static <T> Dialog<T> initDialog(Dialog<T> dialog, ButtonType confirmationButtonType, ButtonType cancellationButtonType,
                                     String title, String headerText, String contentText, Stage owner) {
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);


        dialog.getDialogPane().getButtonTypes().addAll(confirmationButtonType, cancellationButtonType);

        dialog.setResizable(false);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        dialog.getDialogPane().getStylesheets().add(DIALOG_STYLESHEET);

        Platform.runLater(() -> dialog.getDialogPane().getScene().getWindow().sizeToScene());
        return dialog;
    }

    static Alert errorAlert(String title, String headerText, String contentText, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.setResizable(false);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(owner);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.getDialogPane().getStylesheets().add(DIALOG_STYLESHEET);

        Image errorImage = new Image(ERROR_ICON);
        ImageView errorImageView = new ImageView(errorImage);
        errorImageView.setFitWidth(ICON_FIT_WIDTH);
        errorImageView.setFitHeight(ICON_FIT_HEIGHT);
        alert.setGraphic(errorImageView);

        Platform.runLater(() -> alert.getDialogPane().getScene().getWindow().sizeToScene());
        return alert;
    }

    static Background background(String imagePath) {
        return createBackground(imagePath, false, true);
    }

    static Background backgroundImage(String imagePath) {
        return createBackground(imagePath, true, false);
    }

    private static Background createBackground(String imagePath, boolean contain, boolean cover) {
        Image image = new Image(imagePath);
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, contain, cover);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        return new Background(backgroundImage);
    }
}
