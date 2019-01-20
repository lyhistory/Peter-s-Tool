package io.stormbird.token.management;

import java.io.File;
import java.io.InputStream;

public class ConfigManager {

    public static String ticketXMLTemplatePath = "./desktop/res/MeetupContract.xml";
    public static String ticketSignedXMLFilePath="./desktop/res/";
    public static String magicLinksCSVPath = "./desktop/res/magiclinks.csv";

    public static String privateKeyFilePath = "./desktop/res/keystore.ks";

    public String init(){
        File f = new File(ConfigManager.ticketXMLTemplatePath);
        if(f.exists()==false){
            return "MeetupContract.xml Template missing!";
        }
        return "";
    }

}
