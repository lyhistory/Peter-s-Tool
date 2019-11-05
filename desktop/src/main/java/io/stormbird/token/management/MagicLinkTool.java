package io.stormbird.token.management;

import io.stormbird.token.management.CustomComponents.DateTimePicker;
import io.stormbird.token.management.CustomComponents.JTextFieldLimit;
import io.stormbird.token.management.Model.*;
import io.stormbird.token.management.Util.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;
import java.util.List;
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
    private static int STEP = 1;

    private JSplitPane mainSplitPane;
    private JPanel mainSplitPane_topPane;
    private JComboBox global_comboBoxKeysList;
    private JTabbedPane mainSplitPane_tabPane; //

    private JPanel tabPane_container;
    private JPanel tabPane_wizard;
    private JComboBox global_comboBoxContractAddress;
    private JPanel tabPane_container_centerPane;
    private JTextField global_textFieldPrivateKey;

    private JTextField global_textFieldTips;
    private JTextField global_textFieldConnectionStatus;
    private JButton global_buttonConnect;

    private static int magicLinkCount=0;
    private Map<String,String> keys;
    private TokenViewModel _tokenViewModel;
    private Map<Integer, MagicLinkToolViewModel> _magicLinkViewMap;
    private static ArrayList<MagicLinkDataModel> _magicLinkDataModelArrayList;

    public MagicLinkTool(){
        try {
            this._magicLinkViewMap = new ConcurrentHashMap<>();

            //this.resumeSessionData();   //for rendering createTopPane(), createMainTabPane() and updateWeb3StatusUI()

            this.setJMenuBar(this.createMenuBar());
            this.createTopPane();
            this.createMainTabPane();

            this.updateWeb3StatusUI();

            this.mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane_topPane, mainSplitPane_tabPane);
            //this.mainSplitPane.setMinimumSize(new Dimension(900,300));
            this.setContentPane(mainSplitPane);
            //this.setMinimumSize(new Dimension(900,300));
            this.setTitle("MagicLink Generator");
            //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    SessionDataHelper.saveSession(_magicLinkViewMap, keys);
                    super.windowClosing(e);
                    System.exit(0);
                }
            });
            this.setLocationByPlatform(true);
            this.setResizable(true);
            this.revalidate();
            this.repaint();
            this.pack();
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            //log exception
        }catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) {
        ConfigManager.init();
        MagicLinkTool magicLinkTool = new MagicLinkTool();
        magicLinkTool.setVisible(true);
    }

    /**
     * Create Top Menu
     * @return
     */
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;

        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);

        menuItem = new JMenuItem("Reset",
                KeyEvent.VK_B);
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (null,
                        "Have you export your tickets? click Yes to reset, click no to cancel","Warning",dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION){
                    reset();
                }
            }
        });
        menu.addSeparator();
        menuItem = new JMenuItem("Export magic links...",
                KeyEvent.VK_C);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnValue = jfc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    SessionDataHelper.saveMagicLinksToCSV(_magicLinkViewMap);
                    FileHelper.saveToFile(ConfigManager.magicLinksCSVPath, jfc.getSelectedFile()+".csv");
                }

            }
        });
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Export signed xml...",
                KeyEvent.VK_D);
        menuItem.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent e) {
                                           JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                                           int returnValue = jfc.showOpenDialog(null);
                                           if (returnValue == JFileChooser.APPROVE_OPTION) {
                                               FileHelper.saveToFile(ConfigManager.ticketSignedXMLFilePath,jfc.getSelectedFile()+".xml");
                                           }
                                       }
                                   });
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

    /**
     * Import Private Keys, Show web3 status
     */
    private  void createTopPane() {
        this.mainSplitPane_topPane = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        //mainSplitPane_topPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.mainSplitPane_topPane.setLayout(new BorderLayout());
        JPanel controlsPane = new JPanel();
        controlsPane.setLayout(flowLayout);
        controlsPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JPanel leftPane = new JPanel();
        leftPane.setLayout(new GridBagLayout());
        JPanel rightPane = new JPanel();
        rightPane.setLayout(new GridBagLayout());
        rightPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));

        leftPane.add(new JLabel("Private Key:"));
        global_textFieldPrivateKey = new JTextField();
        global_textFieldPrivateKey.setColumns(30);
        leftPane.add(global_textFieldPrivateKey);
        JButton buttonImport=new JButton();
        buttonImport.setText("Click to Import");
        buttonImport.setForeground(Color.RED);
        buttonImport.setBackground(Color.GREEN);
        buttonImport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String privateKey=global_textFieldPrivateKey.getText();
                    if(keys!=null&&keys.size()>0&&keys.containsValue(privateKey)){
                        JOptionPane.showMessageDialog(null, "Already imported!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String address = CryptoHelper.getEthAddress(privateKey);
                    if(keys==null){
                        keys = new ConcurrentHashMap<>();
                    }
                    keys.put(address,privateKey);
                    if(keys.size()>1){
                        reloadMagicLinkStatusUI();
                    }
                    // trigger updateWeb3StatusUI()
                    global_comboBoxKeysList.addItem(new ComboBoxSimpleItem(address,privateKey));
                }catch (Exception ex){
                    JOptionPane.showMessageDialog(null, "Invalid PrivateKey!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        leftPane.add(buttonImport);
        rightPane.add(new JLabel("Current Key:"));
        global_comboBoxKeysList = new JComboBox();
        global_comboBoxKeysList.setPreferredSize(new Dimension(200, 30));
        if(keys!=null&&keys.size()>0){
            for(String key:keys.keySet()){
                global_comboBoxKeysList.addItem(new ComboBoxSimpleItem(key,keys.get(key)));
            }
        }
        rightPane.add(global_comboBoxKeysList);
        global_comboBoxKeysList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateWeb3StatusUI();
            }
        });
        controlsPane.add(leftPane);
        controlsPane.add(rightPane);
        this.mainSplitPane_topPane.add(controlsPane,BorderLayout.NORTH);
        JPanel tipsPane = new JPanel();
        tipsPane.setLayout(flowLayout);
        global_textFieldTips =new JTextField();
        global_textFieldTips.setText("Welcome to use Peter's tool");
        global_textFieldTips.setVisible(true);
        global_textFieldTips.setEditable(false);
        global_textFieldTips.setBackground(Color.yellow);
        global_textFieldTips.setFont(new Font("SansSerif", Font.BOLD, 15));
        tipsPane.add(global_textFieldTips);
        global_textFieldConnectionStatus =new JTextField();
        global_textFieldConnectionStatus.setVisible(false);
        global_textFieldConnectionStatus.setEditable(false);
        tipsPane.add(global_textFieldConnectionStatus);
        global_buttonConnect =new JButton();
        global_buttonConnect.setVisible(false);
        global_buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadMagicLinkStatusUI();
                updateWeb3StatusUI();
            }
        });
        tipsPane.add(global_buttonConnect);
        tipsPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        mainSplitPane_topPane.add(tipsPane,BorderLayout.SOUTH);
        this.setResizable(true);
        this.revalidate();
        this.repaint();
        this.pack();
    }

    /**
     * rely on _magicLinkDataModelArrayList (magiclinks.csv)
     * tab pane for:
     * 1. creation wizard (provide contract address, network type, private key)
     * 2. magic link generation Panel
     * @throws IOException
     * @throws SAXException
     */
    private  void createMainTabPane() throws IOException, SAXException {
        //init tab pane
        this.mainSplitPane_tabPane = new JTabbedPane();
        this.tabPane_container = new JPanel();
        this.tabPane_container.setLayout(new BoxLayout(this.tabPane_container, BoxLayout.Y_AXIS));
        this.tabPane_container.setBorder(new EmptyBorder(10, 10, 10, 10));
        if(this._magicLinkDataModelArrayList==null||this._magicLinkDataModelArrayList.size()==0){
            createWizard();
        }else{
            //resume magiclink pane
            createMagicLinkPane();
        }
        this.mainSplitPane_tabPane.addTab("Meetup[x]",null, tabPane_container, "");
        this.mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_1);
        this.mainSplitPane_tabPane.addTab("[+]",null, null, "");
        this.mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_2);
        this.mainSplitPane_tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        this.mainSplitPane_tabPane.setMinimumSize(new Dimension(0,300));
    }

    /**
     * one step wizard:
     * contract address + network type + private key
     */
    private void createWizard(){
        this.tabPane_wizard = new JPanel();
        this.tabPane_wizard.setLayout(new BoxLayout(this.tabPane_wizard, BoxLayout.Y_AXIS));
        this.tabPane_wizard.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel northPane = new JPanel();
        northPane.add(new JLabel("Deploy a new contract"));
        JTextField textFieldContractAddress = new JTextField(10);
        JComboBox comboBoxNetworkID=new JComboBox();
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("mainnet", "1"));
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("Ropsten", "3"));
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("xDai", "100"));
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("CustomRPC", "0"));

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
                if(keys==null||keys.size()<1){
                    JOptionPane.showMessageDialog(
                            null,
                            "Please import the private key which create this contract!",
                            "Warn",
                            JOptionPane.WARNING_MESSAGE);
                    global_textFieldPrivateKey.requestFocus();
                }else {
                    String contractAddress = textFieldContractAddress.getText();
                    if (contractAddress == null || contractAddress.equals("")) {
                        textFieldContractAddress.selectAll();
                        JOptionPane.showMessageDialog(
                                null,
                                "Please key in the contract address",
                                "Warn",
                                JOptionPane.WARNING_MESSAGE);
                        contractAddress = null;
                        textFieldContractAddress.requestFocusInWindow();
                    } else {
                        // todo validation
                        ComboBoxSimpleItem selectedNetworkItem = (ComboBoxSimpleItem) comboBoxNetworkID.getSelectedItem();
                        String networkId = selectedNetworkItem.getValue();
                        ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem)global_comboBoxKeysList.getSelectedItem();
                        XmlHelper.processContractXml(networkId, contractAddress, currentPrivateKeySelectedItem.getValue());
                        goNext();
                    }
                }
            }
        });
        southPane.add(buttonNextStep);
        this.tabPane_wizard.add(northPane,BorderLayout.NORTH);
        this.tabPane_wizard.add(centerPane,BorderLayout.CENTER);
        this.tabPane_wizard.add(southPane,BorderLayout.SOUTH);
        this.tabPane_container.add(this.tabPane_wizard);
    }

    private void goNext(){
        STEP = 2;
        //ConfigManager.ticketSignedXMLFilePath+=contractAddress+".xml";

        initContract();
        updateWeb3StatusUI();
        createMagicLinkPane();

        this.mainSplitPane_topPane.revalidate();
        this.mainSplitPane_topPane.repaint();
        this.pack();
    }

    /**
     * Magic Link creation Pane
     */
    private void createMagicLinkPane() {
        //NorthPane: Contract Address
        JPanel northPane = new JPanel();
        northPane.add(new JLabel("Contract:"));
        global_comboBoxContractAddress = new JComboBox();
        if (_tokenViewModel == null) {
            JOptionPane.showMessageDialog(null,
                    "Fatal Error! please contact lyhistory@gmail.com");
            return;
        }
        for (ComboBoxSimpleItem item : _tokenViewModel.comboBoxContractAddressList) {
            global_comboBoxContractAddress.addItem(item);
            global_comboBoxContractAddress.setEnabled(true);
        }
        northPane.add(global_comboBoxContractAddress);

        //CenterPane: main container for magic link
        GridBagConstraints col1Constraints = new GridBagConstraints();
        col1Constraints.fill = GridBagConstraints.BOTH;
        col1Constraints.anchor = GridBagConstraints.CENTER;
        col1Constraints.ipadx = 10;
        col1Constraints.ipady = 10;
        col1Constraints.weightx = 0.5;
        col1Constraints.gridwidth = 1;
        col1Constraints.gridx = 0;
        col1Constraints.gridy = 0;
        GridBagConstraints col2Constraints = new GridBagConstraints();
        col2Constraints.fill = GridBagConstraints.BOTH;
        col2Constraints.anchor = GridBagConstraints.CENTER;
        col2Constraints.ipadx = 10;
        col2Constraints.ipady = 10;
        col2Constraints.weightx = 0.5;
        col2Constraints.gridwidth = 1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = 0;
        JPanel centerPane = new JPanel();
        centerPane.setLayout(new BoxLayout(centerPane, BoxLayout.Y_AXIS));
        tabPane_container_centerPane = new JPanel();
        tabPane_container_centerPane.setLayout(new GridBagLayout());
        centerPane.add(tabPane_container_centerPane, BorderLayout.CENTER);

        //render container
        //render title
        initMagicLinkCreationColumnTitle();
        if (_magicLinkDataModelArrayList == null || _magicLinkDataModelArrayList.size() == 0) {
            addAnotherTicket(null);
        } else {
            //resume
            for (int i = 0; i < _magicLinkDataModelArrayList.size(); ++i) {
                addAnotherTicket(_magicLinkDataModelArrayList.get(i));
            }
        }

        //BottomPane: add another button
        JPanel moreMagicLinkButtonPanel =new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        moreMagicLinkButtonPanel.setLayout(flowLayout);
        JButton buttonAddAnother = new JButton();
        buttonAddAnother.setText("Add Another");
        buttonAddAnother.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addAnotherTicket(null);
            }
        });
        moreMagicLinkButtonPanel.add(buttonAddAnother);
        JButton buttonDuplicateAnother = new JButton();
        buttonDuplicateAnother.setText("Duplicate Another");
        buttonDuplicateAnother.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(_magicLinkViewMap!=null&&_magicLinkViewMap.size()>0){
                    MagicLinkToolViewModel model= _magicLinkViewMap.get(magicLinkCount);
                    String lastMagicLink = model.TextFieldMagicLink.getText();
                    MagicLinkDataModel magicLinkData = SessionDataHelper.readMagicLink(lastMagicLink);
                    addAnotherTicket(magicLinkData);
                }else {
                    addAnotherTicket(null);
                }
            }
        });
        moreMagicLinkButtonPanel.add(buttonDuplicateAnother);
        tabPane_container.add(northPane, BorderLayout.NORTH);
        tabPane_container.add(centerPane, BorderLayout.CENTER);
        tabPane_container.add(moreMagicLinkButtonPanel, BorderLayout.SOUTH);
        if (tabPane_wizard != null) {
            tabPane_wizard.removeAll();
            tabPane_wizard.setVisible(false);
        }
        this.setResizable(true);
