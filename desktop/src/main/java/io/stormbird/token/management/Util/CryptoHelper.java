package io.stormbird.token.management.Util;

import org.web3j.crypto.Credentials;

public class CryptoHelper {
    public static String getEthAddress(String privateKey){
        Credentials cs = Credentials.create(privateKey);
        String publicKey = cs.getEcKeyPair().getPublicKey().toString(16);
        return cs.getAddress();
    }
}
