package io.stormbird.token.management.Model;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

public class MagicLinkToolDataModel {
    public BigInteger[] TokenIDs;
    public String Price;
    public String Expiry;
    public TimeZone timeZone;
    public String ContractAddress;

    public String MagicLink;
    public int Status; // 0 editable, 1 redeemped

//        MagicLink: https://app.awallet.io/base64(Message|Signature)
//
//        priceInWei		32 bytes (4 bytes MicroEth)
//        expiry			32 bytes (4 bytes unsigned)
//        contractAdss 20 bytesdre
//        tokenIDs		32bytes * count
//
//        signature		65 bytes
//        v 1 byte
//        r 32 bytes
//        s 32 bytes
    public void generateMagicLink(String privateKeyofOrganizer){
        try {
            BigInteger privateKey = new BigInteger(privateKeyofOrganizer, 16);

            BigInteger priceInSzabo = (new BigInteger(Price));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
            df.setTimeZone(this.timeZone);
            Date date = null;
            date = df.parse(this.Expiry);
            BigInteger expiryTimestamp = BigInteger.valueOf(date.getTime() / 1000);

            byte[] linkData = encodeLinkDataForSpawning(priceInSzabo, expiryTimestamp, this.TokenIDs, this.ContractAddress);
            byte[] toSignData = encodeSignDataForSpawning(priceInSzabo, expiryTimestamp, this.TokenIDs, this.ContractAddress);
            Sign.SignatureData signedData = signMagicLink(toSignData, privateKey);
            byte[] signature = covertSigToByte(signedData);
            byte[] completeLink = new byte[linkData.length + signature.length];
            System.arraycopy(linkData, 0, completeLink, 0, linkData.length);
            System.arraycopy(signature, 0, completeLink, linkData.length, signature.length);

            StringBuilder magicLinkSB = new StringBuilder();

            magicLinkSB.append("https://app.awallet.io/");
            byte[] b64 = Base64.getUrlEncoder().encode(completeLink);
            magicLinkSB.append(new String(b64));
            this.MagicLink = magicLinkSB.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private Sign.SignatureData signMagicLink(byte[] signData, BigInteger privateKeyOfOrganiser)
    {
        ECKeyPair ecKeyPair  = ECKeyPair.create(privateKeyOfOrganiser);
        //returns the v, r and s signature params
        return Sign.signMessage(signData, ecKeyPair);
    }
    private static byte[] encodeLinkDataForSpawning (
            BigInteger priceInSzabo,
            BigInteger expiryTimestamp,
            BigInteger[] tickets,
            String contractAddress)
    {
        //0x01: Standard magic link.
        //0x02: Spawn token magic link.
        //0x03: Customisable spawn token link.
        int byteLength=0;
        byte[] leadingbyte = hexStringToBytes("02");
        byteLength=1;//leading lenght

        byte[] priceInMicroWei = priceInSzabo.toByteArray();
        byteLength+=4;//priceinwei
        byte[] expiry = expiryTimestamp.toByteArray();
        byteLength+=4;//expiry
        byteLength+=20;//contract address
        byteLength+=tickets.length*32;//tickets
        ByteBuffer message = ByteBuffer.allocate(byteLength);
        //message.put(leadingbytes);
        byte[] leadingZeros = new byte[4 - priceInMicroWei.length];
        message.put(leadingbyte);
        message.put(leadingZeros);
        message.put(priceInMicroWei);
        byte[] leadingZerosExpiry = new byte[4 - expiry.length];
        message.put(leadingZerosExpiry);
        message.put(expiry);
        byte[] contract = hexStringToBytes(Numeric.cleanHexPrefix(contractAddress));
        message.put(contract);
        for(BigInteger ticket : tickets) {
            //need to pad so that it is 32bytes
            String paddedTicket = Numeric.toHexStringNoPrefixZeroPadded(ticket, 64);
            byte[] ticketAsByteArray = hexStringToBytes(paddedTicket);
            message.put(ticketAsByteArray);
        }
        return message.array();
    }
    private static byte[] encodeSignDataForSpawning (
            BigInteger priceInSzabo,
            BigInteger expiryTimestamp,
            BigInteger[] tickets,
            String contractAddress)
    {
        int byteLength=0;
        BigInteger priceInWei = Convert.toWei(new BigDecimal(priceInSzabo), Convert.Unit.SZABO).toBigInteger();
        byte[] priceInWeiByte = priceInWei.toByteArray();
        byteLength+=32;//priceinwei
        byte[] expiry = expiryTimestamp.toByteArray();
        byteLength+=32;//expiry
        byteLength+=20;//contract address
        byteLength+=tickets.length*32;//tickets
        ByteBuffer message = ByteBuffer.allocate(byteLength);
        //message.put(leadingbytes);
        byte[] leadingZeros = new byte[32 - priceInWeiByte.length];
        message.put(leadingZeros);
        message.put(priceInWeiByte);
        byte[] leadingZerosExpiry = new byte[32 - expiry.length];
        message.put(leadingZerosExpiry);
        message.put(expiry);
        byte[] contract = hexStringToBytes(Numeric.cleanHexPrefix(contractAddress));
        message.put(contract);
        for(BigInteger ticket : tickets) {
            //need to pad so that it is 32bytes
            String paddedTicket = Numeric.toHexStringNoPrefixZeroPadded(ticket, 64);
            byte[] ticketAsByteArray = hexStringToBytes(paddedTicket);
            message.put(ticketAsByteArray);
        }
        return message.array();
    }
    private static byte[] hexStringToBytes(String s)
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    private byte[] covertSigToByte(Sign.SignatureData ecSig)
    {
        byte subV = ecSig.getV();
        byte[] subR = ecSig.getR();
        byte[] subS = ecSig.getS();
        ByteBuffer sig=ByteBuffer.allocate(subR.length+subS.length+1);
        sig.put(subR);
        sig.put(subS);
        sig.put(subV);
        return sig.array();
    }
}
