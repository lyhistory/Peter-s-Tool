package io.stormbird.token.management.Util;

import org.json.JSONArray;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Base64;
//import java.security.KeyStore;

public class KeyStoreManager {
    public String privateKeyFilePath = "./desktop/res/keystore.ks";
    private KeyStore keyStore;
    private final static String keyStorePasswordStr="alpha-wallet";
    private final static String keyEntryPasswordStr=getMacID();
    private final static String keyAlias="AdminPrivateKeyAddressPair";
    public KeyStoreManager(){
        try {
            keyStore = KeyStore.getInstance("JCEKS");
            char[] keyStorePassword = keyStorePasswordStr.toCharArray();
            InputStream keyStoreData=null;
            if(keystoreFileExists()) {
                keyStoreData = new FileInputStream(privateKeyFilePath);
            }
            keyStore.load(keyStoreData, keyStorePassword);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void storeKeys(JSONArray keys){
        storeKeys(keys,keyEntryPasswordStr);
        saveKeyStore();
    }
    public JSONArray getKeys(){
        try {
            KeyStore.ProtectionParameter password =
                    new KeyStore.PasswordProtection(keyEntryPasswordStr.toCharArray());
            KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyAlias, password);
            byte[] decodedKey = Base64.getDecoder().decode(keyEntry.getSecretKey().getEncoded());
            return new JSONArray(new String(decodedKey, "UTF-8"));
        }catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean storeKeys(JSONArray keys, String entryPassword){
        try {
            byte[] encodedKey = Base64.getEncoder().encode(keys.toString().getBytes("UTF-8"));//.getDecoder().decode(keys.toString());
            // rebuild key using SecretKeySpec
            SecretKey secretKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);

            keyStore.setEntry(keyAlias, secretKeyEntry, new KeyStore.PasswordProtection(entryPassword.toCharArray()));
        }catch (KeyStoreException e) {
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return true;
    }
    private void saveKeyStore(){
        char[] keyStorePassword = keyStorePasswordStr.toCharArray();
        FileOutputStream keyStoreOutputStream = null;
        try {
            keyStoreOutputStream = new FileOutputStream(privateKeyFilePath);
            keyStore.store(keyStoreOutputStream, keyStorePassword);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean keystoreFileExists(){
        File f = new File(privateKeyFilePath);
        if (f.exists()==false) {
            if(f.getParentFile().exists()==false){
                f.getParentFile().mkdirs();
            }
            return false;
        }
        return true;
    }
    private static String getMacID(){
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e){
            e.printStackTrace();
        }
        return "alpha-wallet";
    }
}
