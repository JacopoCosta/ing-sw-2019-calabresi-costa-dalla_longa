package it.polimi.ingsw.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

// offers the final delivery of a message to the client
// if the message is a request, it awaits for a valid response that will be returned to the caller
// used to request values for setup, settings, game choices, and updates about the game status
public abstract class Dispatcher {

    // outputs a message
    public static void sendMessage(String message) {
        CLI.print(message);
    }

    // prints a request message and returns the response inserted via System.in (terminal)
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

    public static int requestNumberedOption(String message, List<?> options, List<Integer> numbers) {
        int length = options.size();

        StringBuilder messageBuilder = new StringBuilder(message);
        for(int i = 0; i < length; i ++) {
            Object option = options.get(i);
            int id = numbers.get(i);
            messageBuilder.append("\n" + "[").append(id).append("] ").append(option.toString());
        }
        message = messageBuilder.toString();

        int value = numbers.stream().reduce(0, Math::min) - 1;
        do {
            value = Dispatcher.safeIntegerConversion(Dispatcher.requestRoutine(message), value);
        } while(!numbers.contains(value));
        return numbers.indexOf(value);
    }

    public static int requestIndex(String message, List<?> list) {
        return requestNumberedOption(message, list, list.stream().map(list::indexOf).map(x -> x + 1).collect(Collectors.toList()));
    }

    // keeps requesting an integer within an interval until such request is fulfilled
    public static int requestInteger(String message, int lowerBound, int upperBound) {
        int value = lowerBound - 1;
        do {
            value = Dispatcher.safeIntegerConversion(Dispatcher.requestRoutine(message + " [" + lowerBound + "~" + upperBound + "]"), value);
        } while(value < lowerBound || value > upperBound);
        return value;
    }

    // keeps requesting a "y" or "n" answer until such request is fulfilled
    public static boolean requestBoolean(String message) {
        String s;
        do {
            s = Dispatcher.requestRoutine(message + " [y|n]");
        } while(!s.equals("y") && !s.equals("n"));
        return s.equals("y");
    }

    // keeps requesting a non-empty string until such request is fulfilled
    public static String requestString(String message) {
        String s;
        do {
            s = Dispatcher.requestRoutine(message);
        } while(s.length() < 1);
        return s;
    }
}