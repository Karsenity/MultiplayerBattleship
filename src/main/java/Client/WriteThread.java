package Client;

import java.io.*;
import java.net.Socket;

public class WriteThread extends Thread{
    private Socket socket;
    private PrintWriter output;
    private Client client;
    private BufferedReader input;

    public WriteThread(Socket s, Client client){
        this.socket = s;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            this.output = new PrintWriter(output, true);
            this.input = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {
        String text;
        try {
            while (true) {
                text = this.input.readLine();
                this.output.println(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
