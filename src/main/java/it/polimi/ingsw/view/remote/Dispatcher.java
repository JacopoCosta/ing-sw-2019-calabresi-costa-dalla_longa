package it.polimi.ingsw.view.remote;

import it.polimi.ingsw.util.console.Console;

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
     * The {@link Console} singleton is used for printing messages and requests.
     */
    private static Console console = Console.getInstance();

    /**
     * Outputs a string to the end user.
     * @param message the string.
     */
    public static void sendMessage(String message) {
        console.tinyPrint(message);
    }

    /**
     * This is a simple question and answer routine. The end user receives instructions
     * and inputs an answer.
     * @param message The instructions, or the question.
     * @return the user's response. No checks are performed on this.
     */
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
     */
    public static int requestMappedOption(String message, List<String> options, List<Integer> numbers) {
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

    /**
     * Queries the end user with a yes-or-no question. The end user needs to input either {@code y} (for "yes")
     * or {@code n} (for "no").
     * @param message the question.
     * @return {@code true} if the answer is yes.
     */
    public static boolean requestBoolean(String message) {
        String s;
        do {
            s = Dispatcher.requestRoutine(message + " [y|n]");
            if(s == null)
                return false;
        } while(!s.equals("y") && !s.equals("n"));
        return s.equals("y");
    }
}