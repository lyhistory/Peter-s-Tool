package io.stormbird.token.management;

import io.stormbird.token.management.CustomComponents.DateTimePicker;
import io.stormbird.token.management.Model.ComboBoxDataModel;
import io.stormbird.token.management.Model.ComboBoxSimpleItem;
import io.stormbird.token.management.Model.TextFieldDataModel;
import io.stormbird.token.management.Model.TokenViewModel;
import org.web3j.utils.Numeric;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.web3j.crypto.*;

/**
 * Layout::
 * mainSplitPane
 * 	mainSplitPane_upperPane
 * 		controlsPane[CENTER]
 * 		ticketsCreateControlPane[SOUTH]
 *
 * 	mainSplitPane_lowerPane(scrollPane)
 * 		mainSplitPane_lowerPane_
 * 			resultControlPane
 * 				titleLable1	titleLable2
 * 				comboBox	scrollPane/normalPane/textPane
 */
public class TokenID extends JFrame{
    private JSplitPane mainSplitPane;
    private JPanel mainSplitPane_upperPane;
    private JScrollPane mainSplitPane_lowerPane;
    private JPanel lowerPane_container;

    //components in upperPane
    private JTextField fieldTokenID;
    private JButton dateTimePickerTime;
    private JComboBox timeZoneTime;
    //components in lowerPane
    private JComboBox comboBoxTickets;
    private JTextField fieldPrivateKey;
    private JTextField fieldContractAddress;
    private JTextField fieldPrice;
    private JTextField fieldPriceInMicroEth;
    private JButton dateTimePickerExpireTime;
    private JComboBox timeZoneExpireTime;

    private static int magicLinkCount=0;

    public InputStream ticketXML = getClass().getResourceAsStream("/TicketingContract.xml");
    private static Map<String,BigInteger> encodedValueMap=new ConcurrentHashMap<>();
    private TokenViewModel tokenViewModel;

    public TokenID(){
        try {
            tokenViewModel=new TokenViewModel(ticketXML, Locale.getDefault());

            initUpperPane();
            initLowerPane();
            mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane_upperPane, mainSplitPane_lowerPane);
            this.setContentPane(mainSplitPane);
            this.setTitle("TokenID Generator");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLocationByPlatform(true);
            this.setResizable(true);
            this.pack();
        } catch (IOException | IllegalArgumentException | SAXException e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        (new TokenID()).setVisible(true);
    }

