package io.stormbird.token.management.Util;

import io.stormbird.token.management.ConfigManager;
import io.stormbird.token.management.Model.MagicLinkDataModel;
import io.stormbird.token.management.Model.MagicLinkToolViewModel;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionDataHelper {
    private static MeetupContractHelper contractHelper;

    public static void initContract(String contractAddress,String networkid,String privateKey,String ownerAddress){
        contractHelper = new MeetupContractHelper(contractAddress,networkid,privateKey,ownerAddress);
    }
    public static String getContractOwner(){
        return contractHelper.contractOwner;
    }
    public static boolean isConnectedToWeb3(){
        if(contractHelper!=null){
            return contractHelper.isConnected;
        }
        return false;
    }
    public static Map<String,String> loadWalletFromKeystore(){
        Map<String,String> keys = new ConcurrentHashMap<>();
        try {
            KeyStoreManager manager=new KeyStoreManager();
            JSONArray array=manager.getKeys();
            for(int i=0; i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                keys.put(obj.getString("address"),obj.getString("privatekey"));
            }

        }catch (Exception ex){
        }
        return keys;
    }
    public static void savePrivateKey(Map<String,String> keys){
        if(keys!=null&&keys.size()>0) {
            try {
                //BufferedWriter writer = Files.newBufferedWriter(Paths.get(privateKeyFilePath));
                //CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("PrivateKey", "Address"));
                JSONArray array=new JSONArray();
                for (String address:keys.keySet()) {
                    //csvPrinter.printRecord(item.getValue(), item.getKey());
                    JSONObject jo=new JSONObject();
                    jo.put("privatekey",keys.get(address));
                    jo.put("address",address);
                    array.put(jo);
                }
                KeyStoreManager manager=new KeyStoreManager();
                manager.storeKeys(array);
                //csvPrinter.flush();
                //writer.close();
            } catch (Exception ex) {

            }
        }
    }
    public static void reloadMagicLink(ArrayList<MagicLinkDataModel> magicLinkDataModelList){
        for(int i=0;i<magicLinkDataModelList.size();++i){
            magicLinkDataModelList.get(i).redeemped=checkStatus(magicLinkDataModelList.get(i).tickets[0]);
        }
    }
    public static ArrayList<MagicLinkDataModel> loadMagicLinksFromCSV(){
        ArrayList<MagicLinkDataModel> magicLinkDataModelList=new ArrayList<MagicLinkDataModel>();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(ConfigManager.magicLinksCSVPath));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int i=0;
            for (CSVRecord csvRecord : csvParser) {
                if(i!=0) {
                    MagicLinkDataModel model = MagicLinkHelper.parseMagicLink(csvRecord.get(0));
                    if (model != null) {
                        model.magicLink = csvRecord.get(0);
                        model.remark = csvRecord.get(1);
                        model.redeemped = checkStatus(model.tickets[0]);
                        model.enabled = !model.redeemped;
                        magicLinkDataModelList.add(model);
                    }
                }
                ++i;
            }
        }catch (Exception ex){
            return null;
        }
        return magicLinkDataModelList;
    }
    public static void saveMagicLinksToCSV(Map<Integer, MagicLinkToolViewModel> dataMap){
        if(dataMap.size()>0) {
            if (FileHelper.createFileIfNotExists(ConfigManager.magicLinksCSVPath)) {
                try {
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(ConfigManager.magicLinksCSVPath));
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("MagicLink", "Remark"));

                    for (Integer index : dataMap.keySet()) {
                        MagicLinkToolViewModel magicLinkViewModel = dataMap.get(index);
                        if (magicLinkViewModel.TextFieldMagicLink.getText() != null
                                && magicLinkViewModel.TextFieldMagicLink.getText().length() > 0) {
                            csvPrinter.printRecord(magicLinkViewModel.TextFieldMagicLink.getText(),
                                    magicLinkViewModel.TextFieldRemark.getText());
                        }
                    }
                    csvPrinter.flush();
                    writer.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Something wrong saving magiclinks to csv.");
                }
            }
        }
    }

    private static boolean checkStatus(BigInteger tokenID){
        if(contractHelper!=null) {
            MeetupContractHelper.RedeemStatus status = contractHelper.checkSpawnableTokenRedeemStatus(tokenID);
            if (status == MeetupContractHelper.RedeemStatus.Redeemed) {
                return true;
            }
        }
        return false;
    }
}
