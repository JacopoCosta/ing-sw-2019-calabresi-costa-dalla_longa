package it.polimi.ingsw.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a generic utility class used for smart printing and console formatting.
 * It is generally useful when outputting data in a list or matrix form.
 */
public abstract class Table {

    /**
     * The default size, in monospace characters, of a tab.
     */
    private static final int TAB_SIZE = 4;

    /**
     * The default separator between elements of a list.
     */
    private static final String DEFAULT_SEPARATOR = ", ";

    /**
     * The default string to printOpponents in places where no value was found.
     */
    private static final String NULL = "--";

    /**
     * Returns a string-matrix of data where every element of each column is vertically aligned to the other elements
     * in that same columns.
     * @param content Several {@code List}s, each representing a column.
     * @return a formatted string describing the content of every list.
     */
    public static String create(List<?> ...content) {
        StringBuilder table = new StringBuilder();

        List<Integer> columnWidths = Arrays.stream(content)
                .map(Table::columnWidth)
                .collect(Collectors.toList());

        int maxRow = Arrays.stream(content)
                .map(List::size)
                .reduce(0, Math::max);

        for(int i = 0; i < maxRow; i ++) {
            for(int j = 0; j < content.length; j ++) {
                int columnWidth = columnWidths.get(j);
                List<?> list = content[j];
                String element;
                try {
                    element = list.get(i).toString();
                } catch (Exception e) {
                    element = NULL;
                }
                table.append(createBlock(element, columnWidth));
            }
            table.append("\n");
        }
        return table.toString();
    }

    /**
     * Returns the minimum width, in monospace characters, that a list needs to be accounted for when
     * creating aligned columns.
     * @param list the list for which to measure the width.
     * @return the number of monospace characters used to describe the longest element of the list.
     */
    private static int columnWidth(List<?> list) {
        return TAB_SIZE + list.stream()
                .map(Object::toString)
                .map(String::length)
                .reduce(0, Math::max);
    }

    /**
     * Concatenates a string with as many spaces as necessary to make it exactly {@code columnWidth}
     * characters long.
     * @param content the starting string.
     * @param columnWidth the desired length.
     * @return the padded string.
     */
    private static String createBlock(String content, int columnWidth) {
        StringBuilder s = new StringBuilder(content);
        for(int caret = content.length(); caret < columnWidth; caret ++)
            s.append(" ");
        return s.toString();
    }

    /**
     * Puts each element of a list in a string, separating them with the {@link Table#DEFAULT_SEPARATOR} character.
     * @param list the list to printOpponents.
     * @return a string describing the content of the list.
     */
    public static String list(List<?> list) {
        return list(list, DEFAULT_SEPARATOR);
    }

    /**
     * Puts each element of a list in a string, separating them with another string passed in as argument.
     * @param list the list to printOpponents.
     * @param separator the string to put in between each pair of adjacent elements of the list.
     * @return a string describing the content of the list.
     */
    public static String list(List<?> list, String separator) {
        boolean useSeparator = false;
        StringBuilder s = new StringBuilder();
        for(Object o : list) {
            if(useSeparator)
                s.append(separator);
            if(o != null)
                s.append(o.toString());
            else
                s.append("null");
            useSeparator = true;
        }
        return s.toString();
    }
}
