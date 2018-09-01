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

    public static Date getDateByValue(BigInteger val){
        byte[] bytes=val.toByteArray();
        String dateStr=new String(bytes, Charset.forName("UTF-8"));
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssZZZZ");
        df.setTimeZone(TimeZone.getTimeZone("GMT"+getTimezoneByValue(val)));
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static String getTimezoneByValue(BigInteger val){
        byte[] bytes=val.toByteArray();
        String dateStr=new String(bytes, Charset.forName("UTF-8"));
        Pattern p = Pattern.compile("(\\+\\d{4})");
        Matcher m = p.matcher(dateStr);
        if (m.find()) {
            return m.group(1);
        }else{
            return "";
        }
    }
    public static Date getDateByValue(long expiry,boolean is_milliseconds){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
        if(is_milliseconds==false){
            expiry=expiry*1000;
        }
        Date date = new Date( expiry);
        return date;
    }
    public static String getTimezoneByValue(long expiry,boolean is_milliseconds){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
        Pattern p = Pattern.compile("(\\+\\d{4})");
        if(is_milliseconds==false){
            expiry=expiry*1000;
        }
        Date date = new Date( expiry);
        Matcher m = p.matcher(df.format(date));
        if (m.find()) {
            return m.group(1);
        }else{
            return "";
        }
    }
}
