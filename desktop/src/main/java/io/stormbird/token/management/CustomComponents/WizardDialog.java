package io.stormbird.token.management.CustomComponents;

import io.stormbird.token.management.MagicLinkTool;
import io.stormbird.token.management.Model.ComboBoxSimpleItem;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WizardDialog extends JDialog
        implements ActionListener,
        PropertyChangeListener {
    private JTextField textFieldContractAddress;
    private JComboBox comboBoxNetworkID;
    private MagicLinkTool parent;
    private JOptionPane optionPane;

    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";

    /** Creates the reusable dialog. */
    public WizardDialog(MagicLinkTool parent) {
        super(parent, true);
        this.parent = parent;

        setTitle("wizard");

        textFieldContractAddress = new JTextField(10);
        comboBoxNetworkID=new JComboBox();
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("mainnet", "1"));
        comboBoxNetworkID.addItem(new ComboBoxSimpleItem("Ropsten", "3"));
        //Create an array of the text and components to be displayed.
        String msgString1 = "Deploy a new contract";
        String msgString2 = "Contract Address:";
        Object[] array = {msgString1, msgString2, textFieldContractAddress,comboBoxNetworkID};

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(
                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                textFieldContractAddress.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        textFieldContractAddress.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
                JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                ComboBoxSimpleItem selectedNetworkItem = (ComboBoxSimpleItem) comboBoxNetworkID.getSelectedItem();
                String networkid = selectedNetworkItem.getValue();
                String contractAddress = textFieldContractAddress.getText();
                // todo validation
                if (contractAddress==null||contractAddress.equals("")) {
                    textFieldContractAddress.selectAll();
                    JOptionPane.showMessageDialog(
                            WizardDialog.this,
                            "cannot be empty!",
                            "Warn",
                            JOptionPane.ERROR_MESSAGE);
                    contractAddress = null;
                    textFieldContractAddress.requestFocusInWindow();
                } else {
                    parent.updateContractAddress(networkid,contractAddress);
                    clearAndHide();
                }
            } else {
                clearAndHide();
            }
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        textFieldContractAddress.setText(null);
        setVisible(false);
    }
}
