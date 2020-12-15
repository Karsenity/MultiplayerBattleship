package Client;

// A simple Client Server Protocol .. Client for Echo Server

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {

    private final String hostname;
    private final int port;
    private ReadThread input;
    private WriteThread output;
    private Boolean inLobby = false;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

    }

    public void initialize() {
        try {
            Socket socket = new Socket(hostname, port);
            this.input = new ReadThread(socket, this);
            this.output = new WriteThread(socket, this);
            input.start();
            output.start();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public boolean isInLobby(){
        return inLobby;
    }

    public void inLobby(boolean state){
        this.inLobby = state;
    }


    public static void main(String[] args) throws IOException{
        var client = new Client("127.0.0.1", 5000);
        client.initialize();
    }
}