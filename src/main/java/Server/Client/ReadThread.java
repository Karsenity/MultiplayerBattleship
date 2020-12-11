package Server.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    private BufferedReader reader;
    private Socket socket;
    private Client client;

    public ReadThread (Socket s) {
        this.socket = s;

        try {
            this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }
        catch (IOException u){
            System.out.println(u.getMessage());
        }
    }

    public void run(){
        while (true) {
            try {
                String response = reader.readLine();
                System.out.println("\t" + response);
            }
            catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
