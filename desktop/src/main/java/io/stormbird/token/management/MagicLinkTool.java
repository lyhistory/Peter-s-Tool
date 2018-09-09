package io.stormbird.token.management;

import io.stormbird.token.entity.EthereumReadBuffer;
import io.stormbird.token.management.CustomComponents.DateTimePicker;
import io.stormbird.token.management.CustomComponents.WizardDialog;
import io.stormbird.token.management.Model.*;
import io.stormbird.token.tools.TokenDefinition;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Convert;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
 * 		            MagicLink Grid
 * 		    south(buttonAddAnother)
 **/
public class MagicLinkTool extends JFrame{
    public InputStream ticketXMLTemplate = getClass().getResourceAsStream("/MeetupContract.xml");
    public String ticketXMLFilePath = "./desktop/res/MeetupContract.xml";
    public String privateKeyFilePath = "./desktop/res/wallets.key";
    public String magicLinksCSVPath = "./desktop/res/magiclinks.csv";

    private JSplitPane mainSplitPane;
    private JPanel mainSplitPane_topPane;
    private JComboBox comboBoxKeysList;
    private JTabbedPane mainSplitPane_tabPane;
    private JPanel tabPane_container;
    private JPanel tabPane_wizard;
    private JComboBox comboBoxContractAddress;
    private JPanel tabPane_container_centerPane;

