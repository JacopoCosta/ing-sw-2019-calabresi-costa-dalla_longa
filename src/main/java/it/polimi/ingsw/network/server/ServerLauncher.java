package it.polimi.ingsw.network.server;

import it.polimi.ingsw.util.printer.ColorPrinter;
import org.fusesource.jansi.AnsiConsole;

/**
 * This class is used to properly launch a {@link Server} application. Its main purpose is to check whether the specific
 * parameters are correct and notify them to the {@link Server}, so that he can start as desired.
 */
public class ServerLauncher {

    /**
     * The {@code Server} ip address from which it can be reached by {@code Client}s
     */
    private static String ipAddress;

    /**
     * The {@code Server} port to listen to.
     */
    private static int port;

    /**
     * The default {@code String} to display when the user inputs wrong or insufficient arguments, or simply asks for help.
     */
    private static final String helpString = "Adrenaline server.\n\n" +
            "Usage:\n" +
            "Server.jar -help\n" +
            "Server.jar <ip address> <port>";

    /**
     * Process the given set of {@code args}  to start the {@link Server} with the proper configuration.
     *
     * @param args the parameters to start the client with.
     */
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

    /**
     * Launches the {@code ServerLauncher} application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        init(args);

        Server server = new Server(ipAddress, port);
        server.start();
    }
}
