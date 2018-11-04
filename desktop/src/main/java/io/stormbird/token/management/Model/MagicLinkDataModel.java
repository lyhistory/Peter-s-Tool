package io.stormbird.token.management.Model;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MagicLinkDataModel {
    public String magicLink;
    public long expiry;
    public double price;
    public BigInteger priceWei;
    public BigInteger[] tickets;
    public int ticketCount;
    public String contractAddress;
    public byte[] signature = new byte[65];
    public int contractType;

    public String remark;
    public boolean redeemped;

    public static String getDateStrByValue(BigInteger val,String timezone){
        SimpleDateFormat sourcedf = new SimpleDateFormat("yyyyMMddHHmmssZZZZ");
        SimpleDateFormat targetdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        byte[] bytes=val.toByteArray();
        String dateStr=new String(bytes, Charset.forName("UTF-8"));
        sourcedf.setTimeZone(TimeZone.getTimeZone("GMT" + timezone));
        targetdf.setTimeZone(TimeZone.getTimeZone("GMT" + timezone));
        try {
            Date date=sourcedf.parse(dateStr);
            return targetdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getTimezoneStrByValue(BigInteger val){
        byte[] bytes=val.toByteArray();
        String dateStr=new String(bytes, Charset.forName("UTF-8"));
        Pattern p = Pattern.compile("(\\+\\d{4}|\\-\\d{4})");
        Matcher m = p.matcher(dateStr);
        if (m.find()) {
            return m.group(1);
        }else{
            return "";
        }
    }
    public static String getDateStrByValue(long expiry, String timezone){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT" + timezone));
        expiry = expiry * 1000;
        Date date = new Date(expiry);
        return df.format(date);
    }
//    public static String getTimezoneByValue(long expiry,String timezone){
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
//        df.setTimeZone(TimeZone.getTimeZone("GMT"+timezone));
//        Pattern p = Pattern.compile("(\\+\\d{4})");
//        expiry=expiry*1000;
//        Date date = new Date( expiry);
//
//        date.getTimezoneOffset();
//        Matcher m = p.matcher(df.format(date));
//        if (m.find()) {
//            return m.group(1);
//        }else{
//            return "";
//        }
//    }
}
