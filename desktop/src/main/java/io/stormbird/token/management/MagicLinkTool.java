package io.stormbird.token.management;

import io.stormbird.token.management.CustomComponents.DateTimePicker;
import io.stormbird.token.management.Model.*;
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
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * Layout::
 * menubar
 * mainSplitPane
 * 	mainSplitPane_topPane(FlowLayout)
 * 		textFieldPrivateKey
 *      comboBoxKeysList
 *
 * 	mainSplitPane_tabPane
 * 		tabPane_container
 * 			northPane(comboBoxContractAddress)
 * 		    centerPane
 * 		        tabPane_container_contentPane
 * 		    south(buttonAddAnother)
 **/
public class MagicLinkTool extends JFrame{

    public InputStream ticketXML = getClass().getResourceAsStream("/TicketingContract.xml");

    private TokenViewModel tokenViewModel;
    private JSplitPane mainSplitPane;
    private JPanel mainSplitPane_topPane;
    JComboBox comboBoxKeysList;

    private JTabbedPane mainSplitPane_tabPane;
    private JPanel tabPane_container;
    private JPanel tabPane_container_contentPane;
    private static int magicLinkCount=0;

    private JComboBox comboBoxContractAddress;

    public Map<Integer, MagicLinkViewModel> _magicLinkViewMap = new ConcurrentHashMap<>();
    public Map<Integer, MagicLinkDataModel> _magicLinkDataMap = new ConcurrentHashMap<>();

    public MagicLinkTool(){
        try {
            tokenViewModel=new TokenViewModel(ticketXML, Locale.getDefault());

            this.setJMenuBar(createMenuBar());

            initUpperPane();
            initTabPane();
            mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane_topPane, mainSplitPane_tabPane);
            mainSplitPane.setMinimumSize(new Dimension(900,300));
            this.setContentPane(mainSplitPane);
            this.setMinimumSize(new Dimension(900,300));
            this.setTitle("MagicLink Generator");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setLocationByPlatform(true);
            this.setResizable(true);
            this.pack();
        } catch (IOException | IllegalArgumentException | SAXException e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        (new MagicLinkTool()).setVisible(true);
    }

