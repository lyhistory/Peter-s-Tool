package io.stormbird.token.management.Util;

import io.stormbird.token.management.ConfigManager;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileHelper {
    public static boolean checkFileExists(String filePath){
        File f = new File(filePath);
        if (f.exists()==false) {
            if(f.getParentFile().exists()==false){
                f.getParentFile().mkdirs();
            }
            return false;
        }
        return true;
    }
    public static boolean deleteFile(String filePath){
        try{
            File f = new File(filePath);
            if(f.exists()==true){
                f.delete();
            }
        }catch(Exception ex){
            return false;
        }
        return  true;
    }
    public static boolean createFileIfNotExists(String filePath){
        try {
            File f =  new File(filePath);
            if (f.exists()==false) {
                if(f.getParentFile().exists()==false){
                    f.getParentFile().mkdirs();
                }
                f.createNewFile();
            }
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "unexpected error try to create "+filePath);
            return false;
        }
    }
    public static void writeFile(String filePath, byte[] data) throws IOException {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(filePath, "rw");
            raf.write(data);
        } finally {
            if (raf != null) {
                raf.close();
            }
        }
    }

    public static void saveToFile(String sourceFilePath, String destFilePath){
        try(OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(destFilePath), StandardCharsets.UTF_8)) {
            BufferedWriter out = new BufferedWriter(fw);
            File fin = new File(sourceFilePath);
            FileInputStream fis = new FileInputStream(fin);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
            String aLine = null;
            while ((aLine = in.readLine()) != null) {
                out.write(aLine);
                out.newLine();
            }
            in.close();
            out.close();
            fw.flush();
        }catch (Exception io){

        }
    }
}
