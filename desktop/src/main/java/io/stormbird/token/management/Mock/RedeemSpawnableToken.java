package io.stormbird.token.management.Mock;

import io.stormbird.token.entity.EthereumReadBuffer;
import io.stormbird.token.management.Model.MagicLinkDataModel;
import io.stormbird.token.management.Util.MeetupContractHelper;
import org.web3j.crypto.Sign;
import org.web3j.utils.Convert;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Base64;

public class RedeemSpawnableToken extends JFrame{
    JPanel mainPanel;
    public RedeemSpawnableToken(){
        try {
            mainPanel = new JPanel();

            mainPanel.add(new JLabel("private key:"));
            JTextField privatekeyTextField = new JTextField();
            privatekeyTextField.setColumns(30);
            mainPanel.add(privatekeyTextField);

            mainPanel.add(new JLabel("contract address:"));
            JTextField contractTextField = new JTextField();
            contractTextField.setColumns(30);
            mainPanel.add(contractTextField);

            mainPanel.add(new JLabel("magic link:"));
            JTextField magicLinkTextField = new JTextField();
            magicLinkTextField.setColumns(30);
            mainPanel.add(magicLinkTextField);
            mainPanel.add(new JLabel("recepient:"));
            JTextField recepientAddress =new JTextField();
            recepientAddress.setColumns(10);
            mainPanel.add(recepientAddress);
            JButton buttonRedeem=new JButton();
            buttonRedeem.setText("Click to Import");
            buttonRedeem.setForeground(Color.red);
            buttonRedeem.addActionListener(new ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   String contractAddr = contractTextField.getText();
                                                   String link = magicLinkTextField.getText();
                                                   String recepient = recepientAddress.getText();
                                                   String privateKey=privatekeyTextField.getText();
                                                   if(link!="") {
                                                       MagicLinkDataModel model=parseMagicLink(link);
                                                       MeetupContractHelper contractHelper = new MeetupContractHelper(contractAddr,privateKey);
                                                       Sign.SignatureData sig = sigFromByteArray(model.signature);
                                                       contractHelper.redeemSpawnableToken(BigInteger.valueOf(model.expiry),Arrays.asList(model.tickets),sig.getV(),sig.getR(),sig.getS(),recepient);
                                                   }
                                               }
                                           });
            mainPanel.add(buttonRedeem);
            this.setContentPane(mainPanel);
            this.setTitle("Mock RedeemSpawnableToken");

            this.setLocationByPlatform(true);
            this.setResizable(true);
            this.pack();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            //log exception
        }
    }
    public static void main(String args[]) {
        RedeemSpawnableToken redeemSpawnableToken = new RedeemSpawnableToken();
        redeemSpawnableToken.setVisible(true);
    }
    // parse magic link
    public MagicLinkDataModel parseMagicLink(String link){
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
    private MagicLinkDataModel readLinkData (String linkData) {
        byte[] bytes = Base64.getUrlDecoder().decode(linkData);
        long szabo;
        MagicLinkDataModel data = new MagicLinkDataModel();
        try
        {
            ByteArrayInputStream bas = new ByteArrayInputStream(bytes);
            EthereumReadBuffer ds = new EthereumReadBuffer(bas);

            //data.contractType = ds.readByte();
            szabo = ds.readBI().intValue();;//ds.toUnsignedLong(ds.readInt());
            data.expiry = ds.readBI().longValue();
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
    public static Sign.SignatureData sigFromByteArray(byte[] sig)
    {
        byte   subv = (byte)(sig[64]);
        if (subv < 27) subv += 27;

        byte[] subrRev = Arrays.copyOfRange(sig, 0, 32);
        byte[] subsRev = Arrays.copyOfRange(sig, 32, 64);

        BigInteger r = new BigInteger(1, subrRev);
        BigInteger s = new BigInteger(1, subsRev);

        Sign.SignatureData ecSig = new Sign.SignatureData(subv, subrRev, subsRev);

        return ecSig;
    }
}