    private static int magicLinkCount=0;
    private TokenViewModel _tokenViewModel;
    private Map<Integer, MagicLinkToolViewModel> _magicLinkViewMap;
    private static ArrayList<MagicLinkDataModel> _magicLinkDataModelArrayList;
    public MagicLinkTool(){
        try {
            _magicLinkViewMap = new ConcurrentHashMap<>();
            _magicLinkDataModelArrayList = loadMagicLinksFromCSV();


            this.setJMenuBar(createMenuBar());
            this.initUpperPane();    //Private key Dropdownlist
            this.initTabPane();      //Magic Link Generation Pane
            this.mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane_topPane, mainSplitPane_tabPane);
            //this.mainSplitPane.setMinimumSize(new Dimension(900,300));
            this.setContentPane(mainSplitPane);
            //this.setMinimumSize(new Dimension(900,300));
            this.setTitle("MagicLink Generator");
            //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    saveMagicLinksToCSV();
                    savePrivateKey();
                    super.windowClosing(e);
                    System.exit(0);
                }
            });


            this.setLocationByPlatform(true);
            this.setResizable(true);
            this.pack();


        } catch (IllegalArgumentException e){
            e.printStackTrace();
            //log exception
        }
    }

    public static void main(String args[]) {
        MagicLinkTool magicLinkTool = new MagicLinkTool();
        magicLinkTool.setVisible(true);
    }

    //create MenuBar
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

    //create UpperPane: manage private key
    private  void initUpperPane() {
        Map<String,String> keys = loadWalletFromKeystore();
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

        leftPane.add(new JLabel("PrivateKey:"));
        JTextField textFieldPrivateKey = new JTextField();
        textFieldPrivateKey.setColumns(30);
        leftPane.add(textFieldPrivateKey);
        JButton buttonImport=new JButton();
        buttonImport.setText("Click to Import");
        buttonImport.setForeground(Color.red);
        buttonImport.setBackground(Color.GREEN);
        buttonImport.addActionListener(new ActionListener() {
                                           @Override
                                           public void actionPerformed(ActionEvent e) {
                                               try{
                                                   String privateKey=textFieldPrivateKey.getText();
                                                   String address = getEthAddress(privateKey);
                                                   comboBoxKeysList.addItem(new ComboBoxSimpleItem(address,privateKey));
                                               }catch (Exception ex){
                                                   JOptionPane.showMessageDialog(null, "Invalid PrivateKey!",
                                                           "Error", JOptionPane.ERROR_MESSAGE);
                                               }
                                           }
                                       });
        leftPane.add(buttonImport);
        rightPane.add(new JLabel("Current Key:"));
        comboBoxKeysList = new JComboBox();
        comboBoxKeysList.setPreferredSize(new Dimension(200, 30));
        rightPane.add(comboBoxKeysList);
        if(keys!=null&&keys.size()>0){
            for(String key:keys.keySet()){
                comboBoxKeysList.addItem(new ComboBoxSimpleItem(key,keys.get(key)));
            }
        }
        mainSplitPane_topPane.add(leftPane);
        mainSplitPane_topPane.add(rightPane);
        mainSplitPane_topPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    }

    //create TabPane: manage magic link creation
    private  void initTabPane(){
        //init tabpane
        mainSplitPane_tabPane = new JTabbedPane();
        tabPane_container = new JPanel();
        tabPane_container.setLayout(new BoxLayout(tabPane_container, BoxLayout.Y_AXIS));
        tabPane_container.setBorder(new EmptyBorder(10, 10, 10, 10));
        if(_magicLinkDataModelArrayList==null||_magicLinkDataModelArrayList.size()==0){
            initWizard();
        }else{
            initTabPaneContainer();
        }
        mainSplitPane_tabPane.addTab("Meetup[x]",null, tabPane_container, "");
        mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_1);
        mainSplitPane_tabPane.addTab("[+]",null, null, "");
        mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_2);
        mainSplitPane_tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        mainSplitPane_tabPane.setMinimumSize(new Dimension(0,300));
    }
    private void initWizard(){
        tabPane_wizard = new JPanel();
        tabPane_wizard.setLayout(new BoxLayout(tabPane_wizard, BoxLayout.Y_AXIS));
        tabPane_wizard.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel northPane = new JPanel();
        northPane.add(new JLabel("Deploy a new contract"));
        JTextField textFieldContractAddress = new JTextField(10);
        JComboBox comboBoxNetworkID=new JComboBox();
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("mainnet", "1"));
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("Ropsten", "3"));

        JPanel centerPane = new JPanel();
        centerPane.add(new JLabel("Contract Address:"));
        centerPane.add(textFieldContractAddress);
        centerPane.add(comboBoxNetworkID);

        JPanel southPane = new JPanel();
        JButton buttonNextStep = new JButton();
        buttonNextStep.setText("Next");
        buttonNextStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ComboBoxSimpleItem selectedNetworkItem = (ComboBoxSimpleItem) comboBoxNetworkID.getSelectedItem();
                String networkid = selectedNetworkItem.getValue();
                String contractAddress = textFieldContractAddress.getText();
                // todo validation
                if (contractAddress==null||contractAddress.equals("")) {
                    textFieldContractAddress.selectAll();
                    JOptionPane.showMessageDialog(
                            null,
                            "cannot be empty!",
                            "Warn",
                            JOptionPane.ERROR_MESSAGE);
                    contractAddress = null;
                    textFieldContractAddress.requestFocusInWindow();
                } else {
                    updateContractAddress(networkid,contractAddress);

                }
            }
        });
        southPane.add(buttonNextStep);
        tabPane_wizard.add(northPane,BorderLayout.NORTH);
        tabPane_wizard.add(centerPane,BorderLayout.CENTER);
        tabPane_wizard.add(southPane,BorderLayout.SOUTH);
        tabPane_container.add(tabPane_wizard);
    }
    private void initTabPaneContainer(){
        try {
            _tokenViewModel = new TokenViewModel(new FileInputStream(ticketXMLFilePath), Locale.getDefault());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        //TopPane: contract address
        JPanel northPane = new JPanel();
        northPane.add(new JLabel("Contract:"));
        comboBoxContractAddress = new JComboBox();
        for(ComboBoxSimpleItem item : _tokenViewModel.comboBoxContractAddressList) {
            comboBoxContractAddress.addItem(item);
            comboBoxContractAddress.setEnabled(true);
        }
        northPane.add(comboBoxContractAddress);

        //CenterPane: main container for magic link
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
        tabPane_container_centerPane = new JPanel();
        tabPane_container_centerPane.setLayout(new GridBagLayout());
        centerPane.add(tabPane_container_centerPane,BorderLayout.CENTER);
        //render container
        //render title
        initMagicLinkCreationColumnTitle();
        //render from autosaved magiclink.csv

        if(_magicLinkDataModelArrayList==null||_magicLinkDataModelArrayList.size()==0){
            addAnotherTicket(null);
        }else{
            for(int i=0;i<_magicLinkDataModelArrayList.size();++i){
                addAnotherTicket(_magicLinkDataModelArrayList.get(i));
            }
        }

        //BottomPane: add another button
        JButton buttonAddAnother = new JButton();
        buttonAddAnother.setText("Add Another");
        buttonAddAnother.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAnotherTicket(null);
            }
        });

        tabPane_container.add(northPane,BorderLayout.NORTH);
        tabPane_container.add(centerPane,BorderLayout.CENTER);
        tabPane_container.add(buttonAddAnother,BorderLayout.SOUTH);
        if(tabPane_wizard!=null) {
            tabPane_wizard.setVisible(false);
        }
        this.setResizable(true);
        this.pack();
    }
    // render the column title by xml attributes + token status/remark
    private void initMagicLinkCreationColumnTitle(){
        int gridx,gridy;
        gridx=gridy=0;
        // render dropdown list
        for(ComboBoxDataModel comboBoxDataModel : _tokenViewModel.comboBoxDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,gridy,0.5,0);
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(comboBoxDataModel.name);
            tabPane_container_centerPane.add(labelAttrName,colConstraints);
            gridx++;
        }
        for(TextFieldDataModel model : _tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,gridy,0.4,0);
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(model.name);
            tabPane_container_centerPane.add(labelAttrName,colConstraints);
            gridx++;
        }
        tabPane_container_centerPane.add(new JLabel("Token Status"),getGridConstraints(gridx,gridy,0.6,0));
        gridx++;
        tabPane_container_centerPane.add(new JLabel("Remark"),getGridConstraints(gridx,gridy,0.4,0));

    }

    // add new magicLink generation form in row, managed by Map<Integer, MagicLinkToolViewModel> _magicLinkViewMap
    private void addAnotherTicket(MagicLinkDataModel magicLinkData){
        BigInteger tokenID = BigInteger.valueOf(0);
        if(magicLinkData!=null&&magicLinkData.tickets.length>0){
            tokenID=magicLinkData.tickets[0];
        }
        String currentTimezone = getCurrentTimezone();

        int gridx=0;
        magicLinkCount++;
        MagicLinkToolViewModel magicLinkViewModel=new MagicLinkToolViewModel();
        JTextField textFieldRowNum = new JTextField();
        textFieldRowNum.setName(Integer.toString(magicLinkCount));
        // draw dropdownlist by XML map attributes
        for(ComboBoxDataModel comboBoxDataModel : _tokenViewModel.comboBoxDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,magicLinkCount,0.5,0);
            ComboBoxDataModel.ComboBoxOption[] options=comboBoxDataModel.getComboBoxOptions();
            JComboBox comboBox = new JComboBox(options);
            comboBox.setName(comboBoxDataModel.getId());
            comboBox.setEnabled(true);
            tabPane_container_centerPane.add(comboBox,colConstraints);
            BigInteger valWithMask = tokenID.and(comboBoxDataModel.bitmask);
            if(valWithMask.equals(BigInteger.valueOf(0))==false){
                for(int i=0;i<options.length;++i){
                    if(options[i].getKey().equals(valWithMask)){
                        comboBox.setSelectedItem(options[i]);
                        break;
                    }
                }
            }

            comboBox.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                    generateMagicLink(Integer.valueOf(textFieldRowNum.getName()));
                }
            });
            gridx++;
            magicLinkViewModel.setComboBoxForXMLList(comboBox);
        }
        // draw textfield&datepicker by XML normal&time attributes
        for(TextFieldDataModel model : _tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,magicLinkCount,0.4,0);
            BigInteger valWithoutMask = tokenID.and(model.bitmask).shiftRight(model.bitshift);
            if(model.id.equals("time")) {
                JTextField textFieldHiddenValue = new JTextField(); //for trigger onchange event
                textFieldHiddenValue.setVisible(false);

                JPanel dateTimePickerPane = new JPanel();
                JButton dateTimePickerTime = new DateTimePicker(textFieldHiddenValue);
                dateTimePickerTime.setName(model.id);
                JComboBox timeZoneTime = createDatePicker(dateTimePickerPane, dateTimePickerTime);
                tabPane_container_centerPane.add(dateTimePickerPane, colConstraints);
                if(valWithoutMask.equals(BigInteger.valueOf(0))==false){
                    currentTimezone = MagicLinkDataModel.getTimezoneStrByValue(valWithoutMask);
                    String dateStr=MagicLinkDataModel.getDateStrByValue(valWithoutMask,currentTimezone);
                    ((DateTimePicker) dateTimePickerTime).setDate(dateStr);
                    setSelectedItem(currentTimezone,timeZoneTime);
                }
                timeZoneTime.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        generateMagicLink(Integer.valueOf(textFieldRowNum.getName()));
                    }
                });
                textFieldHiddenValue.getDocument().addDocumentListener(new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        warn();
                    }
                    public void removeUpdate(DocumentEvent e) {warn();}
                    public void insertUpdate(DocumentEvent e) {
                        warn();
                    }
                    public void warn() {
                        generateMagicLink(Integer.valueOf(textFieldRowNum.getName()));
                    }
                });

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
                            generateMagicLink(Integer.valueOf(textFieldRowNum.getName()));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Invalid Data Type! Please check the type",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            textFieldInput.setText("");
                            textFieldInput.requestFocusInWindow();
                        }
                    }
                });
                tabPane_container_centerPane.add(textFieldInput, colConstraints);
                magicLinkViewModel.setTextFieldForXMLMap(textFieldInput,model);

                if(valWithoutMask.equals(BigInteger.valueOf(0))==false){
                    if (model.as.equals("UTF8")) {
                        byte[] bytes=valWithoutMask.toByteArray();
                        String str=new String(bytes, Charset.forName("UTF-8"));
                        textFieldInput.setText(str);
                    } else if (model.as.equals("Unsigned")) {
                        textFieldInput.setText(String.valueOf(valWithoutMask.intValue()));
                    }

                }
            }
            gridx++;
        }
        JPanel tokenStatusPane = new JPanel();
        tokenStatusPane.setBackground(Color.ORANGE);
        //tokenStatusPane.setBorder(BorderFactory.createEmptyBorder());
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setHgap(0);
        flowLayout.setVgap(0);
        //flowLayout.setAlignment(FlowLayout.TRAILING);
        tokenStatusPane.setLayout(flowLayout);
        tokenStatusPane.add(new JLabel("expiry"));
        JPanel dateTimePickerPane = new JPanel();
        JTextField textFieldHiddenValue = new JTextField();
        textFieldHiddenValue.setVisible(false);
        JButton dateTimePickerExpireTime = new DateTimePicker(textFieldHiddenValue);
        JComboBox timeZoneExpireTime =createDatePicker(dateTimePickerPane, dateTimePickerExpireTime);
        tokenStatusPane.add(dateTimePickerPane);
        magicLinkViewModel.setDateTimePickerExpire(dateTimePickerExpireTime,timeZoneExpireTime);
        if(magicLinkData!=null&&magicLinkData.expiry!=0){
            String dateStr=MagicLinkDataModel.getDateStrByValue(magicLinkData.expiry,currentTimezone);
            ((DateTimePicker) dateTimePickerExpireTime).setDate(dateStr);
            setSelectedItem(currentTimezone,timeZoneExpireTime);
        }
        timeZoneExpireTime.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                generateMagicLink(Integer.valueOf(textFieldRowNum.getName()));
            }
        });
        textFieldHiddenValue.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }
            public void removeUpdate(DocumentEvent e) {
                warn();
            }
            public void insertUpdate(DocumentEvent e) { warn();}
            public void warn() {
                generateMagicLink(Integer.valueOf(textFieldRowNum.getName()));
            }
        });
        tokenStatusPane.add(new JLabel("ask"));
        JTextField textFieldOwner=new JTextField();
        textFieldOwner.setColumns(8);
        tokenStatusPane.add(textFieldOwner);
        magicLinkViewModel.TextFieldOwner=textFieldOwner;

        tokenStatusPane.add(new JLabel("redeem by"));
        JTextField textFieldMagicLink=new JTextField();
        textFieldMagicLink.setEditable(false);
        textFieldMagicLink.setColumns(15);
        if(magicLinkData!=null) {
            textFieldMagicLink.setText(magicLinkData.magicLink);
        }
        tokenStatusPane.add(textFieldMagicLink);

        tabPane_container_centerPane.add(tokenStatusPane,getGridConstraints(gridx,magicLinkCount,0.6,0));
        gridx++;
        JTextField textFieldRemark = new JTextField();
        textFieldRemark.setColumns(15);
        if(magicLinkData!=null) {
            textFieldRemark.setText(magicLinkData.remark);
        }
        tabPane_container_centerPane.add(textFieldRemark,getGridConstraints(gridx,magicLinkCount,0.4,0));

        magicLinkViewModel.TextFieldMagicLink=textFieldMagicLink;
        magicLinkViewModel.TextFieldRemark = textFieldRemark;
        _magicLinkViewMap.put(magicLinkCount,magicLinkViewModel);
        this.validate();
        this.repaint();
        this.pack();
    }

    /**
     * ##########################################################################################################################################
     * ## todo: Refactor all of it
     * ##########################################################################################################################################
     */

    /**
     * MagicLink Helper
     */
    // calculate tokenID and generate magic link from _magicLinkViewMap
    private void generateMagicLink(int rowNum){
        if(rowNum<=0){
            //log expected
            return;
        }
        if(comboBoxKeysList.getItemCount()<=0){
            JOptionPane.showMessageDialog(null,
                    "Please Provide private key.");
            return;
        }

        Map<String,BigInteger> encodedValueMap=new ConcurrentHashMap<>();
        String tokenidStr="";
        BigInteger tokenid=BigInteger.valueOf(0);

        //
        MagicLinkToolViewModel magicLinkViewModel = _magicLinkViewMap.get(rowNum);
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
                    displayFormat.setTimeZone(TimeZone.getTimeZone("GMT"+item.getKey()));
                    targetFormat.setTimeZone(TimeZone.getTimeZone("GMT"+item.getKey()));
                    Date date = displayFormat.parse(dateStr);
                    String targetTimeStr = targetFormat.format(date);
                    if (targetTimeStr != null && targetTimeStr.length() > 0) {
                        byte[] bytes = targetTimeStr.getBytes(Charset.forName("UTF-8"));
                        encodedValue = new BigInteger(bytes);
                        encodedValue = encodedValue.shiftLeft(model.getBitshift()).and(model.getBitmask());
                    }
                    encodedValueMap.put(model.id,encodedValue);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Opoos!"+ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        // now we can get the tokenID!
        for(String key:encodedValueMap.keySet()){
            tokenid=tokenid.or(encodedValueMap.get(key));
        }

        // it's time to create the magic link!
        ComboBoxSimpleItem contractAddressSelectedItem = (ComboBoxSimpleItem)comboBoxContractAddress.getSelectedItem();

        MagicLinkToolDataModel magicLinkDataModel = new MagicLinkToolDataModel();
        magicLinkDataModel.TokenIDs = new BigInteger[]{tokenid};
        magicLinkDataModel.Price = "0";

        DateTimePickerViewModel dateTimePicker=magicLinkViewModel.DateTimePickerExpire;
        ComboBoxSimpleItem item = (ComboBoxSimpleItem)dateTimePicker.TimeZone.getSelectedItem();
        magicLinkDataModel.Expiry=String.format("%s%s",dateTimePicker.DateTimePickerTime.getText(),item.getKey());
        magicLinkDataModel.timeZone = TimeZone.getTimeZone("GMT"+item.getKey());
        magicLinkDataModel.ContractAddress=contractAddressSelectedItem.getKey();

        ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem)comboBoxKeysList.getSelectedItem();
        magicLinkDataModel.generateMagicLink(currentPrivateKeySelectedItem.getValue());
        //update UI
        JTextField textFieldMagicLink = magicLinkViewModel.TextFieldMagicLink;
        textFieldMagicLink.setText(magicLinkDataModel.MagicLink);
        JTextField textFieldRemark = magicLinkViewModel.TextFieldRemark;

        tokenidStr=tokenid.toString(16);
        while (tokenidStr.length() < 64) {
            tokenidStr = "0" + tokenidStr;
        }
        textFieldRemark.setText("(change whatever u want),Row:"+Integer.toString(rowNum)+",TokenID:"+tokenidStr);
    }
    // parse magic link
    public MagicLinkDataModel parseMagicLink(String link){
        final String importTemplate = "https://app.awallet.io/";
        int offset = link.indexOf(importTemplate);
        if (offset > -1)
        {
            offset += importTemplate.length();
            String linkData = link.substring(offset);
            return readLinkData(linkData);
        }
        return null;
    }
    private MagicLinkDataModel readLinkData (String linkData) {
        byte[] bytes = Base64.getUrlDecoder().decode(linkData);
        long szabo;
        MagicLinkDataModel data = new MagicLinkDataModel();
        try
        {
            ByteArrayInputStream bas = new ByteArrayInputStream(bytes);
            EthereumReadBuffer ds = new EthereumReadBuffer(bas);

            data.contractType = ds.readByte();
            szabo = ds.readBI().intValue();;//ds.toUnsignedLong(ds.readInt());
            data.expiry = ds.readBI().longValue();
            data.priceWei = Convert.toWei(BigDecimal.valueOf(szabo), Convert.Unit.SZABO).toBigInteger();
            data.contractAddress = ds.readAddress();
            int ticketCount = (ds.available() - 65) / 32;
            data.tickets =new BigInteger[ticketCount];
            for(int i=0;i<ticketCount;++i){
                data.tickets[i] = ds.readBI();
            }
            data.ticketCount = data.tickets.length;
            //now read signature
            ds.readSignature(data.signature);
            ds.close();
        } catch (Exception e) {
            return null;
        }

        BigInteger microEth = Convert.fromWei(new BigDecimal(data.priceWei), Convert.Unit.SZABO).abs().toBigInteger();
        data.price = microEth.doubleValue() / 1000000.0;

        return data;
    }
    private static String getEthAddress(String privateKey){
        Credentials cs = Credentials.create(privateKey);
        String publicKey = cs.getEcKeyPair().getPublicKey().toString(16);
        return cs.getAddress();
    }

    /**
     * UI Helper
     */
    private GridBagConstraints getGridConstraints(int gridx,int gridy,double weightx,int align){
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
    private JComboBox createDatePicker(final JPanel dateTimePickerPane, JButton dateTimePicker){
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
    private String getCurrentTimezone(){
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
    /**
     * File Helper
     */
    private Map<String,String> loadWalletFromKeystore(){
        Map<String,String> keys = new ConcurrentHashMap<>();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(privateKeyFilePath));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int i=0;
            for (CSVRecord csvRecord : csvParser) {
                if(i!=0) {
                    keys.put(csvRecord.get(0), csvRecord.get(1));
                }
                ++i;
            }
        }catch (Exception ex){
        }
        return keys;
    }
    private ArrayList<MagicLinkDataModel> loadMagicLinksFromCSV(){
        ArrayList<MagicLinkDataModel> magicLinkDataModelList=new ArrayList<MagicLinkDataModel>();
        try {
            Reader reader = Files.newBufferedReader(Paths.get(magicLinksCSVPath));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int i=0;
            for (CSVRecord csvRecord : csvParser) {
                if(i!=0) {
                    MagicLinkDataModel model = parseMagicLink(csvRecord.get(0));
                    if (model != null) {
                        model.magicLink = csvRecord.get(0);
                        model.remark = csvRecord.get(1);
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
    private  void savePrivateKey(){
        if(comboBoxKeysList.getItemCount()>0) {
            if (createFileIfNotExists(privateKeyFilePath)) {
                try {
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(privateKeyFilePath));
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("PrivateKey", "Address"));
                    for (int i = 0; i < comboBoxKeysList.getItemCount(); ++i) {
                        ComboBoxSimpleItem item = (ComboBoxSimpleItem) comboBoxKeysList.getItemAt(i);
                        csvPrinter.printRecord(item.getValue(), item.getKey());
                    }
                    csvPrinter.flush();
                    writer.close();
                } catch (Exception ex) {

                }
            }
        }
    }
    private void saveMagicLinksToCSV(){
        if(_magicLinkViewMap.size()>0) {
            if (createFileIfNotExists(magicLinksCSVPath)) {
                try {
                    BufferedWriter writer = Files.newBufferedWriter(Paths.get(magicLinksCSVPath));
                    CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("MagicLink", "Remark"));

                    for (Integer index : _magicLinkViewMap.keySet()) {
                        MagicLinkToolViewModel magicLinkViewModel = _magicLinkViewMap.get(index);
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
    private boolean createFileIfNotExists(String filePath){
        try {
            File f = new File(filePath);
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

    public void updateContractAddress(String networkid, String contractAddress){
        //update xml
        DocumentBuilder dBuilder;
        Document xml=null;
        Transformer transformer=null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            xml = dBuilder.parse(ticketXMLTemplate);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
            createFileIfNotExists(ticketXMLFilePath);

            xml.getDocumentElement().normalize(); // also good for parcel
            NodeList contractList = xml.getElementsByTagName("contract");
            //.getElementById("holding_contract");
            for(int i=0;i<contractList.getLength();++i){
                Element ele = (Element)contractList.item(i);
                if(ele.getAttribute("id").equals("holding_contract")){
                    NodeList addressNodes = ele.getElementsByTagName("address");
                    int size = addressNodes.getLength();
                    for(int j=0;j<size;j++){
                        ele.removeChild(addressNodes.item(0));
                    }
                    Element newNode = xml.createElement("address");
                    newNode.setAttribute("network",networkid);
                    newNode.setTextContent(contractAddress);
                    ele.appendChild(newNode);
                    break;
                }
            }

            DOMSource source = new DOMSource(xml);
            StreamResult result = new StreamResult(new File(ticketXMLFilePath));
            transformer.transform(source, result);

            this.initTabPaneContainer();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }
}
