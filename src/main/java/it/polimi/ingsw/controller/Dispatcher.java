package it.polimi.ingsw.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

// simulates communication between server and a human-operated client via terminal
// used to request values for setup, settings, game choices
public abstract class Dispatcher {

    // prints a request message and returns the response inserted via System.in (terminal)
    private static String requestRoutine(String q) {
        System.out.println("\n" + q);
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