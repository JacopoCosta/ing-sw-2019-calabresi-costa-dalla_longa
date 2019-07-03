package it.polimi.ingsw.network.client.executable;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.util.console.Console;
import it.polimi.ingsw.view.remote.GraphicManager;

@SuppressWarnings("FieldCanBeLocal")
public class ClientLauncher {
    private static Console console;

    private static String ipAddress;
    private static int port;

    private static CommunicationHandler.Interface communicationInterface;
    private static GraphicManager.Interface graphicalInterface;

    private enum Configuration {
        CLIENT_SOCKET_CLI,
        CLIENT_SOCKET_GUI,
        CLIENT_RMI_CLI,
        CLIENT_RMI_GUI,

        ERROR
    }

    private static String helpString = "Adrenaline: the game.\n\n" +
            "Usage:\n" +
            "\tAdrenaline.jar -help\n" +
            "\tAdrenaline.jar <ip address> <port> [-conn (s|r)] [-int (c|g)]\n\n" +
            "Options:\n" +
            "\t-help\t\tShows this screen.\n" +
            "\tip address\tThe server IP address.\n" +
            "\tport\t\tThe server port.\n" +
            "\t-conn [s|r]\tThe connection protocol: socket or RMI. [Default: s].\n" +
            "\t-mode [c|g]\tThe draw method: CLI or GUI. [Default: g].\n";

    private static Configuration config(String[] args) {
        console.clear();

        if (args.length == 0) {
            console.tinyPrint(helpString);
            return Configuration.ERROR;
        }

        if (args[0].equals("-help")) {
            console.tinyPrint(helpString);
            return Configuration.ERROR;

        }

        if (args.length < 2) {
            console.err("insufficient arguments, required: <ip address> <port>");
            return Configuration.ERROR;
        }

        ipAddress = args[0];

        String[] subs = ipAddress.split("\\.");
        if (subs.length != 4) {
            console.err("invalid IP address");
            return Configuration.ERROR;
        }

        for (String sub : subs) {
            try {
                int val = Integer.parseInt(sub);

                if (val < 0 || val > 255) {
                    console.err("invalid IP address");
                    return Configuration.ERROR;
                }
            } catch (NumberFormatException ignored) {
                console.err("invalid IP address");
                return Configuration.ERROR;
            }
        }

        try {
            port = Integer.parseInt(args[1]);
            if (port < 1024 || port > 65535) {
                console.err("port not int range [1024-65535]");
                return Configuration.ERROR;
            }
        } catch (NumberFormatException ignored) {
            console.err("provide an integer value for parameter <port>");
            return Configuration.ERROR;
        }

        if (args.length == 2) {
            return Configuration.CLIENT_SOCKET_GUI;
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
                    console.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else if (argument.equals("-int")) {
                String interfaceType = args[3];

                if (interfaceType.equals("c")) {
                    return Configuration.CLIENT_SOCKET_CLI;
                } else if (interfaceType.equals("g")) {
                    return Configuration.CLIENT_SOCKET_GUI;
                } else {
                    console.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else {
                console.err("invalid argument, type \"-help\" to see more.");
                return Configuration.ERROR;
            }
        } else if (args.length == 6) {
            String firstArgument = args[2];
            String connectionType;

            if (firstArgument.equals("-conn")) {
                connectionType = args[3];

                if (!connectionType.equals("s") && !connectionType.equals("r")) {
                    console.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }

                String secondArgument = args[4];
                if (!secondArgument.equals("-int")) {
                    console.err("invalid second argument, type \"-help\" to see more.");
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
                    console.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else if (firstArgument.equals("-int")) {
                String interfaceType = args[3];

                if (!interfaceType.equals("c") && !interfaceType.equals("g")) {
                    console.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }

                String secondArgument = args[4];
                if (!secondArgument.equals("-conn")) {
                    console.err("invalid second argument, type \"-help\" to see more.");
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
                    console.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                    return Configuration.ERROR;
                }
            } else {
                console.err("invalid first argument, type \"-help\" to see more.");
                return Configuration.ERROR;
            }
        } else {
            console.err("invalid arguments, type \"-help\" to see more.");
            return Configuration.ERROR;
        }
    }

    public static void main(String[] args) {
        console = Console.getInstance();
        Configuration configuration = config(args);

        switch (configuration) {
            case CLIENT_SOCKET_CLI:
                //launch client with ipAddress, port, socket and CLI
                communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
                graphicalInterface = GraphicManager.Interface.CLI_INTERFACE;
                break;
            case CLIENT_SOCKET_GUI:
                //launch client with ipAddress, port, socket and GUI
                communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
                graphicalInterface = GraphicManager.Interface.GUI_INTERFACE;
                break;
            case CLIENT_RMI_CLI:
                //launch client with ipAddress, port, RMI and CLI
                communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
                graphicalInterface = GraphicManager.Interface.CLI_INTERFACE;
                break;
            case CLIENT_RMI_GUI:
                //launch client with ipAddress, port, RMI and GUI
                communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
                graphicalInterface = GraphicManager.Interface.GUI_INTERFACE;
                break;
            default:
                return;
        }

        Client client = new Client(ipAddress, port, communicationInterface, graphicalInterface);
        client.start();
    }
}
