package it.polimi.ingsw.view.remote.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class Palette {
    //colors
    static final Color ADRENALINE_DARK_GRAY_TRANSPARENT = Color.rgb(34, 42, 43, 0.8);
    static final Color ADRENALINE_DARK_GRAY_FILL = Color.rgb(34, 42, 43, 1);
    static final Color ADRENALINE_ORANGE = Color.rgb(189, 103, 56, 1);
    static final Color ADRENALINE_RED = Color.rgb(208, 81, 67, 1);

    //image paths
    private static final String LIST_ITEM_BACKGROUND_IMAGE_PATH = "/gui/png/backgrounds/list_item_bg.png";
    private static final String LOGIN_BACKGROUND_IMAGE_PATH = "/gui/png/backgrounds/login_bg.png";
    private static final String LOBBY_SELECTION_BACKGROUND_IMAGE_PATH = "/gui/png/backgrounds/lobby_selection_bg.png";
    private static final String ADRENALINE_LOGO_IMAGE_PATH = "/gui/png/logo/logo.png";
    private static final String ERROR_ICON_PATH = "/gui/png/error/error.png";

    //image views
    static final ImageView LIST_ITEM_BACKGROUND_IMAGE = new ImageView(new Image(LIST_ITEM_BACKGROUND_IMAGE_PATH));
    static final ImageView LOGIN_BACKGROUND_IMAGE = new ImageView(new Image(LOGIN_BACKGROUND_IMAGE_PATH));
    static final ImageView LOBBY_SELECTION_BACKGROUND_IMAGE = new ImageView(new Image(LOBBY_SELECTION_BACKGROUND_IMAGE_PATH));
    static final ImageView ADRENALINE_LOGO_IMAGE = new ImageView(new Image(ADRENALINE_LOGO_IMAGE_PATH));
    static final ImageView ERROR_ICON = new ImageView(new Image(ERROR_ICON_PATH));

    //css stylesheets
    static final String BUTTON_STYLESHEET = "/gui/css/button.css";
    static final String BUTTON_ALT_STYLESHEET = "/gui/css/button_alt.css";
    static final String LIST_VIEW_STYLESHEET = "/gui/css/list_view.css";
    static final String TEXT_FIELD_STYLESHEET = "/gui/css/text_field.css";
    static final String TEXT_FIELD_ALT_STYLESHEET = "/gui/css/text_field_alt.css";
    static final String DIALOG_STYLESHEET = "/gui/css/dialog.css";
    static final String LABEL_STYLESHEET = "/gui/css/label.css";

    //font sizes
    static final double DEFAULT_FONT_SIZE = 20.0;

    //fonts
    static final Font DEFAULT_FONT = Font.loadFont(Palette.class.getResource(Palette.DEFAULT_FONT_PATH).toExternalForm(), DEFAULT_FONT_SIZE);

    //font paths
    static final String DEFAULT_FONT_PATH = "/gui/fonts/orbitron.otf";

    // sizes
    private static final double SIZE_NONE = 0.0;
    private static final double SIZE_DEFAULT = 10.0;
    private static final double SIZE_MEDIUM = 20.0;
    private static final double SIZE_LARGE = 30.0;

    //paddings
    static final Insets DEFAULT_SQUARED_PADDING = new Insets(SIZE_DEFAULT);
    static final Insets MEDIUM_SQUARED_PADDING = new Insets(SIZE_MEDIUM);
    static final Insets MEDIUM_HORIZONTAL_PADDING = new Insets(SIZE_NONE, SIZE_DEFAULT, SIZE_DEFAULT, SIZE_NONE);

    //margins
    static final Insets DEFAULT_MARGIN = new Insets(SIZE_DEFAULT);
    static final Insets MEDIUM_LEFT_MARGIN = new Insets(SIZE_NONE, SIZE_NONE, SIZE_NONE, SIZE_MEDIUM);
    static final Insets MEDIUM_RIGHT_MARGIN = new Insets(SIZE_NONE, SIZE_MEDIUM, SIZE_NONE, SIZE_NONE);
    static final Insets MEDIUM_MARGIN = new Insets(SIZE_MEDIUM);
    static final Insets LARGE_RIGHT_MARGIN = new Insets(SIZE_NONE, SIZE_LARGE, SIZE_NONE, SIZE_NONE);
    static final Insets LARGE_VERTICAL_MARGIN = new Insets(SIZE_LARGE, SIZE_NONE, SIZE_NONE, SIZE_LARGE);

    //dimensions
    static final double LIST_VIEW_ITEM_HEIGHT = LIST_ITEM_BACKGROUND_IMAGE.getImage().getHeight();
    static final double LIST_VIEW_ITEM_WIDTH = LIST_ITEM_BACKGROUND_IMAGE.getImage().getWidth();
    static final double ICON_FIT_WIDTH = 64.0;
    static final double ICON_FIT_HEIGHT = 64.0;

    //spacings
    static final double DEFAULT_SPACING = 10.0;
    static final double MEDIUM_SPACING = 15.0;

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

    private static <T> Dialog<T> initDialog(Dialog<T> dialog, ButtonType confirmationButtonType, ButtonType cancellationButtonType,
                                            String title, String headerText, String contentText, Stage owner) {
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(contentText);

        dialog.getDialogPane().getButtonTypes().addAll(confirmationButtonType, cancellationButtonType);

        dialog.setResizable(false);
        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        dialog.initOwner(owner);

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        enableDragAndDrop(dialog.getDialogPane().getScene(), owner);

        dialog.getDialogPane().getStylesheets().add(DIALOG_STYLESHEET);

        Platform.runLater(() -> dialog.getDialogPane().getScene().getWindow().sizeToScene());
        return dialog;
    }

    private static void enableDragAndDrop(Scene scene, Stage parent) {
        final Delta dragDelta = new Delta();
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = parent.getX() - mouseEvent.getScreenX();
            dragDelta.y = parent.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            parent.setX(mouseEvent.getScreenX() + dragDelta.x);
            parent.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
    }

    static Alert errorAlert(String title, String headerText, String contentText, Stage owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.setResizable(false);
        alert.initOwner(owner);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initStyle(StageStyle.UNDECORATED);
        enableDragAndDrop(alert.getDialogPane().getScene(), owner);

        alert.getDialogPane().getStylesheets().add(DIALOG_STYLESHEET);

        ImageView errorImageView = ERROR_ICON;
        errorImageView.setFitWidth(ICON_FIT_WIDTH);
        errorImageView.setFitHeight(ICON_FIT_HEIGHT);
        alert.setGraphic(errorImageView);

        Platform.runLater(() -> alert.getDialogPane().getScene().getWindow().sizeToScene());
        return alert;
    }

    static Stage passwordChoiceStage(Stage owner, Button joinButton, Button cancelButton, TextField inputPassword, HBox errorLabelBox) {
        //label
        Label passwordLabel = new Label(Palette.PASSWORD_TEXT);
        passwordLabel.getStylesheets().add(Palette.LABEL_STYLESHEET);

        //label container
        HBox passwordLabelHBox = new HBox(passwordLabel);
        passwordLabelHBox.setAlignment(Pos.CENTER_LEFT);

        //label and text field container
        HBox passwordInputBox = new HBox();
        passwordInputBox.getChildren().addAll(passwordLabelHBox, inputPassword);
        HBox.setMargin(passwordLabel, MEDIUM_RIGHT_MARGIN);

        //password box and error label container
        VBox passwordBox = new VBox();
        passwordBox.getChildren().addAll(passwordInputBox, errorLabelBox);
        VBox.setMargin(passwordInputBox, LARGE_VERTICAL_MARGIN);

        //button container
        TilePane tileButtons = new TilePane(Orientation.HORIZONTAL);
        tileButtons.setAlignment(Pos.BOTTOM_RIGHT);
        tileButtons.setPadding(MEDIUM_HORIZONTAL_PADDING);
        tileButtons.setVgap(SIZE_NONE);
        tileButtons.setHgap(SIZE_NONE);
        tileButtons.getChildren().addAll(joinButton, cancelButton);

        //base pane
        BorderPane basePane = new BorderPane();
        basePane.setCenter(passwordBox);
        basePane.setBottom(tileButtons);
        basePane.setBackground(new Background(new BackgroundFill(Palette.ADRENALINE_DARK_GRAY_FILL, CornerRadii.EMPTY, Insets.EMPTY)));

        //scene
        Scene scene = new Scene(basePane);

        //stage
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        return stage;
    }

    static Background background(ImageView imageview) {
        return createBackground(imageview.getImage(), false, true);
    }

    static Background backgroundImage(ImageView imageview) {
        return createBackground(imageview.getImage(), true, false);
    }

    private static Background createBackground(Image image, boolean contain, boolean cover) {
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, contain, cover);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        return new Background(backgroundImage);
    }
}

class Delta {
    double x, y;
}
