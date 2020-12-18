package Client;

// A simple Client Server Protocol .. Client for Echo Server

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Client {

    private final String hostname;
    private final int port;
    private ReadThread input;
    private WriteThread output;
    private Boolean inLobby = false;
    private Key publicKey;
    private PrivateKey privateKey;


    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;

    }

    public void initialize() {
        try {
            Socket socket = new Socket(hostname, port);
            this.input = new ReadThread(socket, this);
            this.output = new WriteThread(socket, this);
            this.publicKey = readPublicKey("./src/main/java/Client/public.key");
            input.start();
            output.start();
            var distributablePK = generateNewKeys();
            output.sendKeyToServer(distributablePK);

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            System.out.println("Invalid Key Spec");
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    private Key readPublicKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        File filePublicKey = new File(path);
        FileInputStream fis = new FileInputStream(path);
        byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
        fis.read(encodedPublicKey);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
                encodedPublicKey);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public boolean isInLobby(){
        return inLobby;
    }

    public void inLobby(boolean state){
        this.inLobby = state;
    }

    public Key getPublicKey(){
        return this.publicKey;
    }

    public Key getPrivateKey(){
        return this.privateKey;
    }

    /*
    Method to generate a private/public key. The PrivateKey is saved to the Client, and
    the PublicKey is returned for use.
     */
    private PublicKey generateNewKeys() throws IOException, NoSuchAlgorithmException {
        KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        var publicKey = keys.getPublic();
        this.privateKey = keys.getPrivate();
        return publicKey;
    }

    public static void main(String[] args) throws IOException{
        var client = new Client("127.0.0.1", 5000);
        client.initialize();
    }
}