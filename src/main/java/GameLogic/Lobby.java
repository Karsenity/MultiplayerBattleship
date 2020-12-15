package GameLogic;


import Server.UserThread;

import java.util.ArrayList;
import java.util.UUID;

public class Lobby {
    private final UserThread player1;
    private UserThread player2;
    private final UUID gameID;
    private UUID playerTurn;
    private final int boardSize = 10;
    private final UserGameData p1Data;
    private UserGameData p2Data;

    public Lobby(UserThread p1) {
        this.player1 = p1;
        this.p1Data = new UserGameData(player1.getUserID(), boardSize);
        this.gameID = UUID.randomUUID();
        this.playerTurn = this.player1.getUserID();
    }

    public UUID getGameID(){
        return this.gameID;
    }

    public UUID getPlayer1(){
        return player1.getUserID();
    }

    public UUID getPlayer2(){
        if (player2 != null)
            return player2.getUserID();
        return null;
    }

    public void setPlayer2(UserThread p2){
        this.player2 = p2;
        this.p2Data = new UserGameData(player2.getUserID(), boardSize);
        player1.message("[Server]: Player2 has joined the game");
    }


    public void makeMove(UUID userID, int x, int y) {
        UserThread curPlayer = null;
        if (userID.equals(player1.getUserID())){
            curPlayer = player1;
        } else if (userID.equals(player2.getUserID())){
            curPlayer = player2;
        }
        if (userID.equals(playerTurn)){
            if (canFire(userID, x, y)){
                int turnResult = 0;
                if (userID.equals(player1.getUserID())){
                    turnResult = p2Data.takeHit(x, y);
                    p1Data.updateBoard(x, y, turnResult+1);
                    drawBoard(player1.getUserID());
                    drawBoard(player2.getUserID());
                    playerTurn = player2.getUserID();
                    if (p2Data.hasLost()){
                        player1.message("You win!");
                        player2.message("You lose!");
                        player1.closeConnection();
                        player2.closeConnection();
                    } else {
                        String[] responses = hitMessage(turnResult);
                        player1.message(responses[0]);
                        player2.message(responses[1]);
                    }

                }
                else if (userID.equals(player2.getUserID())) {
                    turnResult = p1Data.takeHit(x, y);
                    p2Data.updateBoard(x, y, turnResult+1);
                    drawBoard(player2.getUserID());
                    drawBoard(player1.getUserID());
                    playerTurn = player1.getUserID();
                    if (p1Data.hasLost()){
                        player2.message("You win!");
                        player1.message("You lose!");
                        player1.closeConnection();
                        player2.closeConnection();
                    } else {
                        String[] responses = hitMessage(turnResult);
                        player2.message(responses[0]);
                        player1.message(responses[1]);
                    }
                }
            } else {
                curPlayer.message("[Server]: You've already shot there, try again.");
            }
        } else {
            if (curPlayer != null)
                curPlayer.message("[Server]: It's not your turn!");
        }
    }

    /*
    Messages to return to players after a turn has been completed.
        - The first value in the list goes to the current player
        - The second value in the list goes to the other player
     */
    private String[] hitMessage(int hitVal){
        return switch (hitVal) {
            case 0 -> new String[]{"[Server]: You missed", "[Server]: Your Opponent Missed"};
            case 1 -> new String[]{"[Server]: Direct hit!", "[Server]: You've been hit!"};
            case 2 -> new String[]{"[Server]: You sunk your opponent's battleship!", "[Server]: You've lost a ship!"};
            default -> new String[]{"", ""};
        };
    }

    private boolean canFire(UUID userID, int x, int y){
        if (userID.equals(player1.getUserID()))
            return p1Data.canFire(x, y);
        if(userID.equals(player2.getUserID()))
            return p2Data.canFire(x, y);
        return false;
    }

    private void drawBoard(UUID userID){
        try {
            UserThread curPlayer = null;
            UserGameData curData = null;
            UserGameData otherData = null;
            if (userID.equals(player1.getUserID())){
                curPlayer = player1;
                curData = p1Data;
                otherData = p2Data;
            } else if (userID.equals(player2.getUserID())){
                curPlayer = player2;
                curData = p2Data;
                otherData = p1Data;
            }
            ArrayList<Ship> ships = curData.getShips();
            ArrayList<ArrayList<Integer>> opponentBoard = curData.getOpponentsBoard();
            ArrayList<ArrayList<Integer>> curBoard = otherData.getOpponentsBoard();
            for (Ship s: ships){
                for (ArrayList<Integer> tile: s.getCoords()){
                    int x = tile.get(0);
                    int y = tile.get(1);
                    if (curBoard.get(x).get(y) == 0){
                        curBoard.get(x).set(y, 4);
                    }
                }
            }
            var curBoardRendered = Tile.renderBoard(curBoard);
            var opponentBoardRendered = Tile.renderBoard(opponentBoard);
            String t = "\t\t\t\t\t";
            for (int i=0; i < curBoardRendered.size(); i++){
                curPlayer.message(curBoardRendered.get(i) + t + opponentBoardRendered.get(i));
            }

        } catch(Exception e){
            System.out.println("Error drawing board");
        }
    }

}
