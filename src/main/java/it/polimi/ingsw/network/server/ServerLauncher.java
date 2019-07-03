package it.polimi.ingsw.network.server;

import it.polimi.ingsw.util.console.Console;

public class ServerLauncher {
    private static Console console;

    private static String ipAddress;
    private static int port;

    private static final String helpString = "Adrenaline server.\n\n" +
            "Usage:\n" +
            "Server.jar -help\n" +
            "Server.jar <ip address> <port>";

    private static void init(String[] args){
        console.clear();

        if (args.length == 0) {
            console.tinyPrint(helpString);
            System.exit(-1);
        }

        if (args[0].equals("-help")) {
            console.tinyPrint(helpString);
            System.exit(-1);
        }

        if (args.length != 2) {
            console.err("insufficient arguments, required: <ip address> <port>");
            System.exit(-1);
        }

        ipAddress = args[0];

        String[] subs = ipAddress.split("\\.");
        if (subs.length != 4) {
            console.err("invalid IP address");
            System.exit(-1);
        }

        for (String sub : subs) {
            try {
                int val = Integer.parseInt(sub);

                if (val < 0 || val > 255) {
                    console.err("invalid IP address");
                    System.exit(-1);
                }
            } catch (NumberFormatException ignored) {
                console.err("invalid IP address");
                System.exit(-1);
            }
        }

        try {
            port = Integer.parseInt(args[1]);
            if (port < 1024 || port > 65535) {
                console.err("port not int range [1024-65535]");
                System.exit(-1);
            }
        } catch (NumberFormatException ignored) {
            console.err("provide an integer value for parameter <port>");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        console = Console.getInstance();
        init(args);

        Server server = new Server(ipAddress, port);
        server.start();
    }
}
