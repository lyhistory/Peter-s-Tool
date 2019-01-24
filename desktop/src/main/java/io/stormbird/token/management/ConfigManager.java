package io.stormbird.token.management;

import java.io.File;
import java.io.InputStream;

public class ConfigManager {

    public static String ticketXMLTemplatePath = "./res/MeetupContract.xml";
    public static String ticketSignedXMLFilePath="./res/MeetupContract-signed.xml";
    public static String magicLinksCSVPath = "./res/magiclinks.csv";
    public static String privateKeyFilePath = "./res/keystore.ks";

    public static void init(){
        File f = new File(ConfigManager.ticketXMLTemplatePath);
        if(f.exists()==false){
            ticketXMLTemplatePath = "./desktop/res/MeetupContract.xml";
            ticketSignedXMLFilePath="./desktop/res/MeetupContract-signed.xml";
            magicLinksCSVPath = "./desktop/res/magiclinks.csv";
            privateKeyFilePath = "./desktop/res/keystore.ks";
        }
    }

}
