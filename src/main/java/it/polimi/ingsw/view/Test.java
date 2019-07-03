package it.polimi.ingsw.view;
import it.polimi.ingsw.network.common.util.console.Console;
import it.polimi.ingsw.util.Color;
import it.polimi.ingsw.util.UTF;

public class Test {

    public static void main(String[] args) {

        Console console = Console.getInstance();
        console.ANSIPrintln(Color.ANSI_CYAN + "Acme" + UTF.corner1 + "Company");
    }
}
