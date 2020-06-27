package io.stormbird.token.management.Util;

import io.stormbird.token.entity.EthereumReadBuffer;
import io.stormbird.token.management.Model.*;
import org.web3j.utils.Convert;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class MagicLinkHelper {
    public static String importTemplate = "https://app.awallet.io/";
    // parse magic link
    public static MagicLinkDataModel parseMagicLink(String link){

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

    public static void generateMagicLink(int rowNum,Map<Integer, MagicLinkToolViewModel> _magicLinkViewMap,String contractAddress,String privateKey){
        Map<String,BigInteger> encodedValueMap=new ConcurrentHashMap<>();
        String tokenidStr="";
        BigInteger tokenid=BigInteger.valueOf(0);
        //
        MagicLinkToolViewModel magicLinkViewModel = _magicLinkViewMap.get(rowNum);
        if(magicLinkViewModel.ComboBoxForXMLList!=null){
            for(int i=0;i<magicLinkViewModel.ComboBoxForXMLList.size();++i){
                JComboBox comboBox=magicLinkViewModel.ComboBoxForXMLList.get(i);
                ComboBoxDataModel.ComboBoxOption c = (ComboBoxDataModel.ComboBoxOption)comboBox.getSelectedItem();
                BigInteger value=new BigInteger(c.getKey().toString(16),16);
                encodedValueMap.put(comboBox.getName(),value);
            }
        }
        if(magicLinkViewModel.TextFieldForXMLMap!=null){
            for(JTextField textField:magicLinkViewModel.TextFieldForXMLMap.keySet()){
                TextFieldDataModel model=magicLinkViewModel.TextFieldForXMLMap.get(textField);
                BigInteger encodedValue = BigInteger.valueOf(0);
                String inputStr = textField.getText().toString();
                if (inputStr != null && inputStr.length() > 0) {
                    if (model.as.equals("UTF8")) {
                        byte[] bytes = inputStr.getBytes(Charset.forName("UTF-8"));
                        encodedValue = new BigInteger(bytes);
                    } else if (model.as.equals("Unsigned")) {
                        encodedValue = new BigInteger(inputStr);
                    }
                    encodedValue = encodedValue.shiftLeft(model.getBitshift()).and(model.getBitmask());
                    encodedValueMap.put(model.id,encodedValue);
                }
            }
        }
        if(magicLinkViewModel.DateTimePickerMap!=null){
            for(DateTimePickerViewModel dateTimePickerViewModel:magicLinkViewModel.DateTimePickerMap.keySet()){
                TextFieldDataModel model=magicLinkViewModel.DateTimePickerMap.get(dateTimePickerViewModel);
                try{
                    BigInteger encodedValue = BigInteger.valueOf(0);
                    DateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
                    DateFormat targetFormat = new SimpleDateFormat("yyyyMMddHHmmssZZZZ");
                    ComboBoxSimpleItem item = (ComboBoxSimpleItem)dateTimePickerViewModel.TimeZone.getSelectedItem();
                    String dateStr = String.format("%s%s",dateTimePickerViewModel.DateTimePickerTime.getText(),item.getKey());
                    displayFormat.setTimeZone(TimeZone.getTimeZone("GMT"+item.getKey()));
                    targetFormat.setTimeZone(TimeZone.getTimeZone("GMT"+item.getKey()));
                    Date date = displayFormat.parse(dateStr);
                    String targetTimeStr = targetFormat.format(date);
                    if (targetTimeStr != null && targetTimeStr.length() > 0) {
                        byte[] bytes = targetTimeStr.getBytes(Charset.forName("UTF-8"));
                        encodedValue = new BigInteger(bytes);
                        encodedValue = encodedValue.shiftLeft(model.getBitshift()).and(model.getBitmask());
                    }
                    encodedValueMap.put(model.id,encodedValue);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Opoos!"+ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        // now we can get the tokenID!
        for(String key:encodedValueMap.keySet()){
            tokenid=tokenid.or(encodedValueMap.get(key));
        }

        MagicLinkToolDataModel magicLinkDataModel = new MagicLinkToolDataModel();
        magicLinkDataModel.TokenIDs = new BigInteger[]{tokenid};
        magicLinkDataModel.Price = magicLinkViewModel.TextFieldPriceInEth.getText();

        DateTimePickerViewModel dateTimePicker=magicLinkViewModel.DateTimePickerExpire;
        ComboBoxSimpleItem item = (ComboBoxSimpleItem)dateTimePicker.TimeZone.getSelectedItem();
        magicLinkDataModel.Expiry=String.format("%s%s",dateTimePicker.DateTimePickerTime.getText(),item.getKey());
        magicLinkDataModel.timeZone = TimeZone.getTimeZone("GMT"+item.getKey());
        magicLinkDataModel.ContractAddress=contractAddress;


        magicLinkDataModel.generateMagicLink(privateKey);
        //update UI
        JTextField textFieldMagicLink = magicLinkViewModel.TextFieldMagicLink;
        textFieldMagicLink.setText(magicLinkDataModel.MagicLink);
        JTextField textFieldRemark = magicLinkViewModel.TextFieldRemark;

        tokenidStr=tokenid.toString(16);
        while (tokenidStr.length() < 64) {
            tokenidStr = "0" + tokenidStr;
        }
        textFieldRemark.setText("(change whatever u want),Row:"+Integer.toString(rowNum)+",TokenID:"+tokenidStr);
    }
}
