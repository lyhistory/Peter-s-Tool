package io.stormbird.token.management.Util;

import io.stormbird.token.entity.EthereumReadBuffer;
import io.stormbird.token.management.Model.MagicLinkDataModel;
import org.web3j.utils.Convert;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;

public class MagicLinkHelper {
    // parse magic link
    public static MagicLinkDataModel parseMagicLink(String link){
        final String importTemplate = "https://app.awallet.io/";
        int offset = link.indexOf(importTemplate);
        if (offset > -1)
        {
            offset += importTemplate.length();
            String linkData = link.substring(offset);
            return readLinkData(linkData);
        }
        return null;
    }
    private static MagicLinkDataModel readLinkData (String linkData) {
        byte[] bytes = Base64.getUrlDecoder().decode(linkData);
        long szabo;
        MagicLinkDataModel data = new MagicLinkDataModel();
        try
        {
            ByteArrayInputStream bas = new ByteArrayInputStream(bytes);
            EthereumReadBuffer ds = new EthereumReadBuffer(bas);

            data.contractType = ds.readByte();
            szabo = ds.toUnsignedLong(ds.readInt());
            data.expiry = ds.toUnsignedLong(ds.readInt());
            data.priceWei = Convert.toWei(BigDecimal.valueOf(szabo), Convert.Unit.SZABO).toBigInteger();
            data.contractAddress = ds.readAddress();
            int ticketCount = (ds.available() - 65) / 32;
            data.tickets =new BigInteger[ticketCount];
            for(int i=0;i<ticketCount;++i){
                data.tickets[i] = ds.readBI();
            }
            data.ticketCount = data.tickets.length;
            //now read signature
            ds.readSignature(data.signature);
            ds.close();
        } catch (Exception e) {
            return null;
        }

        BigInteger microEth = Convert.fromWei(new BigDecimal(data.priceWei), Convert.Unit.SZABO).abs().toBigInteger();
        data.price = microEth.doubleValue() / 1000000.0;

        return data;
    }
}