    private  void onDatePickerChange(JTextField textFieldEncodedValue,int shift,BigInteger bitmask,boolean isUpdateTokenID) {
        try{
            BigInteger encodedValue = BigInteger.valueOf(0);
            DateFormat displayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
            DateFormat targetFormat = new SimpleDateFormat("yyyyMMddHHmmssZZZZ");
            ComboBoxSimpleItem item = (ComboBoxSimpleItem)timeZoneTime.getSelectedItem();
            String dateStr = String.format("%s%s",dateTimePickerTime.getText(),item.getKey());
            Date date = displayFormat.parse(dateStr);
            String targetTimeStr = targetFormat.format(date);
            if (targetTimeStr != null && targetTimeStr.length() > 0) {
                byte[] bytes = targetTimeStr.getBytes(Charset.forName("UTF-8"));
                encodedValue = new BigInteger(bytes);
                encodedValue = encodedValue.shiftLeft(shift).and(bitmask);
            }
            textFieldEncodedValue.setText(encodedValue.toString(16).toUpperCase());
            updateEncodedValueMap(textFieldEncodedValue.getName(), encodedValue, isUpdateTokenID);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Something wrong!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private  void initUpperPane(){
        mainSplitPane_upperPane = new JPanel();
        mainSplitPane_upperPane.setLayout(new BoxLayout(mainSplitPane_upperPane, BoxLayout.Y_AXIS));
        int gridy=0;
        GridBagConstraints col1Constraints = new GridBagConstraints();
        col1Constraints.fill = GridBagConstraints.BOTH;
        col1Constraints.anchor=GridBagConstraints.CENTER;
        col1Constraints.ipadx=10;col1Constraints.ipady=10;
        col1Constraints.weightx=0.3;
        col1Constraints.gridwidth=1;
        col1Constraints.gridx = 0;
        col1Constraints.gridy = gridy;
        GridBagConstraints col2Constraints = new GridBagConstraints();
        col2Constraints.fill = GridBagConstraints.BOTH;
        col2Constraints.anchor=GridBagConstraints.CENTER;
        col2Constraints.ipadx=10;col2Constraints.ipady=10;
        col2Constraints.weightx=0.3;
        col2Constraints.gridwidth=1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = gridy;
        GridBagConstraints col3Constraints = new GridBagConstraints();
        col3Constraints.fill = GridBagConstraints.BOTH;
        col3Constraints.anchor=GridBagConstraints.CENTER;
        col3Constraints.ipadx=10;col3Constraints.ipady=10;
        col3Constraints.weightx = 0.5;
        col3Constraints.gridwidth=1;
        col3Constraints.gridx = 2;
        col3Constraints.gridy = gridy;
        GridBagConstraints col4Constraints = new GridBagConstraints();
        col4Constraints.fill = GridBagConstraints.BOTH;
        col4Constraints.anchor=GridBagConstraints.CENTER;
        col4Constraints.ipadx=10;col4Constraints.ipady=10;
        col4Constraints.weightx = 0.5;
        col4Constraints.gridwidth=1;
        col4Constraints.gridx = 3;
        col4Constraints.gridy = gridy;

        // render column title
        JPanel controlsPane = new JPanel(); //control panel to hold token attribute UI components
        controlsPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlsPane.setLayout(new GridBagLayout());    //full column: attribute name|type|value|encode value
        final JLabel label1 = new JLabel();
        label1.setText("Attribute Name");
        controlsPane.add(label1,col1Constraints);
        final JLabel label2 = new JLabel();
        label2.setText("Type");
        controlsPane.add(label2,col2Constraints);
        final JLabel label3 = new JLabel();
        label3.setText("Value");
        controlsPane.add(label3,col3Constraints);
        final JLabel label4 = new JLabel();
        label4.setText("Encoded Value");
        controlsPane.add(label4,col4Constraints);

        // render dropdown list
        for(ComboBoxDataModel comboBoxDataModel : tokenViewModel.comboBoxDataModelList){
            gridy+=1;
            col1Constraints.gridy=col2Constraints.gridy=col3Constraints.gridy=col4Constraints.gridy=gridy;
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(comboBoxDataModel.name);
            controlsPane.add(labelAttrName,col1Constraints);
            JLabel labelType = new JLabel();
            labelType.setText("Mapping");
            controlsPane.add(labelType,col2Constraints);
            ComboBoxDataModel.ComboBoxOption[] options=comboBoxDataModel.getComboBoxOptions();
            JComboBox comboBox = new JComboBox(options);
            comboBox.setName(comboBoxDataModel.getId());
            comboBox.setEnabled(true);
            controlsPane.add(comboBox,col3Constraints);
            JTextField textFieldEncodedValue = new JTextField();
            textFieldEncodedValue.setEditable(false);
            textFieldEncodedValue.setEnabled(true);
            textFieldEncodedValue.setText(options[0].getKey().toString(16));
            updateEncodedValueMap(comboBox.getName(),options[0].getKey(),false);
            controlsPane.add(textFieldEncodedValue,col4Constraints);
            comboBox.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                    ComboBoxDataModel.ComboBoxOption c = (ComboBoxDataModel.ComboBoxOption)e.getItem();
                    textFieldEncodedValue.setText(c.getKey().toString(16).toUpperCase());
                    BigInteger value=new BigInteger(c.getKey().toString(16),16);
                    updateEncodedValueMap(comboBox.getName(),value,true);
                }

            });
        }
        for(TextFieldDataModel model : tokenViewModel.textFieldDataModelList){
            gridy+=1;
            col1Constraints.gridy=col2Constraints.gridy=col3Constraints.gridy=col4Constraints.gridy=gridy;
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(model.name);
            controlsPane.add(labelAttrName,col1Constraints);
            JLabel labelType = new JLabel();
            labelType.setText(model.type);
            controlsPane.add(labelType,col2Constraints);

            JTextField textFieldEncodedValue = new JTextField();
            textFieldEncodedValue.setName(model.id);
            textFieldEncodedValue.setEditable(false);
            textFieldEncodedValue.setEnabled(true);

            if(model.id.equals("time")) {
                JTextField textFieldHiddenValue = new JTextField();
                textFieldHiddenValue.setVisible(false);
                textFieldHiddenValue.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        warn();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        warn();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        warn();
                    }
                    public void warn() {
                        onDatePickerChange(textFieldEncodedValue,model.getBitshift(),model.getBitmask(),true);
                    }
                });
                JPanel dateTimePickerPane = new JPanel();
                dateTimePickerPane.setLayout(new GridBagLayout());
                dateTimePickerTime = new DateTimePicker(textFieldHiddenValue);
                timeZoneTime = new JComboBox();
                createDatePicker(dateTimePickerPane, dateTimePickerTime,timeZoneTime);
                controlsPane.add(dateTimePickerPane, col3Constraints);
                timeZoneTime.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        onDatePickerChange(textFieldEncodedValue,model.getBitshift(),model.getBitmask(),true);
                    }
                });

                onDatePickerChange(textFieldEncodedValue,model.getBitshift(),model.getBitmask(),false);

            }else{
                JTextField textFieldInput = new JTextField();
                textFieldInput.setEditable(true);
                textFieldInput.setEnabled(true);
                textFieldInput.addKeyListener(new KeyAdapter() {
                    public void keyReleased(KeyEvent e) {
                        try {
                            BigInteger encodedValue = BigInteger.valueOf(0);
                            String inputStr = textFieldInput.getText().toString();
                            if (inputStr != null && inputStr.length() > 0) {
                                if (model.as.equals("UTF8")) {
                                    byte[] bytes = inputStr.getBytes(Charset.forName("UTF-8"));
                                    encodedValue = new BigInteger(bytes);
                                } else if (model.as.equals("Unsigned")) {
                                    encodedValue = new BigInteger(inputStr);
                                }
                                encodedValue = encodedValue.shiftLeft(model.getBitshift()).and(model.getBitmask());
                            }
                            textFieldEncodedValue.setText(encodedValue.toString(16).toUpperCase());
                            updateEncodedValueMap(textFieldEncodedValue.getName(), encodedValue, true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Invalid Data Type! Please check the type",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            textFieldInput.setText("");
                            textFieldInput.requestFocusInWindow();
                        }
                    }
                });
                controlsPane.add(textFieldInput, col3Constraints);
            }
            controlsPane.add(textFieldEncodedValue,col4Constraints);
        }

