package it.polimi.ingsw.view.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.model.Game.autoPilot;

// offers the final delivery of a string to the client
// if the string is a request, it awaits for a valid response that will be returned to the caller
// used to request values for setup, settings, game choices, and updates about the game status
public abstract class Dispatcher {

    // outputs a string
    public static void sendMessage(String message) {
        CLI.print(message);
    }

    // prints a request string and returns the response inserted via System.in (terminal)
    private static String requestRoutine(String message) {
        sendMessage("\n" + message);
        String response;
        try {
            BufferedReader bufferedRead = new BufferedReader(new InputStreamReader(System.in));
            response = bufferedRead.readLine();
        }
        catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        return response;
    }

    // used when requesting integers -- tries to convert a string to an integer
    // upon failure (due to malformed input) it defaults to a value passed as parameter
    private static int safeIntegerConversion(String s, int defaultValue) {
        int n;
        try {
            n = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            n = defaultValue;
        }
        return n;
    }

    public static int requestMappedOption(String message, List<?> options, List<Integer> numbers) {
        int length = options.size();

        StringBuilder messageBuilder = new StringBuilder(message);
        for(int i = 0; i < length; i ++) {
            Object option = options.get(i);
            int id = numbers.get(i);
            messageBuilder.append("\n" + "[").append(id).append("] ").append(option.toString());
        }
        message = messageBuilder.toString();

        if(autoPilot) {
            sendMessage("\n" + message);
            int auto = numbers.get((int) Math.floor(Math.random() * options.size()));
            sendMessage("\nauto >>> " + auto);
            return numbers.indexOf(auto);
        }

        int value = numbers.stream().reduce(0, Math::min) - 1;
        do {
            value = Dispatcher.safeIntegerConversion(Dispatcher.requestRoutine(message), value);
        } while(!numbers.contains(value));
        return numbers.indexOf(value);
    }

    public static int requestListedOption(String message, List<?> list) {
        return requestMappedOption(message, list, list.stream().map(list::indexOf).map(x -> x + 1).collect(Collectors.toList()));
    }

    // keeps requesting a "y" or "n" answer until such request is fulfilled
    public static boolean requestBoolean(String message) {
        if(autoPilot) {
            sendMessage("\n" + message);
            boolean auto = Math.random() < 0.5;
            sendMessage("\nauto >>> " + auto);
            return auto;
        }

        String s;
        do {
            s = Dispatcher.requestRoutine(message + " [y|n]");
            if(s == null)
                return false;
        } while(!s.equals("y") && !s.equals("n"));
        return s.equals("y");
    }
}