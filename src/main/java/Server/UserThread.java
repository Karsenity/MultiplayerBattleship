package Server;

import GameLogic.Lobby;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class UserThread extends Thread{

    UUID userID;
    Socket serverSocket;
    Server server;
    Lobby lobby;
    BufferedReader userInput = null;
    PrintWriter userOutput=null;
    String k1;
    String k2;
    PublicKey publicKey;

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
                matchCommand(server.decryptMessage(request));
            }
        }
        catch (IOException e) {
            System.out.println("IO Error/ Client "+ this.getName() +" terminated abruptly");
        }
        catch(NullPointerException e){
            System.out.println("Client "+ this.getName() +" Closed");
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void matchCommand(String request){
        try {
            //Assign PublicKey for user
            if (request.startsWith("Key1 ")){
                this.k1 = request.replace("Key1 ", "");
                attemptKeyConstruct();
            }
            else if (request.startsWith("Key2 ")){
                this.k2 = request.replace("Key2 ", "");
                attemptKeyConstruct();
            }
            // Create Lobby
            else if (request.equals("Create Lobby")) {
                createLobby();
            }
            // List Lobbies
            else if (request.equals("List Lobbies")) {
                message("Lobbies: ");
                for (Lobby l : server.getLobbies()) {
                    message("\t" + l.getGameID());
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
                    message("ERROR: You can't fire until player2 joins");
                }

            } else {
                message("Command: ");
            }
        } catch(Exception e){
            message("ERROR: Unexpected input");
        }
    }

    public void fire(int x, int y){
        this.lobby.makeMove(this.userID, x, y);
    }

    public void createLobby(){
        if (this.lobby == null) {
            this.lobby = new Lobby(this);
            server.addLobby(this.lobby);
            message("Command: Joined lobby successfully!");
        }
    }

    public void joinLobby(UUID gameID){
        if (this.lobby == null){
            for (Lobby l: server.getLobbies()){
                if (l.getGameID().equals(gameID) && l.getPlayer2() == null){
                    l.setPlayer2(this);
                    this.lobby = l;
                    message("Command: Joined lobby successfully!");
                    break;
                }
            }
        } else {
            message("ERROR: couldn't join lobby specified");
        }
    }

    public void message(String message){
        try {
            var encryptedMessage = this.server.encryptMessage(message, this.publicKey);
            var encodedMessage = Base64.getEncoder().encode(encryptedMessage);
            userOutput.println(new String(encodedMessage));
        } catch (Exception ex){
            System.out.println("ERROR: Couldn't send message");
        }
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

    private void receiveUserKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //Decrypt the message
        var decryptedKey = Base64.getDecoder().decode(key);
        //Use KeyFactory to rebuild key from byte[]
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.publicKey = kf.generatePublic(new X509EncodedKeySpec(decryptedKey));
    }

    private void attemptKeyConstruct() throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (this.k1 != null && this.k2 != null){
            receiveUserKey(k1 + k2);
        }
    }

    public UUID getUserID(){
        return this.userID;
    }
}