//        JPanel bottomPane = new JPanel();
//        bottomPane.setLayout(new GridLayout(0,2));
        gridy+=1;
        GridBagConstraints colConstraints = new GridBagConstraints();
        colConstraints.fill = GridBagConstraints.HORIZONTAL;
        colConstraints.weightx = 0.5;
        colConstraints.gridwidth=1;
        colConstraints.gridx = 3;
        colConstraints.gridy = gridy;
        controlsPane.add(new JSeparator(),colConstraints);
        gridy+=1;
        col1Constraints.gridy=col2Constraints.gridy=col3Constraints.gridy=col4Constraints.gridy=gridy;
        JLabel labelTokenID = new JLabel();
        labelTokenID.setText("TokenID");
        controlsPane.add(labelTokenID, col1Constraints);

        fieldTokenID = new JTextField();
        fieldTokenID.setEditable(false);
        fieldTokenID.setEnabled(true);

        controlsPane.add(fieldTokenID,col4Constraints);
        gridy+=1;
        col4Constraints.gridy=gridy;
        JButton buttonAddToTicketList=new JButton();
        buttonAddToTicketList.setText("Add To Ticket List");
        buttonAddToTicketList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                comboBoxTickets.addItem(new ComboBoxSimpleItem(fieldTokenID.getText(),fieldTokenID.getText()));
            }
        });
        controlsPane.add(buttonAddToTicketList,col4Constraints);
        //controlsPane.add(btnCreateMagicLink,col4Constraints);
        mainSplitPane_upperPane.add(controlsPane,BorderLayout.CENTER);
        /*
         * tickets creation Panel
         */
        controlsPane.add(new JSeparator());
        //
        //
        JPanel ticketsCreateControlPane = new JPanel();
        ticketsCreateControlPane.setLayout(new BoxLayout(ticketsCreateControlPane, BoxLayout.Y_AXIS));
        //
        JPanel ticketsOptionsPane = new JPanel();
        gridy+=1;
        col1Constraints.gridy=col2Constraints.gridy=gridy;
        col1Constraints.weightx=0.3;col2Constraints.weightx=0.7;
        JLabel labelTokenIDList=new JLabel();
        labelTokenIDList.setText("Tickets list");
        comboBoxTickets = new JComboBox();
        ticketsOptionsPane.add(labelTokenIDList,col1Constraints);
        ticketsOptionsPane.add(comboBoxTickets,col2Constraints);
        ticketsCreateControlPane.add(ticketsOptionsPane,BorderLayout.NORTH);
        //
        JPanel ticketsCreatePane = new JPanel();
        ticketsCreatePane.setBorder(new EmptyBorder(10, 10, 10, 10));
        ticketsCreatePane.setLayout(new GridBagLayout());
        col1Constraints.weightx= 0.2;
        col2Constraints.weightx=0.7;
        col3Constraints.weightx=0.2;
        col4Constraints.weightx=0.7;
        gridy+=1;
        col1Constraints.gridy=col2Constraints.gridy=col3Constraints.gridy=col4Constraints.gridy=gridy;
        JLabel lablePrivateKey = new JLabel();
        lablePrivateKey.setText("Private Key");
        fieldPrivateKey = new JTextField();
        JLabel labelContractAddress = new JLabel();
        labelContractAddress.setText("Contract Address");
        fieldContractAddress = new JTextField();
        ticketsCreatePane.add(lablePrivateKey,col1Constraints);
        ticketsCreatePane.add(fieldPrivateKey,col2Constraints);
        ticketsCreatePane.add(labelContractAddress,col3Constraints);
        ticketsCreatePane.add(fieldContractAddress,col4Constraints);
        gridy+=1;
        col1Constraints.gridy=col2Constraints.gridy=col3Constraints.gridy=col4Constraints.gridy=gridy;
        JLabel lablePrice = new JLabel();
        lablePrice.setText("Price (eth)");
        fieldPrice = new JTextField();
        fieldPriceInMicroEth = new JTextField();
        JLabel labelExpireTime = new JLabel();
        labelExpireTime.setText("Expire Time");
        ticketsCreatePane.add(lablePrice,col1Constraints);
        ticketsCreatePane.add(fieldPrice,col2Constraints);
        JLabel lablePriceInMicroEth = new JLabel();
        ticketsCreatePane.add(lablePriceInMicroEth,col3Constraints);

        fieldPrice.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    String priceStr = fieldPrice.getText();
                    if(priceStr==null || priceStr.isEmpty()){
                        lablePriceInMicroEth.setText("");
                    }else {
                        long priceInSzabo = Math.round(Double.parseDouble(priceStr) * 1000000);
                        fieldPriceInMicroEth.setText(String.valueOf(priceInSzabo));
                        lablePriceInMicroEth.setText("Micro Eth:"+priceInSzabo);
                    }
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Invalid Data Type! Please check the type",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    fieldPrice.setText("");
                    fieldPrice.requestFocusInWindow();
                }
            }
        });
        ticketsCreatePane.add(fieldPriceInMicroEth);
        fieldPriceInMicroEth.setVisible(false);
        gridy+=1;
        col1Constraints.gridy=col2Constraints.gridy=col3Constraints.gridy=col4Constraints.gridy=gridy;
        ticketsCreatePane.add(labelExpireTime,col1Constraints);
        JPanel dateTimePickerPane = new JPanel();
        dateTimePickerPane.setLayout(new GridBagLayout());
        dateTimePickerExpireTime = new DateTimePicker();
        timeZoneExpireTime = new JComboBox();
        createDatePicker(dateTimePickerPane, dateTimePickerExpireTime,timeZoneExpireTime);
        ticketsCreatePane.add(dateTimePickerPane,col2Constraints);

        gridy+=1;
        col1Constraints.gridy=col2Constraints.gridy=col3Constraints.gridy=col4Constraints.gridy=gridy;
        JButton btnCreateMagicLink = new JButton();
        btnCreateMagicLink.setText("Create Magic-Link");
        btnCreateMagicLink.setActionCommand("Create");
        btnCreateMagicLink.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int size = comboBoxTickets.getItemCount();
                    if (size > 0) {
                        createMagicLink();
                    } else {
                        JOptionPane.showMessageDialog(null, "Please add tokenid into ticket list first!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null, ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        ticketsCreatePane.add(btnCreateMagicLink,col4Constraints);
        ticketsCreateControlPane.add(ticketsCreatePane,BorderLayout.CENTER);
        mainSplitPane_upperPane.add(ticketsCreateControlPane,BorderLayout.SOUTH);
        //
        //pane.add(bottomPane,BorderLayout.SOUTH);

        updateTokenIDField();
//        mainSplitPane_upperPane.revalidate();
//        mainSplitPane_upperPane.repaint();

    }
    private  void initLowerPane(){
        GridBagConstraints col1Constraints = new GridBagConstraints();
        col1Constraints.fill = GridBagConstraints.BOTH;
        col1Constraints.anchor=GridBagConstraints.CENTER;
        col1Constraints.ipadx=10;col1Constraints.ipady=0;
        col1Constraints.weightx=0.5;
        col1Constraints.gridwidth=1;
        col1Constraints.gridx = 0;
        col1Constraints.gridy = magicLinkCount;
        GridBagConstraints col2Constraints = new GridBagConstraints();
        col2Constraints.fill = GridBagConstraints.BOTH;
        col2Constraints.anchor=GridBagConstraints.CENTER;
        col2Constraints.ipadx=10;col2Constraints.ipady=0;
        col2Constraints.weightx=0.5;
        col2Constraints.gridwidth=1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = magicLinkCount;
        lowerPane_container = new JPanel();
        lowerPane_container.setLayout(new BoxLayout(lowerPane_container, BoxLayout.Y_AXIS));
        lowerPane_container.setMinimumSize(new Dimension(0,300));
        lowerPane_container.setBorder(new EmptyBorder(10, 10, 10, 10));
        lowerPane_container.setLayout(new GridBagLayout());
        final JLabel label1 = new JLabel();
        label1.setText("Token ID");
        lowerPane_container.add(label1,col1Constraints);
        final JLabel label2 = new JLabel();
        label2.setText("Magic Link");
        lowerPane_container.add(label2,col2Constraints);
        //lowerPane_container.setAutoscrolls(true);
        mainSplitPane_lowerPane = new JScrollPane(lowerPane_container);
        mainSplitPane_lowerPane.setMinimumSize(new Dimension(0,300));
    }

    private void updateEncodedValueMap(String name, BigInteger value,boolean isUpdateTokenID){
        encodedValueMap.put(name,value);
        if(isUpdateTokenID){
            updateTokenIDField();
        }
    }
    private void updateTokenIDField(){
        String tokenidStr="";
        BigInteger tokenid=BigInteger.valueOf(0);
        for(String key:encodedValueMap.keySet()){
            tokenid=tokenid.or(encodedValueMap.get(key));
        }
        tokenidStr=tokenid.toString(16);
        while (tokenidStr.length() < 64) {
            tokenidStr = "0" + tokenidStr;
        }
        this.fieldTokenID.setText(tokenidStr.toUpperCase());
    }
    private void setSelectedItem(String key,JComboBox comboBox) {
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

    private void createDatePicker(final JPanel dateTimePickerPane, JButton dateTimePicker, JComboBox comboBoxTimezone){
        GridBagConstraints col1Constraints = new GridBagConstraints();
        col1Constraints.fill = GridBagConstraints.BOTH;
        col1Constraints.anchor=GridBagConstraints.CENTER;
        col1Constraints.weightx=0.7;
        col1Constraints.gridwidth=1;
        col1Constraints.gridx = 0;
        col1Constraints.gridy = 0;
        GridBagConstraints col2Constraints = new GridBagConstraints();
        col2Constraints.fill = GridBagConstraints.BOTH;
        col2Constraints.anchor=GridBagConstraints.CENTER;
        col2Constraints.weightx=0.3;
        col2Constraints.gridwidth=1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = 0;

        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-1200","-1200"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-1100","-1100"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-1000","-1000"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0930","-0930"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0900","-0900"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0800","-0800"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0700","-0700"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0600","-0600"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0500","-0500"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0400","-0400"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0330","-0330"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0300","-0300"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0200","-0200"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("-0100","-0100"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0000","+0000"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0100","+0100"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0200","+0200"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0300","+0300"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0330","+0330"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0400","+0400"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0430","+0430"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0500","+0500"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0530","+0530"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0600","+0600"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0630","+0630"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0700","+0700"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0800","+0800"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0845","+0845"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0900","+0900"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+0930","+0930"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+1000","+1000"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+1030","+1030"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+1100","+1100"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+1200","+1200"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+1245","+1245"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+1300","+1300"));
        comboBoxTimezone.addItem(new ComboBoxSimpleItem("+1400","+1400"));
        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();
        //timeZone = TimeZone.getTimeZone("America/Caracas")
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
        //comboBoxTimezone.setSelectedItem(new ComboBoxSimpleItem(currentTimezone,currentTimezone));
        setSelectedItem(currentTimezone,comboBoxTimezone);
//        comboBoxTimezone.addItemListener(new ItemListener() {
//                                             public void itemStateChanged(ItemEvent e) {
//                                                 ComboBoxSimpleItem item = (ComboBoxSimpleItem)e.getItem();
//                                                 expireTimezone = item.getKey();
//                                             }
//                                         });
        dateTimePickerPane.add(dateTimePicker,col1Constraints);
        dateTimePickerPane.add(comboBoxTimezone,col2Constraints);
    }

    private void createMagicLink() throws ParseException {
        magicLinkCount+=1;
        GridBagConstraints col1Constraints = new GridBagConstraints();
        col1Constraints.fill = GridBagConstraints.BOTH;
        col1Constraints.anchor=GridBagConstraints.CENTER;
        col1Constraints.ipadx=10;col1Constraints.ipady=10;
        col1Constraints.insets = new Insets(5,5,5,5);
        col1Constraints.weightx=0.5;
        col1Constraints.gridwidth=1;
        col1Constraints.gridx = 0;
        col1Constraints.gridy = magicLinkCount;
        GridBagConstraints col2Constraints = new GridBagConstraints();
        col2Constraints.fill = GridBagConstraints.BOTH;
        col2Constraints.anchor=GridBagConstraints.CENTER;
        col2Constraints.ipadx=30;col2Constraints.ipady=30;
        col2Constraints.insets = new Insets(5,5,5,5);
        col2Constraints.weightx=0.5;
        col2Constraints.gridwidth=1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = magicLinkCount;

        int size = comboBoxTickets.getItemCount();
        List<BigInteger> tickets = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ComboBoxSimpleItem item = (ComboBoxSimpleItem)comboBoxTickets.getItemAt(i);
            tickets.add(new BigInteger(item.getValue(),16));
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
        ComboBoxSimpleItem item = (ComboBoxSimpleItem)timeZoneExpireTime.getSelectedItem();
        String dateStr = String.format("%s%s",dateTimePickerExpireTime.getText(),item.getKey());
        Date date = df.parse(dateStr);
        BigInteger expiryTimestamp=BigInteger.valueOf(date.getTime()/1000);
        String contractAddress= fieldContractAddress.getText();
        String privateKey= fieldPrivateKey.getText();
        BigInteger privateKeyofOrganizer=new BigInteger(privateKey,16);
        byte[] message = encodeMessageForSpawnable(expiryTimestamp, tickets ,contractAddress);
        Sign.SignatureData signedData = signMagicLink(message, privateKeyofOrganizer);
        byte[] signature = covertSigToByte(signedData);
        byte[] completeLink = new byte[message.length + 1 + signature.length];
        //encoding byte for spawnable, added to the link but not signed
        completeLink[0] = (byte) 0x02;
        System.arraycopy(message, 0, completeLink, 0, message.length);
        System.arraycopy(signature, 0, completeLink, message.length, signature.length);

        StringBuilder magicLinkSB = new StringBuilder();

        magicLinkSB.append("https://aw.app/");
        byte[] b64 = Base64.getUrlEncoder().encode(completeLink);
        magicLinkSB.append(new String(b64));

        JComboBox comboBox= new JComboBox();
        for(int i=0;i<comboBoxTickets.getItemCount();++i){
            comboBox.addItem(comboBoxTickets.getItemAt(i));
        }
        lowerPane_container.add(comboBox,col1Constraints);
        JTextArea textArea = new JTextArea(magicLinkSB.toString());
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        //JScrollPane scrollPane = new JScrollPane(textArea);
        //lowerPane_container.add(textArea,col2Constraints);
        JTextPane textPane=new JTextPane();
        textPane.setText(magicLinkSB.toString());
        JPanel wrapPane = new JPanel();
        wrapPane.setPreferredSize(new Dimension(80,20));
        wrapPane.add(textPane);
        JScrollPane scrollPane=new JScrollPane(wrapPane);
        scrollPane.setPreferredSize(new Dimension(80,20));
        scrollPane.setViewportView(textPane);
        lowerPane_container.add(scrollPane,col2Constraints);
        this.pack();
    }

    public byte[] covertSigToByte(Sign.SignatureData ecSig)
    {
        byte subV = ecSig.getV();
        byte[] subR = ecSig.getR();
        byte[] subS = ecSig.getS();
        ByteBuffer sig=ByteBuffer.allocate(subR.length+subS.length+1);
        sig.put(subR);
        sig.put(subS);
        sig.put(subV);
        return sig.array();
    }

    public Sign.SignatureData signMagicLink(byte[] message, BigInteger privateKeyOfOrganiser)
    {
        ECKeyPair ecKeyPair  = ECKeyPair.create(privateKeyOfOrganiser);
        //returns the v, r and s signature params
        return Sign.signMessage(message, ecKeyPair);
    }

    private static void addExpiryAndContractToByteBuffer(
            ByteBuffer message,
            BigInteger expiryTimestamp,
            String contractAddress
    ) {
        byte[] expiry = expiryTimestamp.toByteArray();
        byte[] leadingZerosExpiry = new byte[32 - expiry.length];
        message.put(leadingZerosExpiry);
        message.put(expiry);
        byte[] contract = Numeric.hexStringToByteArray(contractAddress);
        message.put(contract);
    }

    public static byte[] encodeMessageForSpawnable(
            BigInteger expiryTimestamp,
            List<BigInteger> tickets,
            String contractAddress
    )
    {
        ByteBuffer message = ByteBuffer.allocate(52 + tickets.size() * 32);
        //price must be set to zero as the paymaster is covering the transaction
        addExpiryAndContractToByteBuffer(message, expiryTimestamp, contractAddress);
        for(BigInteger i : tickets) {
            //need to pad so that it is 32bytes
            String paddedTicket = Numeric.toHexStringNoPrefixZeroPadded(i, 64);
            byte[] ticketAsByteArray = Numeric.hexStringToByteArray(paddedTicket);
            message.put(ticketAsByteArray);
        }
        return message.array();
    }

}
