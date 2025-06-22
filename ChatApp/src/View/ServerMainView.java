package View;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import Model.User;

public class ServerMainView extends JFrame {
    private JTextArea txtLog = new JTextArea();
    private JButton btnBroadcast = new JButton("Broadcast");
    private JTextField txtBroadcast = new JTextField();

    private DefaultListModel<String> userListModel = new DefaultListModel<>();        // User online
    private DefaultListModel<String> allUserListModel = new DefaultListModel<>();     // Tất cả user trong hệ thống

    private JList<String> lstUsers = new JList<>(userListModel);           // Online
    private JList<String> lstAllUsers = new JList<>(allUserListModel);     // Tất cả
    private JComboBox<String> cboTarget = new JComboBox<>();               // Chọn đối tượng chat

    public ServerMainView(User admin) {
        setTitle("QUẢN TRỊ SERVER - " + admin.getUsername());
        setSize(950, 420);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblAllUser = new JLabel("Tất cả user:");
        lblAllUser.setBounds(20, 20, 100, 25);

        JScrollPane spAllUsers = new JScrollPane(lstAllUsers);
        spAllUsers.setBounds(20, 50, 150, 300);

        JLabel lblUser = new JLabel("User Online:");
        lblUser.setBounds(190, 20, 100, 25);

        JScrollPane spUsers = new JScrollPane(lstUsers);
        spUsers.setBounds(190, 50, 150, 300);

        JLabel lblTarget = new JLabel("Gửi đến:");
        lblTarget.setBounds(360, 20, 60, 25);
        cboTarget.setBounds(430, 20, 120, 25);

        JLabel lblLog = new JLabel("Server Log:");
        lblLog.setBounds(570, 20, 100, 25);

        JScrollPane spLog = new JScrollPane(txtLog);
        spLog.setBounds(570, 50, 350, 200);
        txtLog.setEditable(false);

        txtBroadcast.setBounds(570, 270, 260, 30);
        btnBroadcast.setBounds(840, 270, 80, 30);

        add(lblAllUser); add(spAllUsers);
        add(lblUser); add(spUsers);
        add(lblTarget); add(cboTarget);
        add(lblLog); add(spLog);
        add(txtBroadcast); add(btnBroadcast);

        setLocationRelativeTo(null);
    }

    // Getter cho Controller
    public JButton getBtnBroadcast() { return btnBroadcast; }
    public JTextField getTxtBroadcast() { return txtBroadcast; }
    public JTextArea getTxtLog() { return txtLog; }
    public DefaultListModel<String> getUserListModel() { return userListModel; }      // Online
    public DefaultListModel<String> getAllUserListModel() { return allUserListModel; }// Tất cả
    public JList<String> getLstUsers() { return lstUsers; }
    public JList<String> getLstAllUsers() { return lstAllUsers; }
    public JComboBox<String> getCboTarget() { return cboTarget; }
}
