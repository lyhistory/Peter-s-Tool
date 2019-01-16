package io.stormbird.token.management.Util;

import io.stormbird.token.management.Model.ComboBoxSimpleItem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class JSwingHelper {
    public static GridBagConstraints getGridConstraints(int gridx, int gridy, double weightx, int align){
        GridBagConstraints colConstraints = new GridBagConstraints();
        //colConstraints.fill = GridBagConstraints.BOTH;
        colConstraints.anchor = (align == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        colConstraints.fill = (align == 0) ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        colConstraints.weightx=weightx;
        colConstraints.weighty=1.0;
        colConstraints.gridwidth=1;
        colConstraints.gridx = gridx;
        colConstraints.gridy = gridy;
        return colConstraints;
    }

    public static void setSelectedItem(String key,JComboBox comboBox) {
        int index = -1;
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboBoxSimpleItem item = (ComboBoxSimpleItem)comboBox.getItemAt(i);
            if (key.equals(item.getKey())) {
                index = i;
                comboBox.setSelectedItem(item);
                break;
            }
        }
    }

    public static JComboBox createDatePicker(final JPanel dateTimePickerPane, JButton dateTimePicker){
        JComboBox comboBoxTimezone;
        dateTimePickerPane.setLayout(new GridBagLayout());
        dateTimePickerPane.setBorder(BorderFactory.createEmptyBorder());
        GridBagConstraints col1Constraints = new GridBagConstraints();
        col1Constraints.fill = GridBagConstraints.BOTH;
        col1Constraints.anchor=GridBagConstraints.CENTER;
        col1Constraints.weightx=0.7;
        col1Constraints.weighty=1.0;
        col1Constraints.gridwidth=1;
        col1Constraints.gridx = 0;
        col1Constraints.gridy = 0;
        GridBagConstraints col2Constraints = new GridBagConstraints();
        col2Constraints.fill = GridBagConstraints.BOTH;
        col2Constraints.anchor=GridBagConstraints.CENTER;
        col2Constraints.weightx=0.3;
        col2Constraints.weighty=1.0;
        col2Constraints.gridwidth=1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = 0;
        ArrayList<ComboBoxSimpleItem> timezoneOptions = new ArrayList<ComboBoxSimpleItem>();
        timezoneOptions.add(new ComboBoxSimpleItem("-1200","-1200"));
        timezoneOptions.add(new ComboBoxSimpleItem("-1100","-1100"));
        timezoneOptions.add(new ComboBoxSimpleItem("-1000","-1000"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0930","-0930"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0900","-0900"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0800","-0800"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0700","-0700"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0600","-0600"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0500","-0500"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0400","-0400"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0330","-0330"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0300","-0300"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0200","-0200"));
        timezoneOptions.add(new ComboBoxSimpleItem("-0100","-0100"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0000","+0000"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0100","+0100"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0200","+0200"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0300","+0300"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0330","+0330"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0400","+0400"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0430","+0430"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0500","+0500"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0530","+0530"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0600","+0600"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0630","+0630"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0700","+0700"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0800","+0800"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0845","+0845"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0900","+0900"));
        timezoneOptions.add(new ComboBoxSimpleItem("+0930","+0930"));
        timezoneOptions.add(new ComboBoxSimpleItem("+1000","+1000"));
        timezoneOptions.add(new ComboBoxSimpleItem("+1030","+1030"));
        timezoneOptions.add(new ComboBoxSimpleItem("+1100","+1100"));
        timezoneOptions.add(new ComboBoxSimpleItem("+1200","+1200"));
        timezoneOptions.add(new ComboBoxSimpleItem("+1245","+1245"));
        timezoneOptions.add(new ComboBoxSimpleItem("+1300","+1300"));
        timezoneOptions.add(new ComboBoxSimpleItem("+1400","+1400"));

        comboBoxTimezone = new JComboBox(timezoneOptions.toArray());

        setSelectedItem(getCurrentTimezone(),comboBoxTimezone);
        dateTimePickerPane.add(dateTimePicker,col1Constraints);
        dateTimePickerPane.add(comboBoxTimezone,col2Constraints);
        return comboBoxTimezone;
    }

    public static String getCurrentTimezone(){
        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();
        String currentTimezone="";

        int offset = timeZone.getRawOffset();
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;
        String offsetHrsStr,offsetMinsStr="00";
        if(offsetHrs>-10||offset<10){
            if(offset<0) {
                offsetHrsStr = String.format("-0%d", (0-offsetHrs));
            }else{
                offsetHrsStr = String.format("+0%d",offsetHrs);
            }
        }else{
            if(offset<0) {
                offsetHrsStr = String.format("-%d", (0-offsetHrs));
            }else{
                offsetHrsStr = String.format("+%d",offsetHrs);
            }
        }
        if(offsetMins<0){
            offsetMinsStr=String.valueOf((0-offsetMins));
        }else if(offsetMins>0){
            offsetMinsStr=String.valueOf(offsetMins);
        }
        currentTimezone = String.format("%s%s", offsetHrsStr,offsetMinsStr);
        return currentTimezone;
    }
}
