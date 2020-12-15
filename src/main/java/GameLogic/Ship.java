package GameLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ship {
    String name;
    // undamaged is a list of Pairs representing an x-y coordinate for each
    // tile of the ship
    ArrayList<ArrayList<Integer>> undamaged;

    public Ship(String name, ArrayList<ArrayList<Integer>> coordinates){
        this.name = name;
        this.undamaged = coordinates;
        System.out.println(coordinates);
    }

    public boolean checkHit(Integer pos1, Integer pos2){
        ArrayList<Integer> tile = new ArrayList<>(Arrays.asList(pos1, pos2));
        if (undamaged.contains(tile)){
            undamaged.remove(tile);
            return true;
        } else {
            return false;
        }
    }


    public boolean isSunk(){
        return this.undamaged.isEmpty();
    }

    public ArrayList<ArrayList<Integer>> getCoords(){
        return this.undamaged;
    }
}
