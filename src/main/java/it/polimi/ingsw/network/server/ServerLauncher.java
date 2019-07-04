package it.polimi.ingsw.network.server;

import it.polimi.ingsw.util.printer.ColorPrinter;
import org.fusesource.jansi.AnsiConsole;

public class ServerLauncher {

    private static String ipAddress;
    private static int port;

    private static final String helpString = "Adrenaline server.\n\n" +
            "Usage:\n" +
            "Server.jar -help\n" +
            "Server.jar <ip address> <port>";

    private static void init(String[] args) {
        AnsiConsole.systemInstall();
        ColorPrinter.clear();

        if (args.length == 0) {
            ColorPrinter.println(helpString);
            System.exit(-1);
        }

        if (args[0].equals("-help")) {
            ColorPrinter.println(helpString);
            System.exit(-1);
        }

        if (args.length != 2) {
            ColorPrinter.err("insufficient arguments, required: <ip address> <port>");
            System.exit(-1);
        }

        ipAddress = args[0];

        String[] subs = ipAddress.split("\\.");
        if (subs.length != 4) {
            ColorPrinter.err("invalid IP address");
            System.exit(-1);
        }

        for (String sub : subs) {
            try {
                int val = Integer.parseInt(sub);

                if (val < 0 || val > 255) {
                    ColorPrinter.err("invalid IP address");
                    System.exit(-1);
                }
            } catch (NumberFormatException ignored) {
                ColorPrinter.err("invalid IP address");
                System.exit(-1);
            }
        }

        try {
            port = Integer.parseInt(args[1]);
            if (port < 1024 || port > 65535) {
                ColorPrinter.err("port not int range [1024-65535]");
                System.exit(-1);
            }
        } catch (NumberFormatException ignored) {
            ColorPrinter.err("provide an integer value for parameter <port>");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        init(args);

        Server server = new Server(ipAddress, port);
        server.start();
    }
}
