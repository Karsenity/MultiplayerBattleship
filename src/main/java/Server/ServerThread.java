package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread extends Thread{

    Socket socket;
    String line = null;
    BufferedReader is = null;
    PrintWriter os=null;
    Server server;

    public ServerThread(Socket s, Server server){
        this.socket = s;
        this.server = server;
    }

    public void run() {
        initialize();
        String line = "";

        try {
            while(line.compareTo("QUIT")!=0){
                line = is.readLine();
                System.out.println(line);
                os.println(line);
                System.out.println("Response to Client " + this.getName() + "  :  "+line);
                replyAll(line);
            }
        }
        catch (IOException e) {
            line=this.getName(); //reused String line for getting thread name
            System.out.println("IO Error/ Client "+line+" terminated abruptly");
        }
        catch(NullPointerException e){
            line=this.getName(); //reused String line for getting thread name
            System.out.println("Client "+line+" Closed");
        }

        finally {
            closeConnection();
        }
    }

    public void replyAll(String message){
        for (ServerThread st: server.getInstances()){
            if (st != this){
                st.sendMessage("testSource", message);
            }
        }
    }

    public void sendMessage(String source, String message){
        os.println(source + " : " + message);
    }


    private void initialize(){
        try {
            is= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            os=new PrintWriter(socket.getOutputStream(), true);

        }
        catch(IOException e) {
            System.out.println("IO error in server thread");
        }
    }

    private void closeConnection() {
        try {
            if (is!=null){
                is.close();
            }
            if(os!=null){
                os.close();
            }
            if (socket!=null){
                socket.close();
            }
        }
        catch(IOException ie){
            System.out.println("Socket Close Error");
        }
    }

}