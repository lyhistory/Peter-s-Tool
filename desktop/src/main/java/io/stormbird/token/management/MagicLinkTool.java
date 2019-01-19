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
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    private JSplitPane mainSplitPane;
    private JPanel mainSplitPane_topPane;
    private JComboBox comboBoxKeysList;
    private JTabbedPane mainSplitPane_tabPane;
    private JPanel tabPane_container;
    private JPanel tabPane_wizard;
    private JComboBox comboBoxContractAddress;
    private JPanel tabPane_container_centerPane;
    private JTextField textFieldPrivateKey;

    private JTextField textFieldTips;
    private JTextField textFieldConnectionStatus;
    private JButton jButtonConnect;

    private static int magicLinkCount=0;
    private TokenViewModel _tokenViewModel;
    private Map<Integer, MagicLinkToolViewModel> _magicLinkViewMap;
    private static ArrayList<MagicLinkDataModel> _magicLinkDataModelArrayList;

    private Map<String,String> keys;

    private static int STEP=1;

    private void reloadMagicLink(){
        if (_tokenViewModel.comboBoxContractAddressList != null
                &&
                comboBoxKeysList != null) {
            ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem) comboBoxKeysList.getSelectedItem();
            SessionDataHelper.initContract(_tokenViewModel.comboBoxContractAddressList.get(0).getKey(),
                    _tokenViewModel.comboBoxContractAddressList.get(0).getValue(),
                    currentPrivateKeySelectedItem.getValue(), currentPrivateKeySelectedItem.getKey());
        }
        if(_magicLinkDataModelArrayList != null) {
            if (SessionDataHelper.isConnectedToWeb3()) {
                SessionDataHelper.reloadMagicLink(_magicLinkDataModelArrayList);
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
    private void initSessionData() {
        try {
            File f = new File(ConfigManager.ticketSignedXMLFilePath);
            if (f.exists() == true) {
                _tokenViewModel = new TokenViewModel(new FileInputStream(ConfigManager.ticketSignedXMLFilePath), Locale.getDefault());
            }
            //load keys and MeetupContract.xml if available
            if(keys==null) {
                keys = SessionDataHelper.loadWalletFromKeystore();
            }
            if (_tokenViewModel!=null&&_tokenViewModel.comboBoxContractAddressList != null
                    &&
                    keys != null && keys.size() > 0) {
                Map.Entry<String,String> key=keys.entrySet().iterator().next();
                SessionDataHelper.initContract(_tokenViewModel.comboBoxContractAddressList.get(0).getKey(),
                        _tokenViewModel.comboBoxContractAddressList.get(0).getValue(),
                        key.getValue(),key.getKey());
            }
            //load magiclinks if available
            _magicLinkDataModelArrayList = SessionDataHelper.loadMagicLinksFromCSV();
        }catch (IOException ex){

        }catch (SAXException ex){

        }
    }
    public MagicLinkTool(){
        try {
            _magicLinkViewMap = new ConcurrentHashMap<>();
            initSessionData();

            this.setJMenuBar(createMenuBar());
            //init using keys if available
            this.createUpperPaneForKeys();
            this.createTabPane();
            this.mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane_topPane, mainSplitPane_tabPane);
            //this.mainSplitPane.setMinimumSize(new Dimension(900,300));
            this.setContentPane(mainSplitPane);
            //this.setMinimumSize(new Dimension(900,300));
            this.setTitle("MagicLink Generator");
            //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    SessionDataHelper.saveMagicLinksToCSV(_magicLinkViewMap);
                    SessionDataHelper.savePrivateKey(keys);
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
        }catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
    public static void main(String args[]) {
        MagicLinkTool magicLinkTool = new MagicLinkTool();
        magicLinkTool.setVisible(true);
    }

    public void updateUITipPane(){
        textFieldTips.setVisible(false);
        textFieldConnectionStatus.setVisible(false);
        jButtonConnect.setVisible(false);
        if(keys==null||keys.size()==0){
            textFieldTips.setVisible(true);
            textFieldTips.setText("Please import private key");
            textFieldConnectionStatus.setVisible(false);
            jButtonConnect.setVisible(false);
        }else if(STEP==2){
            textFieldTips.setVisible(true);
            textFieldConnectionStatus.setVisible(true);
            jButtonConnect.setVisible(true);
            textFieldTips.setText("Welcome!");
            if (SessionDataHelper.isConnectedToWeb3()) {
                textFieldConnectionStatus.setText("Connected");
                textFieldConnectionStatus.setBackground(Color.green);
                jButtonConnect.setText("Reload");
                jButtonConnect.setEnabled(true);
                jButtonConnect.setForeground(Color.BLACK);
                jButtonConnect.setBackground(Color.GREEN);
                String contractOwner = SessionDataHelper.getContractOwner();
                ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem) comboBoxKeysList.getSelectedItem();
                if (contractOwner != null && contractOwner.isEmpty() == false
                        &&
                        contractOwner.equalsIgnoreCase(currentPrivateKeySelectedItem.getKey().toString()) == false) {
                    textFieldTips.setText("Warn:: current selected key is not the contract owner!! tickets creation with it will be invalid!!");
                }
            } else {
                textFieldTips.setText("Warn:: failed connection,you wouldn't know which tickets been redeemed!!");
                textFieldConnectionStatus.setText("Disconnected");
                textFieldConnectionStatus.setBackground(Color.RED);
                jButtonConnect.setText("Retry");
                jButtonConnect.setEnabled(true);
                jButtonConnect.setForeground(Color.red);
                jButtonConnect.setBackground(Color.orange);
            }
        }
        this.revalidate();
        this.repaint();
        this.pack();
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

//        menuItem = new JMenuItem("New contract behaviour XML file...",
//                KeyEvent.VK_T);
//        menu.add(menuItem);
//        menuItem = new JMenuItem("Open contract behaviour XML file...",
//                KeyEvent.VK_T);
//        menu.add(menuItem);
        menuItem = new JMenuItem("Reset",
                KeyEvent.VK_T);
        menu.add(menuItem);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (null, "Have you export your tickets? click Yes to reset, click no to cancel","Warning",dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION){
                    reset();
                }
            }
        });
        menu.addSeparator();
        menuItem = new JMenuItem("Export magic links...",
                KeyEvent.VK_T);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                int returnValue = jfc.showOpenDialog(null);
                // int returnValue = jfc.showSaveDialog(null);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = jfc.getSelectedFile();
                    System.out.println(selectedFile.getAbsolutePath());
                }
                SessionDataHelper.saveMagicLinksToCSV(_magicLinkViewMap);
                try(FileWriter fw = new FileWriter(jfc.getSelectedFile()+".csv")) {
                    BufferedWriter out = new BufferedWriter(fw);
                    File fin = new File(ConfigManager.magicLinksCSVPath);
                    FileInputStream fis = new FileInputStream(fin);
                    BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                    String aLine = null;
                    while ((aLine = in.readLine()) != null) {
                        out.write(aLine);
                        out.newLine();
                    }
                    fis.close();
                    in.close();
                    out.close();
                    fw.flush();
                }catch (Exception io){

                }
            }
        });
        menu.add(menuItem);
        menu.addSeparator();
        menuItem = new JMenuItem("Export signed xml...",
                KeyEvent.VK_S);
        menuItem.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent e) {
                                           JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

                                           int returnValue = jfc.showOpenDialog(null);

                                           if (returnValue == JFileChooser.APPROVE_OPTION) {
                                               File selectedFile = jfc.getSelectedFile();
                                               System.out.println(selectedFile.getAbsolutePath());
                                           }
                                           try(OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(jfc.getSelectedFile()+".xml"), StandardCharsets.UTF_8)) {
                                               BufferedWriter out = new BufferedWriter(fw);
                                               File fin = new File(ConfigManager.ticketSignedXMLFilePath);
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

    private void reset(){
        STEP=1;
        //reset global variables
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
        if(comboBoxKeysList!=null) {
            comboBoxKeysList.removeAllItems();
            textFieldPrivateKey.removeAll();
        }
        textFieldTips.setVisible(false);
        textFieldConnectionStatus.setVisible(false);
        jButtonConnect.setVisible(false);
        if(comboBoxContractAddress!=null) {
            comboBoxContractAddress.removeAllItems();
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
    /**
     *
     */
    private  void createUpperPaneForKeys() {
        mainSplitPane_topPane = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.TRAILING);
        //mainSplitPane_topPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainSplitPane_topPane.setLayout(new BorderLayout());
        JPanel controlsPane = new JPanel();
        JPanel leftPane = new JPanel();
        leftPane.setLayout(new GridBagLayout());
        JPanel rightPane = new JPanel();
        rightPane.setLayout(new GridBagLayout());
        rightPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        controlsPane.setLayout(flowLayout);

        leftPane.add(new JLabel("Private Key:"));
        textFieldPrivateKey = new JTextField();
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
                    if(keys!=null&&keys.size()>0&&keys.containsValue(privateKey)){
                        JOptionPane.showMessageDialog(null, "Already imported!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String address = CryptoHelper.getEthAddress(privateKey);
                    comboBoxKeysList.addItem(new ComboBoxSimpleItem(address,privateKey));
                    if(keys==null){
                        keys = new ConcurrentHashMap<>();
                    }
                    keys.put(address,privateKey);
                    if(keys.size()>1){
                        reloadMagicLink();
                    }
                    updateUITipPane();
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
        comboBoxKeysList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                updateUITipPane();
            }
        });
        controlsPane.add(leftPane);
        controlsPane.add(rightPane);
        controlsPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        mainSplitPane_topPane.add(controlsPane,BorderLayout.NORTH);
        JPanel tipsPane = new JPanel();
        tipsPane.setLayout(flowLayout);
        textFieldTips=new JTextField();
        textFieldTips.setVisible(false);
        textFieldTips.setEditable(false);
        textFieldTips.setBackground(Color.yellow);
        textFieldTips.setFont(new Font("SansSerif", Font.BOLD, 15));
        tipsPane.add(textFieldTips);
        textFieldConnectionStatus=new JTextField();
        textFieldConnectionStatus.setVisible(false);
        textFieldConnectionStatus.setEditable(false);
        tipsPane.add(textFieldConnectionStatus);
        jButtonConnect=new JButton();
        jButtonConnect.setVisible(false);
        jButtonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reloadMagicLink();
                updateUITipPane();
            }
        });
        tipsPane.add(jButtonConnect);
        tipsPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        updateUITipPane();
        mainSplitPane_topPane.add(tipsPane,BorderLayout.SOUTH);

    }

    /**
     * rely on _magicLinkDataModelArrayList (magiclinks.csv)
     * tab pane for:
     * 1. creation wizard (provide contract address, network type, private key)
     * 2. magic link generation Panel
     * @throws IOException
     * @throws SAXException
     */
    private  void createTabPane() throws IOException, SAXException {
        //init tab pane
        mainSplitPane_tabPane = new JTabbedPane();
        tabPane_container = new JPanel();
        tabPane_container.setLayout(new BoxLayout(tabPane_container, BoxLayout.Y_AXIS));
        tabPane_container.setBorder(new EmptyBorder(10, 10, 10, 10));
        if(_magicLinkDataModelArrayList==null||_magicLinkDataModelArrayList.size()==0){
            STEP=1;
            createWizard();
        }else{
            STEP=2;
            createMagicLinkPane();
        }
        mainSplitPane_tabPane.addTab("Meetup[x]",null, tabPane_container, "");
        mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_1);
        mainSplitPane_tabPane.addTab("[+]",null, null, "");
        mainSplitPane_tabPane.setMnemonicAt(0, KeyEvent.VK_2);
        mainSplitPane_tabPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        mainSplitPane_tabPane.setMinimumSize(new Dimension(0,300));
    }

    /**
     * one step wizard:
     * contract address + network type + private key
     */
    private void createWizard(){
        tabPane_wizard = new JPanel();
        tabPane_wizard.setLayout(new BoxLayout(tabPane_wizard, BoxLayout.Y_AXIS));
        tabPane_wizard.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel northPane = new JPanel();
        northPane.add(new JLabel("Deploy a new contract"));
        JTextField textFieldContractAddress = new JTextField(10);
        JComboBox comboBoxNetworkID=new JComboBox();
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("mainnet", "1"));
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("Ropsten", "3"));
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
                    textFieldPrivateKey.requestFocus();
                }else {
                    ComboBoxSimpleItem selectedNetworkItem = (ComboBoxSimpleItem) comboBoxNetworkID.getSelectedItem();
                    String networkid = selectedNetworkItem.getValue();
                    String contractAddress = textFieldContractAddress.getText();
                    // todo validation
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
                        //
                        STEP = 2;
                        ComboBoxSimpleItem currentPrivateKeySelectedItem = (ComboBoxSimpleItem)comboBoxKeysList.getSelectedItem();
                        XmlHelper.processContractXml(networkid, contractAddress,currentPrivateKeySelectedItem.getValue());
                        initSessionData();
                        updateUITipPane();
                        createMagicLinkPane();
                    }
                }
            }
        });
        southPane.add(buttonNextStep);
        tabPane_wizard.add(northPane,BorderLayout.NORTH);
        tabPane_wizard.add(centerPane,BorderLayout.CENTER);
        tabPane_wizard.add(southPane,BorderLayout.SOUTH);
        tabPane_container.add(tabPane_wizard);
    }

    /**
     * Magic Link creation Pane
     */
    private void createMagicLinkPane() {
        //NorthPane: Contract Address
        JPanel northPane = new JPanel();
        northPane.add(new JLabel("Contract:"));
        comboBoxContractAddress = new JComboBox();
        if (_tokenViewModel == null) {
            //log error
            //alert something broken
            return;
        }
        for (ComboBoxSimpleItem item : _tokenViewModel.comboBoxContractAddressList) {
            comboBoxContractAddress.addItem(item);
            comboBoxContractAddress.setEnabled(true);
        }
        northPane.add(comboBoxContractAddress);

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
        //render from autosaved magiclink.csv
        if (_magicLinkDataModelArrayList == null || _magicLinkDataModelArrayList.size() == 0) {
            addAnotherTicket(null);
        } else {
            for (int i = 0; i < _magicLinkDataModelArrayList.size(); ++i) {
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

        tabPane_container.add(northPane, BorderLayout.NORTH);
        tabPane_container.add(centerPane, BorderLayout.CENTER);
        tabPane_container.add(buttonAddAnother, BorderLayout.SOUTH);
        if (tabPane_wizard != null) {
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

    /**
     * MagicLink Helper
     */
    // calculate tokenID and generate magic link from _magicLinkViewMap
    private void generateMagicLink(int rowNum){
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
}
