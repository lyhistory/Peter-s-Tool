package io.stormbird.token.management;

import io.stormbird.token.management.CustomComponents.DateTimePicker;
import io.stormbird.token.management.Model.ComboBoxDataModel;
import io.stormbird.token.management.Model.ComboBoxSimpleItem;
import io.stormbird.token.management.Model.TextFieldDataModel;
import io.stormbird.token.management.Model.TokenViewModel;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MagicLinkTool extends JFrame{

    public InputStream ticketXML = getClass().getResourceAsStream("/TicketingContract.xml");
    private static Map<String,BigInteger> encodedValueMap=new ConcurrentHashMap<>();
    private TokenViewModel tokenViewModel;
    private JSplitPane mainSplitPane;
    private JPanel mainSplitPane_topPane;
    JComboBox comboBoxKeysList;

    private JTabbedPane mainSplitPane_tabPane;
    private JPanel tabPane_container;
    private JPanel tabPane_container_titlePane;
    private JPanel tabPane_container_contentPane;
    private static int magicLinkCount=0;

    public MagicLinkTool(){
        try {
            tokenViewModel=new TokenViewModel(ticketXML, Locale.getDefault());

            this.setJMenuBar(createMenuBar());

            initUpperPane();
            initTabPane();
            mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitPane_topPane, mainSplitPane_tabPane);
            this.setContentPane(mainSplitPane);
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
        mainSplitPane_topPane.setLayout(new BoxLayout(mainSplitPane_topPane, BoxLayout.Y_AXIS));
        JPanel controlsPane = new JPanel();
        JPanel leftPane = new JPanel();
        leftPane.setLayout(new GridBagLayout());
        JPanel rightPane = new JPanel();
        rightPane.setLayout(new GridBagLayout());
        rightPane.setBorder(BorderFactory.createLineBorder(Color.GREEN));
        controlsPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        controlsPane.setLayout(new GridBagLayout());
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
        col2Constraints.weightx=0.7;
        col2Constraints.gridwidth=1;
        col2Constraints.gridx = 1;
        col2Constraints.gridy = 0;
        GridBagConstraints col3Constraints = new GridBagConstraints();
        col3Constraints.fill = GridBagConstraints.BOTH;
        col3Constraints.anchor=GridBagConstraints.CENTER;
        col3Constraints.ipadx=10;col2Constraints.ipady=10;
        col3Constraints.weightx=0.7;
        col3Constraints.gridwidth=1;
        col3Constraints.gridx = 1;
        col3Constraints.gridy = 0;

        leftPane.add(new JLabel("Import PrivateKey:"),col1Constraints);
        JTextField textFieldPrivateKey = new JTextField();
        leftPane.add(textFieldPrivateKey,col2Constraints);

        rightPane.add(new JLabel("Current Key:"),col1Constraints);
        comboBoxKeysList = new JComboBox();
        rightPane.add(comboBoxKeysList,col2Constraints);
        controlsPane.add(leftPane,col1Constraints);
        controlsPane.add(rightPane,col2Constraints);
        mainSplitPane_topPane.add(controlsPane,BorderLayout.CENTER);
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
        JComboBox comboBoxContractAddress = new JComboBox();
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
        tabPane_container_titlePane = new JPanel();
        tabPane_container_titlePane.setLayout(new GridBagLayout());
        tabPane_container_contentPane = new JPanel();
        tabPane_container_contentPane.setLayout(new GridBagLayout());
        centerPane.add(tabPane_container_titlePane,BorderLayout.NORTH);
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

    private GridBagConstraints getGridConstraints(int gridx,int gridy,double weightx){
        GridBagConstraints colConstraints = new GridBagConstraints();
        colConstraints.fill = GridBagConstraints.BOTH;
        colConstraints.anchor=GridBagConstraints.CENTER;
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
            GridBagConstraints colConstraints = getGridConstraints(gridx,gridy,0.5);
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(comboBoxDataModel.name);
            tabPane_container_titlePane.add(labelAttrName,colConstraints);
            gridx++;
        }
        for(TextFieldDataModel model : tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,gridy,0.4);
            JLabel labelAttrName = new JLabel();
            labelAttrName.setText(model.name);
            tabPane_container_titlePane.add(labelAttrName,colConstraints);
            gridx++;
        }
        tabPane_container_titlePane.add(new JLabel("Token Status"),getGridConstraints(gridx,gridy,0.6));
        gridx++;
        tabPane_container_titlePane.add(new JLabel("Remark"),getGridConstraints(gridx,gridy,0.4));

    }
    private void addAnotherTicket(){
        int gridx=0;
        magicLinkCount++;

        // render dropdown list
        for(ComboBoxDataModel comboBoxDataModel : tokenViewModel.comboBoxDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,magicLinkCount,0.5);
            ComboBoxDataModel.ComboBoxOption[] options=comboBoxDataModel.getComboBoxOptions();
            JComboBox comboBox = new JComboBox(options);
            comboBox.setName(comboBoxDataModel.getId());
            comboBox.setEnabled(true);
            tabPane_container_contentPane.add(comboBox,colConstraints);
            JTextField textFieldEncodedValue = new JTextField();
            textFieldEncodedValue.setEditable(false);
            textFieldEncodedValue.setEnabled(true);
            textFieldEncodedValue.setVisible(false);
            textFieldEncodedValue.setText(options[0].getKey().toString(16));
            //updateEncodedValueMap(comboBox.getName(),options[0].getKey(),false);
            //tabPane_container_contentPane.add(textFieldEncodedValue);
            comboBox.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                    ComboBoxDataModel.ComboBoxOption c = (ComboBoxDataModel.ComboBoxOption)e.getItem();
                    textFieldEncodedValue.setText(c.getKey().toString(16).toUpperCase());
                    BigInteger value=new BigInteger(c.getKey().toString(16),16);
                    //updateEncodedValueMap(comboBox.getName(),value,true);
                }
            });
            gridx++;
        }
        for(TextFieldDataModel model : tokenViewModel.textFieldDataModelList){
            GridBagConstraints colConstraints = getGridConstraints(gridx,magicLinkCount,0.4);
            JTextField textFieldEncodedValue = new JTextField();
            textFieldEncodedValue.setName(model.id);
            textFieldEncodedValue.setEditable(false);
            textFieldEncodedValue.setEnabled(true);

//            if(model.id.equals("time")) {
//                JTextField textFieldHiddenValue = new JTextField();
//                textFieldHiddenValue.setVisible(false);
//                textFieldHiddenValue.getDocument().addDocumentListener(new DocumentListener() {
//                    public void changedUpdate(DocumentEvent e) {
//                        warn();
//                    }
//                    public void removeUpdate(DocumentEvent e) {
//                        warn();
//                    }
//                    public void insertUpdate(DocumentEvent e) {
//                        warn();
//                    }
//                    public void warn() {
//                        onDatePickerChange(textFieldEncodedValue,model.getBitshift(),model.getBitmask(),true);
//                    }
//                });
//                JPanel dateTimePickerPane = new JPanel();
//                dateTimePickerPane.setLayout(new GridBagLayout());
//                dateTimePickerTime = new DateTimePicker(textFieldHiddenValue);
//                timeZoneTime = new JComboBox();
//                createDatePicker(dateTimePickerPane, dateTimePickerTime,timeZoneTime);
//                controlsPane.add(dateTimePickerPane, col3Constraints);
//                timeZoneTime.addItemListener(new ItemListener() {
//                    @Override
//                    public void itemStateChanged(ItemEvent e) {
//                        onDatePickerChange(textFieldEncodedValue,model.getBitshift(),model.getBitmask(),true);
//                    }
//                });
//
//                onDatePickerChange(textFieldEncodedValue,model.getBitshift(),model.getBitmask(),false);
//
//            }else{
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
                            textFieldEncodedValue.setVisible(false);
                            //updateEncodedValueMap(textFieldEncodedValue.getName(), encodedValue, true);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Invalid Data Type! Please check the type",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            textFieldInput.setText("");
                            textFieldInput.requestFocusInWindow();
                        }
                    }
                });
            tabPane_container_contentPane.add(textFieldInput,colConstraints);
            //}
            //tabPane_container_contentPane.add(textFieldEncodedValue);
            gridx++;
        }

        tabPane_container_contentPane.add(new JTextField(),getGridConstraints(gridx,magicLinkCount,0.6));
        gridx++;
        tabPane_container_contentPane.add(new JTextField(),getGridConstraints(gridx,magicLinkCount,0.4));
        this.validate();
        this.repaint();
        this.pack();
    }

}
