package io.stormbird.token.management.Util;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

public class CryptoHelper {
    private static final String ALGO_NAME_EC = "EC";

    private static final String ALGORITHM = "ECDSA";
    private static final String SIGALG = "SHA256withECDSA";
    private static final String CURVE_NAME = "secp256k1";
    private static final ECDomainParameters dp;
    private static final ECCurve curve;
    private static final ECNamedCurveSpec p;
    static {
        X9ECParameters xp = ECUtil.getNamedCurveByName(CURVE_NAME);
        p = new ECNamedCurveSpec(CURVE_NAME, xp.getCurve(), xp.getG(), xp.getN(), xp.getH(), null);
        curve = EC5Util.convertCurve(p.getCurve());
        org.bouncycastle.math.ec.ECPoint g = EC5Util.convertPoint(curve, p.getGenerator(), false);
        BigInteger n = p.getOrder();
        BigInteger h = BigInteger.valueOf(p.getCofactor());
        dp = new ECDomainParameters(curve, g, n, h);
    }
    public static String getEthAddress(String privateKey){
        Credentials cs = Credentials.create(privateKey);
        String publicKey = cs.getEcKeyPair().getPublicKey().toString(16);
        return cs.getAddress();
    }

    public static ECKeyPair getECKeyPairFromPrivateKey(String privateKey){
        BigInteger privateKeyBI = new BigInteger(privateKey, 16);

        return ECKeyPair.create(privateKeyBI);
    }

    public static KeyPair generatePKCS8(ECKeyPair ecKeyPair) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] bytes = Numeric.toBytesPadded(ecKeyPair.getPublicKey(), 64);
        BigInteger x = Numeric.toBigInt(Arrays.copyOfRange(bytes, 0, 32));
        BigInteger y = Numeric.toBigInt(Arrays.copyOfRange(bytes, 32, 64));
        ECPoint q = curve.createPoint(x, y);
        byte[] priKeyPkcs8Der =convertEcPriKeyToPkcs8Der(new ECPrivateKeyParameters(ecKeyPair.getPrivateKey(), dp),new ECPublicKeyParameters(q, dp));

        FileHelper.writeFile("./desktop/res/pkcs8.key", priKeyPkcs8Der);

        byte[] privateKeyDerByteArray = Files.readAllBytes(Paths.get("./desktop/res/pkcs8.key"));
        KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
        // Read privateKeyDerByteArray from DER file.
        KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyDerByteArray);
        PrivateKey privateKey = kf.generatePrivate(keySpec);

        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256k1");

        ECPoint Q = ecSpec.getG().multiply(((org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey).getD());

        ECPublicKeySpec pubSpec = new ECPublicKeySpec(Q, ecSpec);
        PublicKey publicKeyGenerated = kf.generatePublic(pubSpec);
        return new KeyPair(publicKeyGenerated, privateKey);
    }
    public static byte[] convertEcPriKeyToPkcs8Der(ECPrivateKeyParameters priKey,
                                                   ECPublicKeyParameters pubKey) throws IOException {
        ECDomainParameters domainParams = priKey.getParameters();
        ECParameterSpec spec = new ECParameterSpec(domainParams.getCurve(), domainParams.getG(),
                domainParams.getN(), domainParams.getH());
        BCECPublicKey publicKey = null;
        if (pubKey != null) {
            publicKey = new BCECPublicKey(ALGO_NAME_EC, pubKey, spec,
                    BouncyCastleProvider.CONFIGURATION);
        }
        BCECPrivateKey privateKey = new BCECPrivateKey(ALGO_NAME_EC, priKey, publicKey,
                spec, BouncyCastleProvider.CONFIGURATION);
        return privateKey.getEncoded();
    }

}
