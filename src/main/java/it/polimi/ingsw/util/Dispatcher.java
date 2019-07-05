package it.polimi.ingsw.util;

import it.polimi.ingsw.network.client.executable.Client;
import it.polimi.ingsw.network.common.exceptions.ClientTimeOutException;
import it.polimi.ingsw.util.printer.ColorPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * This class offers the final delivery of a message and/or a request to the end user.
 * In case of a request, it holds the caller in a blocking wait, until the end user
 * inputs a response that is considered valid.
 */
public abstract class Dispatcher {
    /**
     * The number of milliseconds a {@link Client} is allowed to answer a question.
     */
    public static int ANSWER_TIME_LIMIT = 12000;

    /**
     * The maximum time allowed to pass between the user's press of {@code enter} and the start of the input processing.
     * Lowering this number too much may have an impact on performances.
     */
    private static final int INTERVAL = 100;

    /**
     * The {@code BufferedReader} used to gather and process the user's input.
     */
    private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Outputs a string to the end user.
     * @param message the string.
     */
    public static void sendMessage(String message) {
        ColorPrinter.println(message);
    }

    /**
     * This is a simple question and answer routine. The end user receives instructions
     * and inputs an answer.
     * @param message The instructions, or the question.
     * @return the user's response. No checks are performed on this.
     */
    private static String requestRoutine(String message) {
        sendMessage("\n" + message);

        int timeLeft = ANSWER_TIME_LIMIT;
        try {
            while (timeLeft > 0 && !bufferedReader.ready()) {
                try {
                    Thread.sleep(INTERVAL);
                } catch (InterruptedException e) {
                    return null;
                }
                timeLeft -= INTERVAL;
            }
        } catch (IOException e) {
            return null;
        }
        if(timeLeft > 0) {
            try {
                return bufferedReader.readLine();
            } catch (IOException ignored) { }
        }
        return null;
    }

    /**
     * Converts a string into its numerical equivalent, returned as int. Should the input string
     * not be convertible into an integer, a fallback value is returned instead.
     * @param s the string to parse.
     * @param defaultValue the fallback value.
     * @return the result of the conversion.
     */
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

    /**
     * Queries the end user with a multiple choice question. Each of the possible answers is labeled with a number.
     * The end user needs to input the number associated to the answer they want to select. The caller has a guarantee
     * that this method will return a valid index, corresponding to one of the options.
     * @param message The question.
     * @param options A list containing all options.
     * @param numbers A list containing the numerical labels. This list is expected to be as long as the {@code options} list.
     * @return The index of the selected option inside {@code options}. Please note that, in general, the number inputted by
     * the user and the number returned by this method are different.
     * @throws ClientTimeOutException when the user takes too long to answer.
     */
    public static int requestMappedOption(String message, List<String> options, List<Integer> numbers) throws ClientTimeOutException {
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
            String s = Dispatcher.requestRoutine(message);
            if(s == null)
                throw new ClientTimeOutException("Timed out.");
            value = Dispatcher.safeIntegerConversion(s, value);
        } while(!numbers.contains(value));
        return numbers.indexOf(value);
    }

    /**
     * Queries the end user with a yes-or-no question. The end user needs to input either {@code y} (for "yes")
     * or {@code n} (for "no").
     * @param message the question.
     * @return {@code true} if the answer is yes.
     * @throws ClientTimeOutException when the user takes too long to answer.
     */
    public static boolean requestBoolean(String message) throws ClientTimeOutException {
        String s;
        do {
            s = Dispatcher.requestRoutine(message + " [y|n]");
            if(s == null)
                throw new ClientTimeOutException("Timed out.");
        } while(!s.equals("y") && !s.equals("n"));
        return s.equals("y");
    }
}