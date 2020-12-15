package Server;

import GameLogic.Lobby;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    private final ServerSocket server;
    private final ArrayList<UserThread> userInstances;
    private final ArrayList<Lobby> lobbies;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.userInstances = new ArrayList<>();
        this.lobbies = new ArrayList<>();
    }

    private void acceptClients(){
        Socket s;
        try {
            while (true){
                s = server.accept();
                UserThread st = new UserThread(s, this);
                st.start();
                userInstances.add(st);
            }
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("Connection Error");
        }
    }

    public void addLobby(Lobby l){
        lobbies.add(l);
    }

    public ArrayList<Lobby> getLobbies(){
        return lobbies;
    }

    public ArrayList<UserThread> getInstances(){
        return userInstances;
    }


    public static void main(String[] args) throws IOException {
        var server = new Server(5000);
        server.acceptClients();
    }
}

