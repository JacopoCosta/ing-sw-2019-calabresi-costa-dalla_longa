package it.polimi.ingsw.view.remote.gui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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

abstract class Palette {
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
    private static final String BUTTON_STYLESHEET = "/gui/css/button.css";
    private static final String BUTTON_ALT_STYLESHEET = "/gui/css/button_alt.css";
    private static final String LIST_VIEW_STYLESHEET = "/gui/css/list_view.css";
    private static final String TEXT_FIELD_STYLESHEET = "/gui/css/text_field.css";
    private static final String TEXT_FIELD_ALT_STYLESHEET = "/gui/css/text_field_alt.css";
    private static final String DIALOG_STYLESHEET = "/gui/css/dialog.css";

    //font sizes
    private static final double DEFAULT_FONT_SIZE = 20.0;

    //fonts
    private static final Font DEFAULT_FONT = Font.loadFont(Palette.class.getResource(Palette.DEFAULT_FONT_PATH).toExternalForm(), DEFAULT_FONT_SIZE);

    //font paths
    private static final String DEFAULT_FONT_PATH = "/gui/fonts/orbitron.otf";

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
    static final Insets DEFAULT_SQUARED_MARGIN = new Insets(SIZE_DEFAULT);
    static final Insets MEDIUM_LEFT_MARGIN = new Insets(SIZE_NONE, SIZE_NONE, SIZE_NONE, SIZE_MEDIUM);
    static final Insets MEDIUM_RIGHT_MARGIN = new Insets(SIZE_NONE, SIZE_MEDIUM, SIZE_NONE, SIZE_NONE);
    static final Insets MEDIUM_SQUARED_MARGIN = new Insets(SIZE_MEDIUM);
    static final Insets LARGE_RIGHT_MARGIN = new Insets(SIZE_NONE, SIZE_LARGE, SIZE_NONE, SIZE_NONE);
    static final Insets LARGE_TOP_LEFT_MARGIN = new Insets(SIZE_LARGE, SIZE_NONE, SIZE_NONE, SIZE_LARGE);
    static final Insets LARGE_TOP_RIGHT_MARGIN = new Insets(SIZE_LARGE, SIZE_LARGE, SIZE_NONE, SIZE_NONE);

    //dimensions
    static final double LIST_VIEW_ITEM_HEIGHT = LIST_ITEM_BACKGROUND_IMAGE.getImage().getHeight();
    static final double LIST_VIEW_ITEM_WIDTH = LIST_ITEM_BACKGROUND_IMAGE.getImage().getWidth();

    //icon dimensions
    private static final double ICON_FIT_WIDTH = 64.0;
    private static final double ICON_FIT_HEIGHT = 64.0;

    //spacings
    static final double DEFAULT_SPACING = 10.0;
    static final double MEDIUM_SPACING = 15.0;

    //strings
    static final String JOIN_TEXT = "JOIN";
    static final String LOGIN_TEXT = "LOGIN";
    static final String OK_TEXT = "OK";
    static final String CANCEL_TEXT = "CANCEL";
    static final String QUIT_TEXT = "QUIT";
    static final String NAME_TEXT = "NAME";
    static final String PASSWORD_TEXT = "PASSWORD";
    static final String CREATE_JOIN_TEXT = "CREATE & JOIN";
    static final String CREATE_LOBBY_TEXT = "CREATE LOBBY";
    static final String INVALID_NICKNAME_TEXT = "Invalid nickname";
    static final String CHOOSE_NICKNAME_TEXT = "Choose a nickname";
    static final String ERROR_TITLE_TEXT = "Error";
    static final String GENERIC_ERROR_TEXT = "An error occurred";
    static final String TITLE_TEXT = "Adrenaline: the game!";
    static final String EXIT_MESSAGE_TEXT = "Exit Adrenaline?";
    static final String WELCOME_TEXT = "Welcome to adrenaline, ";

    //timeouts
    static final double DEFAULT_TIMEOUT = 3.0;

    static HBox labelBox(String text, Color textColor) {
        Label label = label(text, textColor, ADRENALINE_DARK_GRAY_TRANSPARENT);

        HBox hbox = new HBox();
        HBox.setMargin(label, Palette.DEFAULT_SQUARED_MARGIN);
        hbox.getChildren().add(label);
        hbox.setAlignment(Pos.CENTER);
        return hbox;
    }

    static Label label(String text, Color textColor, Color backgroundColor) {
        Label label = new Label(text);
        label.setPadding(DEFAULT_SQUARED_PADDING);
        label.setFont(DEFAULT_FONT);
        label.setTextFill(textColor);
        label.setBackground(backgroundColor(backgroundColor));
        return label;
    }

    static TextField textField() {
        TextField textField = new TextField();
        textField.getStylesheets().add(TEXT_FIELD_STYLESHEET);
        return textField;
    }

    static TextField textFieldAlt() {
        TextField textField = new TextField();
        textField.getStylesheets().add(TEXT_FIELD_ALT_STYLESHEET);
        return textField;
    }

    static Button button(String text) {
        Button button = new Button(text);
        button.getStylesheets().add(BUTTON_STYLESHEET);
        return button;
    }

    static Button buttonAlt(String text) {
        Button button = new Button(text);
        button.getStylesheets().add(BUTTON_ALT_STYLESHEET);
        return button;
    }

    static <T> ListView<T> listView(ObservableList<T> list) {
        ListView<T> listView = new ListView<>(list);
        listView.getStylesheets().add(LIST_VIEW_STYLESHEET);
        listView.setOrientation(Orientation.VERTICAL);
        listView.setPrefWidth(Palette.LIST_VIEW_ITEM_WIDTH / 2 + Palette.MEDIUM_SPACING);
        return listView;
    }

    static Background background(ImageView imageview) {
        return createBackground(imageview.getImage(), false, true);
    }

    static Background backgroundImage(ImageView imageview) {
        return createBackground(imageview.getImage(), true, false);
    }

    static Background backgroundColor(Color color) {
        return new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
    }

    private static Background createBackground(Image image, boolean contain, boolean cover) {
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, contain, cover);
        BackgroundImage backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        return new Background(backgroundImage);
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
        enableDragAndDrop(alert.getDialogPane().getScene(), (Stage) alert.getDialogPane().getScene().getWindow());

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
        Label passwordLabel = label(PASSWORD_TEXT, Color.WHITE, Color.TRANSPARENT);

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
        VBox.setMargin(passwordInputBox, LARGE_TOP_LEFT_MARGIN);

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
        basePane.setBackground(backgroundColor(Palette.ADRENALINE_DARK_GRAY_FILL));

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
}

class Delta {
    double x, y;
}
