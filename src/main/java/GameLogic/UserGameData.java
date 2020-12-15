package GameLogic;

import java.util.*;

public class UserGameData {
    UUID userID;
    ArrayList<Ship> ships = new ArrayList<>();
    Integer boardLength;
    // Represents current state of opponents board
        // 0 = Nothing, 1 = Shot and missed, 2 = Shot and hit
    ArrayList<ArrayList<Integer>> opponentsBoard;
    final ArrayList<String> shipNames = new ArrayList<>(Arrays.asList(
            "Aircraft Carrier",
            "Battleship",
            "Cruiser",
            "Submarine",
            "Destroyer"
    ));
    final ArrayList<Integer> shipSizes = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));

    public UserGameData(UUID id, Integer bLength){
        this.userID = id;
        this.boardLength = bLength;
        generateRandomShips();
        this.opponentsBoard = new ArrayList<>();
        for (int i=0; i < boardLength; i++){
            var row = new ArrayList<Integer>();
            for (int j=0; j<boardLength; j++){
                row.add(0);
            }
            opponentsBoard.add(row);
        }

    }

    public boolean canFire(int x, int y){
        int tile = opponentsBoard.get(x).get(y);
        return tile == 0;
    }

    /*
    Method called when fired on by enemy player. Will return int between 0-2:
        - 0 = missed
        - 1 = hit
        - 2 = sunk ship
     */
    public int takeHit(int x, int y){
        for (Ship s: ships){
            // check if hit ship and whether it sank
            if (s.checkHit(x, y)){
                var sunk = s.isSunk();
                if (sunk)
                    return 2;
                else
                    return 1;
            }
        }
        return 0;
    }

    /*
    Method called after ship fires.
     */
    public void updateBoard(int x, int y, int hitVal){
        opponentsBoard.get(x).set(y, hitVal);
    }

    private void generateRandomShips(){
        Random rand = new Random();
        ArrayList<ArrayList<Integer>> occupiedSpaces = new ArrayList<>();
        for (int i=0; i < shipNames.size(); i++){
            boolean created = false;
            while (!created){
                Integer startX = rand.nextInt(boardLength);
                Integer startY = rand.nextInt(boardLength);
                Integer length = shipSizes.get(i);
                int dir = rand.nextInt(4);
                boolean valid = checkValidity(dir, startX, startY, length);
                ArrayList<ArrayList<Integer>> plannedSpaces = new ArrayList<>();
                if (valid){
                    plannedSpaces.add(new ArrayList<>(Arrays.asList(startX, startY)));
                    switch(dir){
                        case 0:
                            for (int j=1; j < length; j++){
                                plannedSpaces.add(new ArrayList<>(Arrays.asList(startX, startY-j)));
                            }
                            break;
                        case 1:
                            for (int j=1; j < length; j++){
                                plannedSpaces.add(new ArrayList<>(Arrays.asList(startX+j, startY)));
                            }
                            break;
                        case 2:
                            for (int j=1; j < length; j++){
                                plannedSpaces.add(new ArrayList<>(Arrays.asList(startX, startY+j)));
                            }
                            break;
                        case 3:
                            for (int j=1; j < length; j++){
                                plannedSpaces.add(new ArrayList<>(Arrays.asList(startX - j, startY)));
                            }
                            break;
                    }
                    if (Collections.disjoint(occupiedSpaces, plannedSpaces)){
                        occupiedSpaces.addAll(plannedSpaces);
                        Ship ship = new Ship(shipNames.get(i), plannedSpaces);
                        ships.add(ship);
                        created = true;
                    }
                }
            }
        }
    }

    public boolean hasLost(){
        for (Ship s: ships){
            if (!s.isSunk()){
                return false;
            }
        }
        return true;
    }

    private boolean checkValidity(int direction, Integer startX, Integer startY, Integer length){
        boolean check = false;
        switch(direction){
            case 0:
                if (startY.compareTo(length-1) >= 0)
                    check = true;
                break;
            case 1:
                if (startX.compareTo(boardLength-length) <= 0)
                    check = true;
                break;
            case 2:
                if (startY.compareTo(boardLength-length) <= 0)
                    check = true;
                break;
            case 3:
                if (startX.compareTo(length-1) >= 0)
                    check = true;
                break;
        }
        return check;
    }

    public ArrayList<Ship> getShips(){
        return this.ships;
    }
    /*
    Returns a COPY of the board, not the actual board
     */
    public ArrayList<ArrayList<Integer>> getOpponentsBoard(){
        var retval = new ArrayList<ArrayList<Integer>>();
        for (var row: opponentsBoard){
            retval.add(new ArrayList<>(row));
        }
        return retval;
    }

}
