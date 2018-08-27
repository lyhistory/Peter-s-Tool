package io.stormbird.token.management.Model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MagicLinkViewModel {
    public List<JComboBox> ComboBoxForXMLList;
    public void setComboBoxForXMLList(JComboBox comboBox){
        if(this.ComboBoxForXMLList==null){
            this.ComboBoxForXMLList = new ArrayList<>();
        }
        this.ComboBoxForXMLList.add(comboBox);
    }

    public Map<JTextField,TextFieldDataModel> TextFieldForXMLMap;
    public void setTextFieldForXMLMap(JTextField textField,TextFieldDataModel textFieldDataModel){
        if(this.TextFieldForXMLMap==null){
            this.TextFieldForXMLMap=new ConcurrentHashMap<>();
        }
        this.TextFieldForXMLMap.put(textField,textFieldDataModel);
    }

    public Map<DateTimePickerViewModel,TextFieldDataModel> DateTimePickerMap;
    public void setDateTimePickerMap(JButton dateTimePickerTime,JComboBox timeZoneTime,TextFieldDataModel textFieldDataModel){
        if(this.DateTimePickerMap==null){
            this.DateTimePickerMap=new ConcurrentHashMap<>();
        }
        DateTimePickerViewModel model = new DateTimePickerViewModel();
        model.DateTimePickerTime=dateTimePickerTime;
        model.TimeZone=timeZoneTime;
        this.DateTimePickerMap.put(model,textFieldDataModel);
    }

    public DateTimePickerViewModel DateTimePickerExpire;
    public void setDateTimePickerExpire(JButton dateTimePickerTime,JComboBox timeZoneTime){
        this.DateTimePickerExpire=new DateTimePickerViewModel();
        this.DateTimePickerExpire.DateTimePickerTime=dateTimePickerTime;
        this.DateTimePickerExpire.TimeZone=timeZoneTime;
    }


    public JTextField TextFieldMagicLink;
    public JTextField TextFieldRemark;

    //todo
    public JTextField TextFieldOwner;

}

