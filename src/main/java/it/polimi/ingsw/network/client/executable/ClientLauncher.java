package it.polimi.ingsw.network.client.executable;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.util.printer.ColorPrinter;
import it.polimi.ingsw.view.remote.GraphicsManager;

/**
 * This class is used to properly launch a {@link Client} application. Its main purpose is to check whether the specific
 * parameters are correct and notify them to the {@link Client}, so that he can start as desired.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ClientLauncher {
    /**
     * The server host address to connect to.
     */
    private static String ipAddress;

    /**
     * The server port to listen from.
     */
    private static int port;

    /**
     * The communication agent used to interact with the remote server counterpart.
     */
    private static CommunicationHandler.Interface communicationInterface;

    /**
     * The preferred graphical interface to interact to during the application execution.
     */
    private static GraphicsManager.Interface graphicalInterface;

    /**
     * The possible configurations in which a {@link Client} can be found into.
     */
    private enum Configuration {
        CLIENT_SOCKET_CLI,
        CLIENT_SOCKET_GUI,
        CLIENT_RMI_CLI,
        CLIENT_RMI_GUI,

        ERROR
    }

    /**
     * The default {@code String} to display when the user inputs wrong or insufficient arguments, or simply asks for help.
     */
    private static String helpString = "Adrenaline: the game.\n\n" +
            "Usage:\n" +
            "\tAdrenaline.jar -help\n" +
            "\tAdrenaline.jar <ip address> <port> [-conn (s|r)] [-int (c|g)]\n\n" +
            "Options:\n" +
            "\t-help\t\tShows this screen.\n" +
            "\tip address\tThe server IP address.\n" +
            "\tport\t\tThe server port.\n" +
            "\t-conn [s|r]\tThe connection protocol: socket or RMI. [Default: s].\n" +
            "\t-int [c|g]\tThe draw method: CLI or GUI. [Default: g].\n";

    /**
     * Process the given set of {@code args} and return the desired {@link Configuration} to start the {@link Client} with.
     *
     * @param args the parameters to start the client with.
     * @return the corresponding {@link Client} {@link Configuration} or an error one if parameters are incorrect or missing.
     */
    private static Configuration config(String[] args) {
        ColorPrinter.clear();

        if (args.length == 0) {
            ColorPrinter.println(helpString);
            return Configuration.ERROR;
        }

        if (args[0].equals("-help")) {
            ColorPrinter.println(helpString);
            return Configuration.ERROR;

        }

        if (args.length < 2) {
            ColorPrinter.err("insufficient arguments, required: <ip address> <port>");
            return Configuration.ERROR;
        }

        ipAddress = args[0];

        String[] subs = ipAddress.split("\\.");
        if (subs.length != 4) {
            ColorPrinter.err("invalid IP address");
            return Configuration.ERROR;
        }

        for (String sub : subs) {
            try {
                int val = Integer.parseInt(sub);

                if (val < 0 || val > 255) {
                    ColorPrinter.err("invalid IP address");
                    return Configuration.ERROR;
                }
            } catch (NumberFormatException ignored) {
                ColorPrinter.err("invalid IP address");
                return Configuration.ERROR;
            }
        }

        try {
            port = Integer.parseInt(args[1]);
            if (port < CommunicationHandler.LOWERBOUD_PORT || port > CommunicationHandler.UPPERBOUND_PORT) {
                ColorPrinter.err("port not int range [" + CommunicationHandler.LOWERBOUD_PORT + "-" + CommunicationHandler.UPPERBOUND_PORT + "]");
                return Configuration.ERROR;
            }
        } catch (NumberFormatException ignored) {
            ColorPrinter.err("provide an integer value for parameter <port>");
            return Configuration.ERROR;
        }

        if (args.length == 2) {
            return Configuration.CLIENT_SOCKET_CLI;
        } else if (args.length == 4) {
            String argument = args[2];
            String connectionType;

            if (argument.equals("-conn")) {
                connectionType = args[3];

                if (connectionType.equals("s")) {
                    return Configuration.CLIENT_SOCKET_GUI;
                } else if (connectionType.equals("r")) {
                    return Configuration.CLIENT_RMI_GUI;
                } else {
                    ColorPrinter.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else if (argument.equals("-int")) {
                String interfaceType = args[3];

                if (interfaceType.equals("c")) {
                    return Configuration.CLIENT_SOCKET_CLI;
                } else if (interfaceType.equals("g")) {
                    return Configuration.CLIENT_SOCKET_GUI;
                } else {
                    ColorPrinter.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else {
                ColorPrinter.err("invalid argument, type \"-help\" to see more.");
                return Configuration.ERROR;
            }
        } else if (args.length == 6) {
            String firstArgument = args[2];
            String connectionType;

            if (firstArgument.equals("-conn")) {
                connectionType = args[3];

                if (!connectionType.equals("s") && !connectionType.equals("r")) {
                    ColorPrinter.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }

                String secondArgument = args[4];
                if (!secondArgument.equals("-int")) {
                    ColorPrinter.err("invalid second argument, type \"-help\" to see more.");
                    return Configuration.ERROR;
                }

                String interfaceType = args[5];
                if (interfaceType.equals("c")) {
                    if (connectionType.equals("s"))
                        return Configuration.CLIENT_SOCKET_CLI;
                    return Configuration.CLIENT_RMI_CLI;
                } else if (interfaceType.equals("g")) {
                    if (connectionType.equals("s"))
                        return Configuration.CLIENT_SOCKET_GUI;
                    return Configuration.CLIENT_RMI_GUI;
                } else {
                    ColorPrinter.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else if (firstArgument.equals("-int")) {
                String interfaceType = args[3];

                if (!interfaceType.equals("c") && !interfaceType.equals("g")) {
                    ColorPrinter.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }

                String secondArgument = args[4];
                if (!secondArgument.equals("-conn")) {
                    ColorPrinter.err("invalid second argument, type \"-help\" to see more.");
                    return Configuration.ERROR;
                }

                connectionType = args[5];
                if (connectionType.equals("s")) {
                    if (interfaceType.equals("c"))
                        return Configuration.CLIENT_SOCKET_CLI;
                    return Configuration.CLIENT_SOCKET_GUI;
                } else if (connectionType.equals("r")) {
                    if (interfaceType.equals("c"))
                        return Configuration.CLIENT_RMI_CLI;
                    return Configuration.CLIENT_RMI_GUI;
                } else {
                    ColorPrinter.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else {
                ColorPrinter.err("invalid first argument, type \"-help\" to see more.");
                return Configuration.ERROR;
            }
        } else {
            ColorPrinter.err("invalid arguments, type \"-help\" to see more.");
            return Configuration.ERROR;
        }
    }

    /**
     * Launches the {@code ClientLauncher} application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        Configuration configuration = config(args);

        switch (configuration) {
            case CLIENT_SOCKET_CLI:
                //launch client with ipAddress, port, socket and CLI
                communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
                graphicalInterface = GraphicsManager.Interface.CLI_INTERFACE;
                break;
            case CLIENT_SOCKET_GUI:
                //launch client with ipAddress, port, socket and GUI
                communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
                graphicalInterface = GraphicsManager.Interface.GUI_INTERFACE;
                break;
            case CLIENT_RMI_CLI:
                //launch client with ipAddress, port, RMI and CLI
                communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
                graphicalInterface = GraphicsManager.Interface.CLI_INTERFACE;
                break;
            case CLIENT_RMI_GUI:
                //launch client with ipAddress, port, RMI and GUI
                communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
                graphicalInterface = GraphicsManager.Interface.GUI_INTERFACE;
                break;
            default:
                return;
        }

        Client client = new Client(ipAddress, port, communicationInterface, graphicalInterface);
        client.start();
    }
}
