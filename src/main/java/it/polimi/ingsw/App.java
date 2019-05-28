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

    private static Configuration init(String[] args) {
        /* syntax to launch App:
         *
         * SERVER > java -jar Adrenaline -mode [s] [ip address] [port]
         * CLIENT > java -jar Adrenaline -mode [c] [ip address] [port] (-conn [s/r]) (-int[c/g])
         *
         * NOTE: -mode must come first, then [ip address], then [port] in this exact order, then none, one or both of
         * the other parameters in any order, followed by their respective arguments.
         */

        if (args[0].equals("help")) {
            console.out("Use \"java -jar Adrenaline\" followed by:\n\n" +
                    "-mode [s/c]\tto choose from client or server functionality\n" +
                    "[ip address]\tthe ip address used by this game instance\n" +
                    "[port]\t\tthe port used by this game instance\n" +
                    "-conn [s/r]\t<OPTIONAL> used only with \"-mode c\". The connection technology used (socket or RMI)\n\t\t<DEFAULT> s\n" +
                    "-int [c/g]\t<OPTIONAL> used only with \"-mode c\". The interface used to interact with the game (CLI or GUI)\n\t\t<DEFAULT> g");
            return Configuration.ERROR;
        } else if (!args[0].equals("-mode")) {
            console.err("first argument must be: -mode [c/s]. Type \"help\" to see more.");
            return Configuration.ERROR;
        }

        String mode = args[1];

        ipAddress = args[2];
        try {
            port = Integer.parseInt(args[3]);
            if (port <= 0 || port > 65535)
                return Configuration.ERROR;
        } catch (NumberFormatException e) {
            return Configuration.ERROR;
        }

        if (mode.equals("s")) {
            if (args.length == 4) {
                return Configuration.SERVER;
            } else {
                console.err("too many arguments. Correct server syntax is -mode [s] [ip address] [port]. Type \"help\" to see more.");
                return Configuration.ERROR;
            }
        } else if (mode.equals("c")) {
            if (args.length == 4) {
                return App.Configuration.CLIENT_SOCKET_GUI;
            } else if (args.length == 6) {
                String argument = args[4];

                if (argument.equals("-conn")) {
                    connectionType = args[5];

                    if (connectionType.equals("s")) {
                        return App.Configuration.CLIENT_SOCKET_GUI;
                    } else if (connectionType.equals("r")) {
                        return App.Configuration.CLIENT_RMI_GUI;
                    } else {
                        console.err("parameter for argument \"-conn\" must be [s/r]. Type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else if (argument.equals("-int")) {
                    interfaceType = args[5];

                    if (interfaceType.equals("c")) {
                        return App.Configuration.CLIENT_SOCKET_CLI;
                    } else if (interfaceType.equals("g")) {
                        return App.Configuration.CLIENT_SOCKET_GUI;
                    } else {
                        console.err("parameter for argument \"-int\" must be [c/g]. Type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else {
                    console.err("invalid argument, type \"help\" to see more.");
                    return App.Configuration.ERROR;
                }
            } else if (args.length == 8) {
                String firstArgument = args[4];


                if (firstArgument.equals("-conn")) {
                    connectionType = args[5];

                    if (!connectionType.equals("s") && !connectionType.equals("r")) {
                        console.err("parameter for argument \"-conn\" must be [s/r]. Type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }

                    String secondArgument = args[6];
                    if (!secondArgument.equals("-int")) {
                        console.err("invalid second argument, type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }

                    interfaceType = args[7];
                    if (interfaceType.equals("c")) {
                        if (connectionType.equals("s"))
                            return App.Configuration.CLIENT_SOCKET_CLI;
                        return App.Configuration.CLIENT_RMI_CLI;
                    } else if (interfaceType.equals("g")) {
                        if (connectionType.equals("s"))
                            return App.Configuration.CLIENT_SOCKET_GUI;
                        return App.Configuration.CLIENT_RMI_GUI;
                    } else {
                        console.err("parameter for argument \"-int\" must be [c/g]. Type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else if (firstArgument.equals("-int")) {
                    interfaceType = args[5];

                    if (!interfaceType.equals("c") && !interfaceType.equals("g")) {
                        console.err("parameter for argument \"-int\" must be [c/g]. Type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }

                    String secondArgument = args[6];
                    if (!secondArgument.equals("-conn")) {
                        console.err("invalid second argument, type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }

                    connectionType = args[7];
                    if (connectionType.equals("s")) {
                        if (interfaceType.equals("c"))
                            return App.Configuration.CLIENT_SOCKET_CLI;
                        return App.Configuration.CLIENT_SOCKET_GUI;
                    } else if (connectionType.equals("r")) {
                        if (interfaceType.equals("c"))
                            return App.Configuration.CLIENT_RMI_CLI;
                        return App.Configuration.CLIENT_RMI_GUI;
                    } else {
                        console.err("parameter for argument \"-conn\" must be [s/r]. Type \"help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else {
                    console.err("invalid first argument, type \"help\" to see more.");
                    return App.Configuration.ERROR;
                }
            } else {
                console.err("invalid arguments, type \"help\" to see more.");
                return App.Configuration.ERROR;
            }
        } else {
            console.err("parameter for argument \"-mode\" must be [c/s]. Type \"help\" to see more.");
            return Configuration.ERROR;
        }
    }

    public static void main(String[] args) {
        /*switch (init(args)) {
            case SERVER:
                //launch server with ipAddress and port
                System.exit(0);
                break;
            case CLIENT_SOCKET_CLI:
                //launch client with ipAddress, port, socket and CLI
                System.exit(0);
                break;
            case CLIENT_SOCKET_GUI:
                //launch client with ipAddress, port, socket and GUI
                System.exit(0);
                break;
            case CLIENT_RMI_CLI:
                //launch client with ipAddress, port, RMI and CLI
                System.exit(0);
                break;
            case CLIENT_RMI_GUI:
                //launch client with ipAddress, port, RMI and GUI
                System.exit(0);
                break;
            case ERROR:
                System.exit(-1);
                break;
        }*/

        CommunicationHandler communicationHandler;
        try {
            communicationHandler = new CommunicationHandler("127.0.0.1", 60000, CommunicationHandler.Interface.RMI_INTERFACE);
        } catch (ConnectionException e) {
            e.printStackTrace();
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