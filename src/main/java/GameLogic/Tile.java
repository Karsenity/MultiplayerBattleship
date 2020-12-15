package GameLogic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Tile {
    static String topTemplate = "+---";

    private static ArrayList<String> renderRow(ArrayList<Integer> row){
        int size = row.size();
        String top = topTemplate.repeat(size) + "+";
        StringBuilder middle = new StringBuilder();
        for (Integer integer : row) {
            String cell = switch (integer) {
                case 0 -> "|   ";
                case 1 -> "| - ";
                case 2 -> "| * ";
                case 4 -> "| O ";
                default -> throw new IllegalStateException("Unexpected value: " + integer);
            };
            middle.append(cell);
        }
        middle.append("|");
        String mid = middle.toString();
        return new ArrayList<>(Arrays.asList(top, mid));
    }

    public static ArrayList<String> renderBoard(ArrayList<ArrayList<Integer>> board){
        int size = board.size();
        ArrayList<String> retval = new ArrayList<>();
        for (ArrayList<Integer> row: board){
            retval.addAll(renderRow(row));
        }
        retval.add(topTemplate.repeat(size) + "+");
        return retval;
    }
}
