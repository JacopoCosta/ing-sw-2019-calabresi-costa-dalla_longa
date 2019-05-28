package it.polimi.ingsw;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.common.exceptions.ConnectionException;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.view.remote.BoardArt;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;

public class App extends Application implements EventHandler<ActionEvent> {
    private static final Console console = new Console();

    private enum Configuration {
        SERVER,

        CLIENT_SOCKET_CLI,
        CLIENT_SOCKET_GUI,
        CLIENT_RMI_CLI,
        CLIENT_RMI_GUI,

        ERROR
    }

    private static String ipAddress;
    private static int port;

    private static String connectionType;
    private static String interfaceType;

    private static BoardArt boardArt;

    public static void main(String[] args) {


        Console console = new Console();

        String hostAddress;
        int port;

        CommunicationHandler communicationHandler;

        if (args.length != 4) {
            console.err("correct syntax is: Client [ip address] [port] -conn [s/r]\n");
            System.exit(-1);
            return;
        }
        hostAddress = args[0];

        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            console.err("server port not in range [1025 - 65535]\n");
            System.exit(-1);
            return;
        }

        if (!args[2].equals("-conn")) {
            console.err("correct syntax is: Client [ip address] [port] -conn [s/r]\n");
            System.exit(-1);
            return;
        }
        String interfaceType = args[3];

        CommunicationHandler.Interface communicationInterface;
        if (interfaceType.equals("s")) {
            communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
        } else if (interfaceType.equals("r")) {
            communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
        } else {
            console.err("options for param \"-conn\" must be [s] or [r]\n");
            System.exit(-1);
            return;
        }

        try {
            communicationHandler = new CommunicationHandler(hostAddress, port, communicationInterface);
        } catch (ConnectionException e) {
            console.err(e.getMessage() + "\n");
            System.exit(-1);
            return;
        }

        boardArt = new BoardArt(communicationHandler);

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) {

        boardArt.displayLogin(primaryStage);
    }

    @Override
    public void handle(javafx.event.ActionEvent event) {
        //nothing for now
    }

}