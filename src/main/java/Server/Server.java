package Server;

import GameLogic.Lobby;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;


public class Server {

    private final ServerSocket server;
    private final ArrayList<UserThread> userInstances;
    private final ArrayList<Lobby> lobbies;
    private final Key privateKey;

    public Server(int port) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        this.server = new ServerSocket(port);
        this.userInstances = new ArrayList<>();
        this.lobbies = new ArrayList<>();
        this.privateKey = readPrivateKey();
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

    private Key readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File filePrivateKey = new File("./src/main/java/Server/private.key");
        FileInputStream fis = new FileInputStream("./src/main/java/Server/private.key");
        byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
        fis.read(encodedPrivateKey);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
                encodedPrivateKey);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public byte[] encryptMessage(String message, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    public String decryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] decodedMessage = Base64.getDecoder().decode(message);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.privateKey);
        var decryptedMessage = cipher.doFinal(decodedMessage);
        return new String(decryptedMessage);
    }

    public static void main(String[] args) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        var server = new Server(5000);
        server.acceptClients();
    }

}

