package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadThread extends Thread {
    private final Socket socket;
    private BufferedReader input;
    private Client client;

    public ReadThread (Socket s, Client client) {
        this.socket = s;
        this.client = client;

        try {
            this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        }
        catch (IOException u){
            System.out.println(u.getMessage());
        }
    }

    public void run(){
        try {
            System.out.println("\nCommands: \n\tCreate Lobby\n\tList Lobbies\n\tJoin Lobby {Lobby ID}");
            while (true) {
                String response = input.readLine();
                if (response.startsWith("Command: ")){
                    matchCommand(response.replace("Command: ", ""));
                    if (!client.isInLobby()){
                        System.out.println("\nCommands: \n\tCreate Lobby\n\tList Lobbies\n\tJoin Lobby {Lobby ID}");
                    } else {
                        System.out.println("\nCommands:\n\tFire {x-pos} {y-pos}");
                    }
                } else {
                    System.out.println(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void matchCommand(String response) {
        switch(response){
            case "Imagine I'm a command":
                System.out.println("Call the command");
                break;
            case "Joined lobby successfully!":
                System.out.println(response);
                client.inLobby(true);
                break;
            default:
                System.out.println(response);
                System.out.println("Unknown command, please check your spelling and try again!");
        }
    }

}
