package Vue;

import Modele.ConnectionMode;

import javax.swing.*;
import java.awt.event.*;

public class ConnectionTypeDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    private ButtonGroup buttonGroup;
    private JRadioButton radioBtnNoSecureConnection;
    private JRadioButton radioBtnSecureConnection;

    private int selectedConnectionMode;

    public ConnectionTypeDialog() {
        setContentPane(contentPane);
        setModal(true);
        setSize(500, 250);
        getRootPane().setDefaultButton(buttonOK);

        buttonGroup = new ButtonGroup();
        buttonGroup.add(radioBtnNoSecureConnection);
        buttonGroup.add(radioBtnSecureConnection);

        selectedConnectionMode = ConnectionMode.NO_SECURE;

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        if(radioBtnSecureConnection.isSelected()) {
            System.out.println("Secure connection choose");
            selectedConnectionMode = ConnectionMode.SECURE;
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public int getSelectedConnectionMode() { return selectedConnectionMode; }

    public static void main(String[] args) {
        ConnectionTypeDialog dialog = new ConnectionTypeDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