    //build menu
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);

        menuItem = new JMenuItem("New contract behaviour XML file...",
                KeyEvent.VK_T);
        //menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem = new JMenuItem("Open contract behaviour XML file...",
                KeyEvent.VK_T);
        //menuItem.addActionListener(this);
        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem("Export magic links...",
                KeyEvent.VK_T);
        //menuItem.addActionListener(this);
        menu.add(menuItem);

        //Build second menu in the menu bar.
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_N);
        menuBar.add(menu);
        menuItem = new JMenuItem("About",
                KeyEvent.VK_T);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Magic Link Generation Tool.");
            }
        });
        menu.add(menuItem);

        return menuBar;
    }

    private  void initUpperPane() {
        mainSplitPane_topPane = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        mainSplitPane_topPane.setLayout(flowLayout);
        JPanel controlsPane = new JPanel();
        JPanel leftPane = new JPanel();
        leftPane.setLayout(new GridBagLayout());
        JPanel rightPane = new JPanel();
        rightPane.setLayout(new GridBagLayout());
        rightPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        controlsPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlsPane.setLayout(new GridBagLayout());

        leftPane.add(new JLabel("Import PrivateKey:"));
        JTextField textFieldPrivateKey = new JTextField();
        textFieldPrivateKey.setColumns(30);
        leftPane.add(textFieldPrivateKey);
        JButton buttonImport=new JButton();
        buttonImport.setText("Import");
        buttonImport.addActionListener(new ActionListener() {
                                           @Override
                                           public void actionPerformed(ActionEvent e) {
                                               comboBoxKeysList.addItem(new ComboBoxSimpleItem(textFieldPrivateKey.getText(), textFieldPrivateKey.getText()));
                                           }
                                       });
        leftPane.add(buttonImport);
        rightPane.add(new JLabel("Current Key:"));
        comboBoxKeysList = new JComboBox();
        comboBoxKeysList.setPreferredSize(new Dimension(200, 30));
        rightPane.add(comboBoxKeysList);

        mainSplitPane_topPane.add(leftPane);
        mainSplitPane_topPane.add(rightPane);
        mainSplitPane_topPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    private  void initTabPane(){

        mainSplitPane_tabPane = new JTabbedPane();
        tabPane_container = new JPanel();
        tabPane_container.setLayout(new BoxLayout(tabPane_container, BoxLayout.Y_AXIS));
        //tabPane_container.setMinimumSize(new Dimension(0,300));
        tabPane_container.setBorder(new EmptyBorder(10, 10, 10, 10));

        //top contract address
        JPanel northPane = new JPanel();
        northPane.add(new JLabel("Contract:"));
        comboBoxContractAddress = new JComboBox();
        for(ComboBoxSimpleItem item : tokenViewModel.comboBoxContractAddressList) {
            comboBoxContractAddress.addItem(item);
            comboBoxContractAddress.setEnabled(true);
        }
        northPane.add(comboBoxContractAddress);
        //center: left and right pane
        GridBagConstraints col1Constraints = new GridBagConstraints();
        col1Constraints.fill = GridBagConstraints.BOTH;
        col1Constraints.anchor=GridBagConstraints.CENTER;
        col1Constraints.ipadx=10;col1Constraints.ipady=10;
        col1Constraints.weightx=0.5;
        col1Constraints.gridwidth=1;
        col1Constraints.gridx = 0;
        col1Constraints.gridy = 0;
        GridBagConstraints col2Constraints = new GridBagConstraints();
        col2Constraints.fill = GridBagConstraints.BOTH;
        col2Constraints.anchor=GridBagConstraints.CENTER;
        col2Constraints.ipadx=10;col2Constraints.ipady=10;
        col2Constraints.weightx=0.5;
        col2Constraints.gridwidth=1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = 0;
        JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
        tabPane_container_contentPane = new JPanel();
        tabPane_container_contentPane.setLayout(new GridBagLayout());
        centerPane.add(tabPane_container_contentPane,BorderLayout.CENTER);
        //bottom: add another button
        JButton buttonAddAnother = new JButton();
        buttonAddAnother.setText("Add Another");
        buttonAddAnother.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAnotherTicket();
            }
        });
        initTabPaneTitle();
        addAnotherTicket();
        tabPane_container.add(northPane,BorderLayout.NORTH);
        tabPane_container.add(centerPane,BorderLayout.CENTER);
        tabPane_container.add(buttonAddAnother,BorderLayout.SOUTH);
        mainSplitPane_tabPane.addTab("Meetup[x]",null, tabPane_container,
                "");
        mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_1);
        mainSplitPane_tabPane.addTab("[+]",null, null,
                "");
        mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_2);
        mainSplitPane_tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        mainSplitPane_tabPane.setMinimumSize(new Dimension(0,300));
    }

    private GridBagConstraints getGridConstraints(int gridx,int gridy,double weightx,int align){
        GridBagConstraints colConstraints = new GridBagConstraints();
        colConstraints.fill = GridBagConstraints.BOTH;
        colConstraints.anchor = (align == 0) ? GridBagConstraints.WEST : GridBagConstraints.EAST;
        colConstraints.fill = (align == 0) ? GridBagConstraints.BOTH
                : GridBagConstraints.HORIZONTAL;
        colConstraints.ipadx=10;colConstraints.ipady=10;
        colConstraints.weightx=weightx;
        colConstraints.gridwidth=1;
        colConstraints.gridx = gridx;
        colConstraints.gridy = gridy;
        return colConstraints;
    }
    private void initTabPaneTitle(){
        int gridx,gridy;
        gridx=gridy=0;

        // render dropdown list
        for(ComboBoxDataModel comboBoxDataModel : tokenViewModel.comboBoxDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,gridy,0.5,0);
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(comboBoxDataModel.name);
            tabPane_container_contentPane.add(labelAttrName,colConstraints);
            gridx++;
        }
        for(TextFieldDataModel model : tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,gridy,0.4,0);
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(model.name);
            tabPane_container_contentPane.add(labelAttrName,colConstraints);
            gridx++;
        }
        tabPane_container_contentPane.add(new JLabel("Token Status"),getGridConstraints(gridx,gridy,0.6,0));
        gridx++;
        tabPane_container_contentPane.add(new JLabel("Remark"),getGridConstraints(gridx,gridy,0.4,0));

    }
    private void addAnotherTicket(){
        int gridx=0;
        magicLinkCount++;
        MagicLinkViewModel magicLinkViewModel=new MagicLinkViewModel();

        JTextField textFieldRowNum = new JTextField();
        textFieldRowNum.setName(Integer.toString(magicLinkCount));
        // render dropdown list
        for(ComboBoxDataModel comboBoxDataModel : tokenViewModel.comboBoxDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,magicLinkCount,0.5,0);
            ComboBoxDataModel.ComboBoxOption[] options=comboBoxDataModel.getComboBoxOptions();
            JComboBox comboBox = new JComboBox(options);
            comboBox.setName(comboBoxDataModel.getId());
            comboBox.setEnabled(true);
            tabPane_container_contentPane.add(comboBox,colConstraints);

            //updateEncodedValueMap(comboBox.getName(),options[0].getKey(),-1);
            //tabPane_container_contentPane.add(textFieldEncodedValue);

            comboBox.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                    updateEncodedValueMap(Integer.valueOf(textFieldRowNum.getName()));
                }
            });
            gridx++;

            magicLinkViewModel.setComboBoxForXMLList(comboBox);
        }
        for(TextFieldDataModel model : tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,magicLinkCount,0.4,0);

            if(model.id.equals("time")) {
                JTextField textFieldHiddenValue = new JTextField();
                textFieldHiddenValue.setVisible(false);

                JPanel dateTimePickerPane = new JPanel();
                dateTimePickerPane.setLayout(new GridBagLayout());
                JButton dateTimePickerTime = new DateTimePicker(textFieldHiddenValue);
                dateTimePickerTime.setName(model.id);
                JComboBox timeZoneTime = new JComboBox();
                createDatePicker(dateTimePickerPane, dateTimePickerTime, timeZoneTime);
                tabPane_container_contentPane.add(dateTimePickerPane, colConstraints);
//                timeZoneTime.addItemListener(new ItemListener() {
//                    @Override
//                    public void itemStateChanged(ItemEvent e) {
//                        onDatePickerChange(dateTimePickerTime,timeZoneTime,textFieldEncodedValue,model.getBitshift(),model.getBitmask(),magicLinkCount);
//                    }
//                });
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
                        onDatePickerChange(dateTimePickerTime, timeZoneTime, model.getBitshift(), model.getBitmask(), Integer.valueOf(textFieldRowNum.getName()));
                    }
                });
                //onDatePickerChange(dateTimePickerTime, timeZoneTime, textFieldEncodedValue, model.getBitshift(), model.getBitmask(), -1);
                magicLinkViewModel.setDateTimePickerMap(dateTimePickerTime,timeZoneTime,model);
            }else {
                JTextField textFieldInput = new JTextField();
                textFieldInput.setName(model.id);
                textFieldInput.setEditable(true);
                textFieldInput.setEnabled(true);
                textFieldInput.setColumns(10);
                textFieldInput.addKeyListener(new KeyAdapter() {
                    public void keyReleased(KeyEvent e) {
                        try {

                            updateEncodedValueMap(Integer.valueOf(textFieldRowNum.getName()));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Invalid Data Type! Please check the type",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            textFieldInput.setText("");
                            textFieldInput.requestFocusInWindow();
                        }
                    }
                });
                tabPane_container_contentPane.add(textFieldInput, colConstraints);
                magicLinkViewModel.setTextFieldForXMLMap(textFieldInput,model);
            }
            //tabPane_container_contentPane.add(textFieldEncodedValue);
            gridx++;
        }
        JPanel tokenStatusPane = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        tokenStatusPane.setLayout(flowLayout);
        tokenStatusPane.add(new JLabel("expiry"));
        JPanel dateTimePickerPane = new JPanel();
        dateTimePickerPane.setLayout(new GridBagLayout());
        JButton dateTimePickerExpireTime = new DateTimePicker();
        JComboBox timeZoneExpireTime = new JComboBox();
        createDatePicker(dateTimePickerPane, dateTimePickerExpireTime,timeZoneExpireTime);
        tokenStatusPane.add(dateTimePickerPane);
        magicLinkViewModel.setDateTimePickerExpire(dateTimePickerExpireTime,timeZoneExpireTime);

        tokenStatusPane.add(new JLabel("ask"));
        JTextField textFieldOwner=new JTextField();
        textFieldOwner.setColumns(10);
        tokenStatusPane.add(textFieldOwner);
        magicLinkViewModel.TextFieldOwner=textFieldOwner;

        tokenStatusPane.add(new JLabel("redeem by"));
        JTextField textFieldMagicLink=new JTextField();
        textFieldMagicLink.setEditable(false);
        textFieldMagicLink.setColumns(15);
        tokenStatusPane.add(textFieldMagicLink);

        tabPane_container_contentPane.add(tokenStatusPane,getGridConstraints(gridx,magicLinkCount,0.6,0));
        gridx++;
        JTextField textFieldRemark = new JTextField();
        textFieldRemark.setColumns(15);
        tabPane_container_contentPane.add(textFieldRemark,getGridConstraints(gridx,magicLinkCount,0.4,0));

        magicLinkViewModel.TextFieldMagicLink=textFieldMagicLink;
        magicLinkViewModel.TextFieldRemark = textFieldRemark;
        _magicLinkViewMap.put(magicLinkCount,magicLinkViewModel);
        this.validate();
        this.repaint();
        this.pack();
    }


    private  void onDatePickerChange(JButton dateTimePickerTime,JComboBox timeZoneTime,int shift,BigInteger bitmask,int rowNum) {
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
            updateEncodedValueMap(rowNum);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Something wrong!",
                    "Error", JOptionPane.ERROR_MESSAGE);
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

    /**
     * Calculation
     */

    private void updateEncodedValueMap(int rowNum){
        //encodedValueMap.put(name,value);
        if(rowNum>0){
            updateTokenIDField(rowNum);
        }
    }
    private void updateTokenIDField(int rowNum){
        Map<String,BigInteger> encodedValueMap=new ConcurrentHashMap<>();
        String tokenidStr="";
        BigInteger tokenid=BigInteger.valueOf(0);

        //
        MagicLinkViewModel magicLinkViewModel = _magicLinkViewMap.get(rowNum);
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
                    Date date = displayFormat.parse(dateStr);
                    String targetTimeStr = targetFormat.format(date);
                    if (targetTimeStr != null && targetTimeStr.length() > 0) {
                        byte[] bytes = targetTimeStr.getBytes(Charset.forName("UTF-8"));
                        encodedValue = new BigInteger(bytes);
                        encodedValue = encodedValue.shiftLeft(model.getBitshift()).and(model.getBitmask());
                    }
                    encodedValueMap.put(model.id,encodedValue);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Something wrong!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        for(String key:encodedValueMap.keySet()){
            tokenid=tokenid.or(encodedValueMap.get(key));
        }

        //

        ComboBoxSimpleItem contractAddressSelectedItem = (ComboBoxSimpleItem)comboBoxContractAddress.getSelectedItem();

        MagicLinkDataModel magicLinkDataModel = _magicLinkDataMap.get(rowNum);
        if(magicLinkDataModel==null){
            magicLinkDataModel=new MagicLinkDataModel();
        }
        magicLinkDataModel.TokenIDs = new BigInteger[]{tokenid};
        magicLinkDataModel.Price = "0";

        DateTimePickerViewModel dateTimePicker=magicLinkViewModel.DateTimePickerExpire;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZZ");
        ComboBoxSimpleItem item = (ComboBoxSimpleItem)dateTimePicker.TimeZone.getSelectedItem();
        magicLinkDataModel.Expiry=String.format("%s%s",dateTimePicker.DateTimePickerTime.getText(),item.getKey());
        magicLinkDataModel.ContractAddress=contractAddressSelectedItem.getKey();

        ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem)comboBoxKeysList.getSelectedItem();
        if(currentPrivateKeySelectedItem==null||currentPrivateKeySelectedItem.getValue()==null){
            JOptionPane.showMessageDialog(null,
                    "Please Provide private key.");
        }
        magicLinkDataModel.generateMagicLink(currentPrivateKeySelectedItem.getValue());
        //update UI
        JTextField textFieldMagicLink = magicLinkViewModel.TextFieldMagicLink;
        textFieldMagicLink.setText(magicLinkDataModel.MagicLink);
        JTextField textFieldRemark = magicLinkViewModel.TextFieldRemark;

        tokenidStr=tokenid.toString(16);
        while (tokenidStr.length() < 64) {
            tokenidStr = "0" + tokenidStr;
        }
        textFieldRemark.setText(Integer.toString(rowNum)+","+tokenidStr);

    }
}
