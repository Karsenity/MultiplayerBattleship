package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server {

    private final ServerSocket server;
    private ArrayList<ServerThread> instances;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port);
        this.instances = new ArrayList<>();
    }

    private void acceptClients(){
        Socket s;

        while (true){
            try {
                s = server.accept();
                System.out.println("Added Client to Server");
                ServerThread st = new ServerThread(s, this);
                st.start();
                instances.add(st);

            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("Connection Error");

            }
        }
    }

    public ArrayList<ServerThread> getInstances(){
        return instances;
    }


    public static void main(String[] args) throws IOException {
        var server = new Server(5000);
        server.acceptClients();
    }
}

