import GameLogic.Ship;
import GameLogic.UserGameData;

import javax.crypto.*;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class ShipTest {
    static Key publicKey;
    static Key privateKey;
    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeySpecException {

        publicKey = readPublicKey("./src/main/java/Client/public.key");
        privateKey = readPrivateKey();
        KeyFactory kf = KeyFactory.getInstance("RSA");
        var bytes = publicKey.getEncoded();
        PublicKey copiedKey = kf.generatePublic(new X509EncodedKeySpec(bytes));
        bytes = Base64.getEncoder().encode(bytes);
        //Testing whether or not I can append a string to the beginning of the request
        var test = "Hello|there";
        var part1 = test.substring(0, test.length()/2);
        var part2 = test.substring(test.length()/2);
        System.out.println(part1);
        System.out.println(part2);

    }

    public static byte[] encryptMessage(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    public static String decryptMessage(byte[] message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(message));
    }

    private static Key readPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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

    private static Key readPublicKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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

    private static void generateNewKeys() throws IOException, NoSuchAlgorithmException {
        KeyPair keys = KeyPairGenerator.getInstance("RSA").generateKeyPair();

        publicKey = keys.getPublic();
        privateKey = keys.getPrivate();

        // Store Public Key.
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
                publicKey.getEncoded());
        FileOutputStream fos = new FileOutputStream("./src/main/java/Client" + "/public.key");
        fos.write(x509EncodedKeySpec.getEncoded());
        fos.close();

        // Store Private Key.
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                privateKey.getEncoded());
        fos = new FileOutputStream("./src/main/java/Server" + "/private.key");
        fos.write(pkcs8EncodedKeySpec.getEncoded());
        fos.close();
    }


}
