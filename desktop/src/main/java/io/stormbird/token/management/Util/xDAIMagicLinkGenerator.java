package io.stormbird.token.management.Util;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.MalformedInputException;
import java.util.Base64;

/**
 * Magic link spec:
 * 0x04 as the encoding byte
 * 58 44 41 49 44 52 4F 50 (8 bytes for prefix "XDAIDROP")
 * 4 bytes for the nonce value (can increment from 0)
 * 4 bytes for expiry timestamp
 * 20 bytes for contractAddress
 * 65 bytes for signature (v, r, s)
 * The admin signs 2,3 & 4
 */

public class xDAIMagicLinkGenerator {

    private static final String contractAddress = "0x9d928a678eeaaEfA19eF73E5368830b3476C0678"; //TODO use own contract
    private static final byte encodingByte = 0x04;
    private static final byte[] prefix = new byte[]{
            0x58,
            0x44,
            0x41,
            0x49,
            0x44,
            0x52,
            0x4F,
            0x50
    }; //XDAIDROP in ascii bytes
    private static final int messageByteCount = 40;
    private static final ECKeyPair adminKeyPair = ECKeyPair.create(new BigInteger(
            "30e193c5c48044a0b6cc66fa8e9a08a478cbe9e88ac77123810da5ebf150f84b",
            16)
    ); //TODO use own key

    public static void main(String[] args) {
        //TODO set below values to what you want
        BigInteger nonce = BigInteger.TEN;
        BigInteger expiry = new BigInteger("1648635171");
        int numberOfLinks = 1;
        BigInteger amountOfxDAIPerLink = BigInteger.ONE; //in szabo
        try {
            while(numberOfLinks > 0) {
                //increment nonce by one each time
                byte[] message = formMessageForUniversalLink(nonce, expiry, amountOfxDAIPerLink);
                Sign.SignatureData sigData = Sign.signMessage(message, adminKeyPair);
                ByteBuffer signatureByteBuffer = ByteBuffer.allocate(65);
                signatureByteBuffer.put(sigData.getS());
                signatureByteBuffer.put(sigData.getR());
                signatureByteBuffer.put(sigData.getV());
                byte[] signature = signatureByteBuffer.array();
                String magicLink = formUniversalLinkFromMessageAndSignature(message, signature);
                System.out.println(magicLink);
                System.out.println();
                numberOfLinks--;
                nonce = nonce.add(BigInteger.ONE);
            }
        } catch (MalformedInputException e) {
            e.printStackTrace();
        }
    }
    private static byte[] formMessageForUniversalLink(
            BigInteger nonce,
            BigInteger expiry,
            BigInteger amount
    ) throws MalformedInputException {
        ByteBuffer message = ByteBuffer.allocate(messageByteCount);
        byte[] nonceBytes = padTo4Bytes(nonce.toByteArray());
        byte[] expiryBytes = padTo4Bytes(expiry.toByteArray());
        byte[] amtBytes = padTo4Bytes(amount.toByteArray());
        byte[] contractAddressBytes = Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(contractAddress));
        if(validateMessageComponents(nonceBytes, expiryBytes, contractAddressBytes)) {
            message.put(prefix);
            message.put(nonceBytes);
            message.put(amtBytes);
            message.put(expiryBytes);
            message.put(contractAddressBytes);
            return message.array();
        } else {
            int length = nonceBytes.length + expiryBytes.length + contractAddressBytes.length;
            throw new MalformedInputException(length);
        }
    }

    private static byte[] padTo4Bytes(byte[] item) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        int length = item.length;
        if(length == 4) {
            return item;
        } else {
            int remaining = 4 - length;
            for(int i = remaining; i < 4; i++) {
                byteBuffer.put((byte) 0x00);
            }
            byteBuffer.put(item);
            return byteBuffer.array();
        }
    }

    private static boolean validateMessageComponents(byte[] nonce, byte[] expiry, byte[] contractAddress) {
        return !(nonce.length != 4 || expiry.length != 4 || contractAddress.length != 20);
    }

    //TODO use this over the other function designed to format the link from message and signature or vice versa
    private static String formUniversalLinkFromMessageAndSignature(byte[] message, byte[] signature) {
        byte[] completeLink = new byte[message.length + signature.length + 1];
        completeLink[0] = encodingByte;
        System.arraycopy(message, 0, completeLink, 1, message.length);
        System.arraycopy(signature, 0, completeLink, message.length + 1, signature.length);
        StringBuilder sb = new StringBuilder();
        sb.append("https://app.awallet.io/");
        //URL Safe
        String b64 = Base64.getUrlEncoder().encodeToString(completeLink);
        sb.append(b64);
        //this trade can be claimed by anyone who pushes the transaction through and has the sig
        return sb.toString();
    }

}
