package it.polimi.ingsw;

import it.polimi.ingsw.network.client.communication.CommunicationHandler;
import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.common.util.Console;
import it.polimi.ingsw.network.server.Server;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App {
    private static Console console;
    private static ExecutorService executor;
    private static Runnable task;

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

    private static CommunicationHandler.Interface communicationInterface;
    private static String graphicalInterface;

    private static String helpString = "Adrenaline: the game.\n\n" +
            "Usage:\n" +
            "\tAdrenaline.jar -help\n" +
            "\tAdrenaline.jar -mode s <ip address> <port>\n" +
            "\tAdrenaline.jar -mode c <ip address> <port> [-conn (s|r)] [-int (c|g)]\n\n" +
            "Options:\n" +
            "\t-help\t\tShows this screen.\n" +
            "\t-mode [c|s]\tThe application mode: client or server.\n" +
            "\tip address\tThe host IP address.\n" +
            "\tport\t\tThe host port.\n" +
            "\t-conn [s|r]\tThe connection protocol: socket or RMI. [Default: s].\n" +
            "\t-mode [c|g]\tThe display method: CLI or GUI. [Default: g].\n";

    private static Configuration init(String[] args) {
        console.clear();

        if (args.length == 0) {
            console.tinyPrint(helpString);
            return Configuration.ERROR;
        }

        if (args[0].equals("-help")) {
            console.tinyPrint(helpString);
            return Configuration.ERROR;
        } else if (!args[0].equals("-mode")) {
            console.err("first argument must be: -mode [c|s]. Type \"-help\" to see more.");
            return Configuration.ERROR;
        }

        if (args.length < 4) {
            console.err("insufficient arguments, required: <ip address> <port>");
            return Configuration.ERROR;
        }

        String mode = args[1];

        ipAddress = args[2];

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
            port = Integer.parseInt(args[3]);
            if (port < 1024 || port > 65535) {
                console.err("port not int range [1024-65535]");
                return Configuration.ERROR;
            }
        } catch (NumberFormatException ignored) {
            console.err("provide an integer value for parameter <port>");
            return Configuration.ERROR;
        }

        if (mode.equals("s")) {
            if (args.length == 4) {
                return Configuration.SERVER;
            } else {
                console.err("too many arguments. Correct server syntax is -mode [s] <ip address> <port>. Type \"-help\" to see more.");
                return Configuration.ERROR;
            }
        } else if (mode.equals("c")) {
            if (args.length == 4) {
                return App.Configuration.CLIENT_SOCKET_GUI;
            } else if (args.length == 6) {
                String argument = args[4];
                String connectionType;

                if (argument.equals("-conn")) {
                    connectionType = args[5];

                    if (connectionType.equals("s")) {
                        return App.Configuration.CLIENT_SOCKET_GUI;
                    } else if (connectionType.equals("r")) {
                        return App.Configuration.CLIENT_RMI_GUI;
                    } else {
                        console.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else if (argument.equals("-int")) {
                    String interfaceType = args[5];

                    if (interfaceType.equals("c")) {
                        return App.Configuration.CLIENT_SOCKET_CLI;
                    } else if (interfaceType.equals("g")) {
                        return App.Configuration.CLIENT_SOCKET_GUI;
                    } else {
                        console.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else {
                    console.err("invalid argument, type \"-help\" to see more.");
                    return App.Configuration.ERROR;
                }
            } else if (args.length == 8) {
                String firstArgument = args[4];
                String connectionType;

                if (firstArgument.equals("-conn")) {
                    connectionType = args[5];

                    if (!connectionType.equals("s") && !connectionType.equals("r")) {
                        console.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                        return App.Configuration.ERROR;
                    }

                    String secondArgument = args[6];
                    if (!secondArgument.equals("-int")) {
                        console.err("invalid second argument, type \"-help\" to see more.");
                        return App.Configuration.ERROR;
                    }

                    String interfaceType = args[7];
                    if (interfaceType.equals("c")) {
                        if (connectionType.equals("s"))
                            return App.Configuration.CLIENT_SOCKET_CLI;
                        return App.Configuration.CLIENT_RMI_CLI;
                    } else if (interfaceType.equals("g")) {
                        if (connectionType.equals("s"))
                            return App.Configuration.CLIENT_SOCKET_GUI;
                        return App.Configuration.CLIENT_RMI_GUI;
                    } else {
                        console.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else if (firstArgument.equals("-int")) {
                    String interfaceType = args[5];

                    if (!interfaceType.equals("c") && !interfaceType.equals("g")) {
                        console.err("parameter for argument \"-int\" must be [c|g]. Type \"-help\" to see more.");
                        return App.Configuration.ERROR;
                    }

                    String secondArgument = args[6];
                    if (!secondArgument.equals("-conn")) {
                        console.err("invalid second argument, type \"-help\" to see more.");
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
                        console.err("parameter for argument \"-conn\" must be [s|r]. Type \"-help\" to see more.");
                        return App.Configuration.ERROR;
                    }
                } else {
                    console.err("invalid first argument, type \"-help\" to see more.");
                    return App.Configuration.ERROR;
                }
            } else {
                console.err("invalid arguments, type \"-help\" to see more.");
                return App.Configuration.ERROR;
            }
        } else {
            console.err("parameter for argument \"-mode\" must be [c|s]. Type \"-help\" to see more.");
            return Configuration.ERROR;
        }
    }

    public static void main(String[] args) {
        console = Console.getInstance();
        executor = Executors.newSingleThreadExecutor();

        Configuration config = init(args);

        if (config.equals(Configuration.SERVER)) {
            //launch server with ipAddress and port
            task = new Server(ipAddress, port);
        } else {
            if (config.equals(Configuration.CLIENT_SOCKET_CLI)) {
                //launch client with ipAddress, port, socket and CLI
                communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
                graphicalInterface = "CLI";
            } else if (config.equals(Configuration.CLIENT_SOCKET_GUI)) {
                //launch client with ipAddress, port, socket and GUI
                communicationInterface = CommunicationHandler.Interface.SOCKET_INTERFACE;
                graphicalInterface = "GUI";
            } else if (config.equals(Configuration.CLIENT_RMI_CLI)) {
                //launch client with ipAddress, port, RMI and CLI
                communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
                graphicalInterface = "CLI";
            } else if (config.equals(Configuration.CLIENT_RMI_GUI)) {
                //launch client with ipAddress, port, RMI and GUI
                communicationInterface = CommunicationHandler.Interface.RMI_INTERFACE;
                graphicalInterface = "GUI";
            } else// ERROR
                System.exit(-1);
            task = new Client(ipAddress, port, communicationInterface, graphicalInterface, args);
        }
        executor.execute(task);
    }
}