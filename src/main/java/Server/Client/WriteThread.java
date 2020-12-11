package Server.Client;

import java.io.*;
import java.net.Socket;

public class WriteThread extends Thread{
    private PrintWriter writer;
    private Socket socket;
    private Client client;

    public WriteThread(Socket s){
        this.socket = s;

        try {
            OutputStream output = socket.getOutputStream();
            this.writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        var console = new BufferedReader(new InputStreamReader(System.in));
        String text = "default";
        while (true) {
            try {
                text = console.readLine();
                this.writer.println(text);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
