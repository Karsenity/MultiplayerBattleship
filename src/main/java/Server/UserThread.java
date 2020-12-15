package Server;

import GameLogic.Lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class UserThread extends Thread{

    UUID userID;
    Socket serverSocket;
    Server server;
    Lobby lobby;
    BufferedReader userInput = null;
    PrintWriter userOutput=null;


    public UserThread(Socket s, Server server){
        this.serverSocket = s;
        this.server = server;
        this.userID = UUID.randomUUID();
        initialize();

    }

    public void run() {
        String request = "";

        try {
            while(request.compareTo("QUIT")!=0){
                request = userInput.readLine();
                matchCommand(request);
                //userOutput.println(line);
            }
        }
        catch (IOException e) {
            System.out.println("IO Error/ Client "+ this.getName() +" terminated abruptly");
        }
        catch(NullPointerException e){
            System.out.println("Client "+ this.getName() +" Closed");
        }

        finally {
            closeConnection();
        }
    }

    private void matchCommand(String request){
        try {
            // Create Lobby
            if (request.equals("Create Lobby")) {
                createLobby();
            }
            // List Lobbies
            else if (request.equals("List Lobbies")) {
                userOutput.println("Lobbies: ");
                for (Lobby l : server.getLobbies()) {
                    userOutput.println("\t" + l.getGameID());
                }
            }
            // Join Lobby
            else if (request.contains("Join Lobby ")) {
                String[] req = request.split(" ", 3);
                joinLobby(UUID.fromString(req[2].strip()));
            }
            // Fire at ship
            else if (request.startsWith("Fire ")) {
                if (this.lobby.getPlayer2() != null){
                    request = request.replace("Fire ", "");
                    String[] positions = request.split(" ");
                    fire(Integer.parseInt(positions[0]), Integer.parseInt(positions[1]));
                } else {
                    userOutput.println("ERROR: You can't fire until player2 joins");
                }

            } else {
                userOutput.println("Command: ");
            }
        } catch(Exception e){
            userOutput.println("ERROR: Unexpected input");
        }
    }

    public void fire(int x, int y){
        this.lobby.makeMove(this.userID, x, y);
    }

    public void createLobby(){
        if (this.lobby == null) {
            this.lobby = new Lobby(this);
            server.addLobby(this.lobby);
            userOutput.println("Command: Joined lobby successfully!");
        }
    }

    public void joinLobby(UUID gameID){
        if (this.lobby == null){
            for (Lobby l: server.getLobbies()){
                if (l.getGameID().equals(gameID) && l.getPlayer2() == null){
                    l.setPlayer2(this);
                    this.lobby = l;
                    userOutput.println("Command: Joined lobby successfully!");
                    break;
                }
            }
        } else {
            userOutput.println("ERROR: couldn't join lobby specified");
        }
    }

    public void message(String message){
        userOutput.println(message);
    }

    private void initialize(){
        try {
            this.userInput = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            this.userOutput = new PrintWriter(serverSocket.getOutputStream(), true);

        }
        catch(IOException e) {
            System.out.println("IO error in server thread");
        }
    }

    public void closeConnection() {
        try {
            if (userInput!=null){
                userInput.close();
            }
            if(userOutput!=null){
                userOutput.close();
            }
            if (serverSocket!=null){
                serverSocket.close();
            }
        }
        catch(IOException ie){
            System.out.println("Socket Close Error");
        }
    }

    public UUID getUserID(){
        return this.userID;
    }

}