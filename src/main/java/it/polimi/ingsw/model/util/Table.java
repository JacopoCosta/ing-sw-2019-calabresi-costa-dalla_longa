package it.polimi.ingsw.model.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Table {

    private static final int TAB_SIZE = 4;
    private static final String DEFAULT_SEPARATOR = ", ";
    private static final String NULL = "--";

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

    private static int columnWidth(List<?> list) {
        return TAB_SIZE + list.stream()
                .map(Object::toString)
                .map(String::length)
                .reduce(0, Math::max);
    }

    private static String createBlock(String content, int columnWidth) {
        StringBuilder s = new StringBuilder(content);
        for(int caret = content.length(); caret < columnWidth; caret ++)
            s.append(" ");
        return s.toString();
    }

    public static String list(List<?> list) {
        return list(list, DEFAULT_SEPARATOR);
    }

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
