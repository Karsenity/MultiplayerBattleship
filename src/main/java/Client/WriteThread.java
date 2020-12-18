package Client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

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
                var request = encryptMessage(text);
                this.output.println(request);
            }
        } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void sendKeyToServer(PublicKey key) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        //turn the key into bytes
        var bytes = key.getEncoded();
        //Base64 encode key and turn it to a string
        bytes = Base64.getEncoder().encode(bytes);
        var message = new String(bytes);
        // Key is too big to send in one go, so we split it in half
        var part1 = message.substring(0, message.length()/2);
        var part2 = message.substring(message.length()/2);
        // Attach message so the server knows what it's receiving
        part1 = "Key1 " + part1;
        part2 = "Key2 " + part2;
        // now encrypt the messages and send them to the server
        part1 = encryptMessage(part1);
        part2 = encryptMessage(part2);
        this.output.println(part1);
        this.output.println(part2);
    }

    /*
    Method for encrypting a message to be sent to the server
        1.) Encrypt the message using the PublicKey for the server
        2.) Encode the message using Base64 for transmission
        3.) Convert the message to a string for sending across the channel
     */
    public String encryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, client.getPublicKey());
        var encryptedMessage = cipher.doFinal(message.getBytes());
        var encodedMessage = Base64.getEncoder().encode(encryptedMessage);
        return new String(encodedMessage);
    }

}
