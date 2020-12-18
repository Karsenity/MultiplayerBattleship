package Client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class ReadThread extends Thread {
    private final Socket socket;
    private BufferedReader input;
    private Client client;

    public ReadThread (Socket s, Client client) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
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
                String answer = input.readLine();
                String response = decryptMessage(answer);
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
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private void matchCommand(String response) {
        switch (response) {
            case "Joined lobby successfully!" -> {
                System.out.println(response);
                client.inLobby(true);
            }
            default -> {
                System.out.println(response);
                System.out.println("Unknown command, please check your spelling and try again!");
            }
        }
    }

    /*
    Method for decrypting messages received from the server.
        1.) First, message is decoded back into byte[]
        2.) Second, message is then translated using the PrivateKey from the parent Client.
        3.) Last, translated message is returned as its original string.
     */
    public String decryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        var decodedMessage = Base64.getDecoder().decode(message);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, client.getPrivateKey());
        return new String(cipher.doFinal(decodedMessage));
    }


}