//        this.revalidate();
//        this.repaint();
        this.pack();
    }
    // render the column title by xml attributes + token status/remark
    private void initMagicLinkCreationColumnTitle(){
        int gridx,gridy;
        gridx=gridy=0;
        // render dropdown list
        for(ComboBoxDataModel comboBoxDataModel : _tokenViewModel.comboBoxDataModelList){
            GridBagConstraints colConstraints = JSwingHelper.getGridConstraints(gridx,gridy,0.5,0);
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(comboBoxDataModel.name);
            tabPane_container_centerPane.add(labelAttrName,colConstraints);
            gridx++;
        }
        for(TextFieldDataModel model : _tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = JSwingHelper.getGridConstraints(gridx,gridy,0.4,0);
            JLabel labelAttrName = new JLabel();
            if(model.getPlaceholder()!=null&&model.getPlaceholder()!=""){
                labelAttrName.setText(model.getName()+"("+model.getPlaceholder()+")");
            }else {
                labelAttrName.setText(model.getName());
            }
            tabPane_container_centerPane.add(labelAttrName,colConstraints);
            gridx++;
        }
        tabPane_container_centerPane.add(new JLabel("Token Status"),JSwingHelper.getGridConstraints(gridx,gridy,0.6,0));
        gridx++;
        tabPane_container_centerPane.add(new JLabel("Remark"),JSwingHelper.getGridConstraints(gridx,gridy,0.4,0));

    }
    // add new magicLink generation form in row, managed by Map<Integer, MagicLinkToolViewModel> _magicLinkViewMap
    private void addAnotherTicket(MagicLinkDataModel magicLinkData){
        boolean enabled=true;
        BigInteger tokenID = BigInteger.valueOf(0);
        if(magicLinkData!=null&&magicLinkData.tickets.length>0){
            tokenID=magicLinkData.tickets[0];
            enabled=magicLinkData.enabled;
        }
        String currentTimezone = JSwingHelper.getCurrentTimezone();

        int gridx=0;
        magicLinkCount++;
        MagicLinkToolViewModel magicLinkViewModel=new MagicLinkToolViewModel();
        JTextField textFieldRowNum = new JTextField();
        textFieldRowNum.setName(Integer.toString(magicLinkCount));
        // draw dropdownlist by XML map attributes
        for(ComboBoxDataModel comboBoxDataModel : _tokenViewModel.comboBoxDataModelList){
            GridBagConstraints colConstraints = JSwingHelper.getGridConstraints(gridx,magicLinkCount,0.5,0);
            ComboBoxDataModel.ComboBoxOption[] options=comboBoxDataModel.getComboBoxOptions();
            JComboBox comboBox = new JComboBox(options);
            comboBox.setName(comboBoxDataModel.getId());
            comboBox.setEnabled(enabled);
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
                    int rowNum = Integer.valueOf(textFieldRowNum.getName());
                    if(rowNum<=0){
                        //log expected
                        return;
                    }
                    if(keys==null||keys.size()<=0){
                        JOptionPane.showMessageDialog(null,
                                "Please Provide private key.");
                        return;
                    }
                    generateMagicLink(rowNum);
                }
            });
            gridx++;
            magicLinkViewModel.setComboBoxForXMLList(comboBox);
        }
        // draw textfield&datepicker by XML normal&time attributes
        for(TextFieldDataModel model : _tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = JSwingHelper.getGridConstraints(gridx,magicLinkCount,0.4,0);
            BigInteger valWithoutMask = tokenID.and(model.bitmask).shiftRight(model.bitshift);
            if(model.id.equals("time")) {
                JTextField textFieldHiddenValue = new JTextField(); //for trigger onchange event
                textFieldHiddenValue.setVisible(false);

                JPanel dateTimePickerPane = new JPanel();
                JButton dateTimePickerTime = new DateTimePicker(textFieldHiddenValue);
                dateTimePickerTime.setName(model.id);
                dateTimePickerTime.setEnabled(enabled);
                JComboBox timeZoneTime = JSwingHelper.createDatePicker(dateTimePickerPane, dateTimePickerTime);
                timeZoneTime.setEnabled(enabled);
                tabPane_container_centerPane.add(dateTimePickerPane, colConstraints);
                if(valWithoutMask.equals(BigInteger.valueOf(0))==false){
                    currentTimezone = MagicLinkDataModel.getTimezoneStrByValue(valWithoutMask);
                    String dateStr=MagicLinkDataModel.getDateStrByValue(valWithoutMask,currentTimezone);
                    ((DateTimePicker) dateTimePickerTime).setDate(dateStr);
                    JSwingHelper.setSelectedItem(currentTimezone,timeZoneTime);
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
                JTextField textFieldInput;
                textFieldInput = new JTextField();
                if(model.getLengthlimit()>0){
                    textFieldInput.setDocument(new JTextFieldLimit(model.getLengthlimit()));
                }
                textFieldInput.setName(model.id);
                textFieldInput.setEditable(enabled);
                textFieldInput.setEnabled(enabled);
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
        tokenStatusPane.add(new JLabel("Price"));
        JTextField textFieldPriceInEth = new JTextField();
        textFieldPriceInEth.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                try {
                    generateMagicLink(Integer.valueOf(textFieldRowNum.getName()));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid Data Type! Please check the type",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    textFieldPriceInEth.setText("");
                    textFieldPriceInEth.requestFocusInWindow();
                }
            }
        });
        if(magicLinkData!=null){
            textFieldPriceInEth.setText(Double.toString(magicLinkData.price));
        }else {
            textFieldPriceInEth.setText("0");
        }
        textFieldPriceInEth.setColumns(6);
        magicLinkViewModel.setTextFieldPriceInEth(textFieldPriceInEth);
        tokenStatusPane.add(textFieldPriceInEth);
        tokenStatusPane.add(new JLabel("expiry"));
        JPanel dateTimePickerPane = new JPanel();
        JTextField textFieldHiddenValue = new JTextField();
        textFieldHiddenValue.setVisible(false);
        JButton dateTimePickerExpireTime = new DateTimePicker(textFieldHiddenValue);
        dateTimePickerExpireTime.setEnabled(enabled);
        JComboBox timeZoneExpireTime =JSwingHelper.createDatePicker(dateTimePickerPane, dateTimePickerExpireTime);
        timeZoneExpireTime.setEnabled(enabled);
        tokenStatusPane.add(dateTimePickerPane);
        magicLinkViewModel.setDateTimePickerExpire(dateTimePickerExpireTime,timeZoneExpireTime);
        if(magicLinkData!=null&&magicLinkData.expiry!=0){
            String dateStr=MagicLinkDataModel.getDateStrByValue(magicLinkData.expiry,currentTimezone);
            ((DateTimePicker) dateTimePickerExpireTime).setDate(dateStr);
            JSwingHelper.setSelectedItem(currentTimezone,timeZoneExpireTime);
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
//        tokenStatusPane.add(new JLabel("ask"));
//        JTextField textFieldOwner=new JTextField();
//        textFieldOwner.setColumns(8);
//        tokenStatusPane.add(textFieldOwner);
//        magicLinkViewModel.TextFieldOwner=textFieldOwner;

        tokenStatusPane.add(new JLabel("redeem by"));
        JTextField textFieldMagicLink=new JTextField();
        textFieldMagicLink.setEditable(false);
        textFieldMagicLink.setColumns(15);
        if(magicLinkData!=null) {
            textFieldMagicLink.setText(magicLinkData.magicLink);
        }
        tokenStatusPane.add(textFieldMagicLink);

        tabPane_container_centerPane.add(tokenStatusPane,JSwingHelper.getGridConstraints(gridx,magicLinkCount,0.6,0));
        gridx++;
        JTextField textFieldRemark = new JTextField();
        textFieldRemark.setColumns(15);
        if(magicLinkData!=null) {
            textFieldRemark.setText(magicLinkData.remark);
        }
        tabPane_container_centerPane.add(textFieldRemark,JSwingHelper.getGridConstraints(gridx,magicLinkCount,0.4,0));

        magicLinkViewModel.TextFieldMagicLink=textFieldMagicLink;
        magicLinkViewModel.TextFieldRemark = textFieldRemark;
        _magicLinkViewMap.put(magicLinkCount,magicLinkViewModel);
        this.validate();
        this.repaint();
        this.pack();
    }

    // calculate tokenID and generate magic link from _magicLinkViewMap
    private void generateMagicLink(int rowNum){
        // it's time to create the magic link!
        ComboBoxSimpleItem contractAddressSelectedItem = (ComboBoxSimpleItem)global_comboBoxContractAddress.getSelectedItem();
        ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem)global_comboBoxKeysList.getSelectedItem();
        MagicLinkHelper.generateMagicLink(rowNum,_magicLinkViewMap,contractAddressSelectedItem.getKey(),currentPrivateKeySelectedItem.getValue());
    }


    //resume reload update reset
    private void reloadMagicLinkStatusUI(){
        if (_tokenViewModel.comboBoxContractAddressList != null
                &&
                global_comboBoxKeysList != null) {
            ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem) global_comboBoxKeysList.getSelectedItem();
            SessionDataHelper.initContract(_tokenViewModel.comboBoxContractAddressList.get(0).getKey(),
                    _tokenViewModel.comboBoxContractAddressList.get(0).getValue(),
                    currentPrivateKeySelectedItem.getValue(), currentPrivateKeySelectedItem.getKey());
        }
        if(_magicLinkDataModelArrayList != null) {
            if (SessionDataHelper.isConnectedToWeb3()) {
                SessionDataHelper.reloadMagicLinkStatus(_magicLinkDataModelArrayList);
                for (Integer rowNo : _magicLinkViewMap.keySet()) {
                    boolean redeemed = _magicLinkDataModelArrayList.get(rowNo - 1).redeemped;
                    Map<JTextField, TextFieldDataModel> textFieldMap = _magicLinkViewMap.get(rowNo).TextFieldForXMLMap;
                    for (JTextField textField : textFieldMap.keySet()) {
                        textField.setEnabled(!redeemed);
                    }
                    List<JComboBox> comboBoxes = _magicLinkViewMap.get(rowNo).ComboBoxForXMLList;
                    for (int i = 0; i < comboBoxes.size(); ++i) {
                        comboBoxes.get(i).setEnabled(!redeemed);
                    }
                    Map<DateTimePickerViewModel, TextFieldDataModel> datetimePickerMap = _magicLinkViewMap.get(rowNo).DateTimePickerMap;
                    for (DateTimePickerViewModel dateTimePicker : datetimePickerMap.keySet()) {
                        dateTimePicker.TimeZone.setEnabled(!redeemed);
                        dateTimePicker.DateTimePickerTime.setEnabled(!redeemed);
                    }
                }
            }
        }
    }
    private void initContract(){
        //dependent on keys and contract
        try {
            _tokenViewModel = new TokenViewModel(new FileInputStream(ConfigManager.ticketSignedXMLFilePath), Locale.getDefault());
            if (_tokenViewModel!=null&&_tokenViewModel.comboBoxContractAddressList != null
                    &&
                    keys != null && keys.size() > 0) {
                Map.Entry<String,String> key=keys.entrySet().iterator().next();
                SessionDataHelper.initContract(_tokenViewModel.comboBoxContractAddressList.get(0).getKey(),
                        _tokenViewModel.comboBoxContractAddressList.get(0).getValue(),
                        key.getValue(),key.getKey());
            }
        }catch (Exception ex){

        }
    }
    private void resumeSessionData() {
        try {
            //load keys and MeetupContract.xml if available
            if(keys==null) {
                keys = SessionDataHelper.loadWalletFromKeystore();
            }

            initContract();

            //load magiclinks if available
            _magicLinkDataModelArrayList = SessionDataHelper.loadMagicLinksFromCSV();
            if(_magicLinkDataModelArrayList==null||_magicLinkDataModelArrayList.size()==0){
                STEP = 1;
            }else{
                STEP = 2;
            }

        }catch (Exception ex){

        }
    }

    public void updateWeb3StatusUI(){
        //global_textFieldTips.setVisible(false);
        global_textFieldConnectionStatus.setVisible(false);
        global_buttonConnect.setVisible(false);
        if(keys==null||keys.size()==0){
            global_textFieldTips.setVisible(true);
            global_textFieldTips.setText("Please import private key");
            global_textFieldConnectionStatus.setVisible(false);
            global_buttonConnect.setVisible(false);
        }else if(STEP == 2){
            global_textFieldTips.setVisible(true);
            global_textFieldConnectionStatus.setVisible(true);
            global_buttonConnect.setVisible(true);
            global_textFieldTips.setText("Welcome!");
            if (SessionDataHelper.isConnectedToWeb3()) {
                global_textFieldConnectionStatus.setText("Connected");
                global_textFieldConnectionStatus.setBackground(Color.green);
                global_buttonConnect.setText("Reload");
                global_buttonConnect.setEnabled(true);
                global_buttonConnect.setForeground(Color.BLACK);
                global_buttonConnect.setBackground(Color.GREEN);
                String contractOwner = SessionDataHelper.getContractOwner();
                ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem) global_comboBoxKeysList.getSelectedItem();
                if (contractOwner != null && contractOwner.isEmpty() == false
                        &&
                        contractOwner.equalsIgnoreCase(currentPrivateKeySelectedItem.getKey().toString()) == false) {
                    global_textFieldTips.setText("Warning:: current selected key is not the contract owner! tickets creation with it will be invalid!!!");
                }
            } else {
                global_textFieldTips.setText("Warning:: failed connection, you wouldn't know which tickets been redeemed!!!");
                global_textFieldConnectionStatus.setText("Disconnected");
                global_textFieldConnectionStatus.setBackground(Color.RED);
                global_buttonConnect.setText("Retry");
                global_buttonConnect.setEnabled(true);
                global_buttonConnect.setForeground(Color.RED);
                global_buttonConnect.setBackground(Color.ORANGE);
            }
        }
        this.revalidate();
        this.repaint();
        this.pack();
    }

    private void reset(){
        //reset global variables
        STEP = 1;
        magicLinkCount=0;
        if(_magicLinkViewMap!=null) {
            _magicLinkViewMap.clear();
        }
        if(_magicLinkDataModelArrayList!=null) {
            _magicLinkDataModelArrayList.clear();
        }
        if(keys!=null) {
            keys.clear();
        }
        //clean file
        FileHelper.deleteFile(ConfigManager.ticketSignedXMLFilePath);
        FileHelper.deleteFile(ConfigManager.magicLinksCSVPath);
        FileHelper.deleteFile(ConfigManager.privateKeyFilePath);
        //reset UI
        if(global_comboBoxKeysList!=null) {
            global_comboBoxKeysList.removeAllItems();
            global_textFieldPrivateKey.removeAll();
        }
        global_textFieldTips.setVisible(false);
        global_textFieldConnectionStatus.setVisible(false);
        global_buttonConnect.setVisible(false);
        if(global_comboBoxContractAddress!=null) {
            global_comboBoxContractAddress.removeAllItems();
        }

        if(tabPane_container_centerPane!=null) {
            tabPane_container_centerPane.removeAll();
        }
        if(tabPane_container!=null){
            tabPane_container.removeAll();
        }

        createWizard();
        this.revalidate();
        this.repaint();
        this.pack();
    }
}
