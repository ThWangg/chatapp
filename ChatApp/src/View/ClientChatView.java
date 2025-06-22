package View;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import Model.User;

public class ClientChatView extends JFrame {
    private JTextPane txtChat = new JTextPane();
    private JTextField txtInput = new JTextField();
    private JButton btnSend = new JButton("Gửi");
    private JButton btnSendImage = new JButton("Ảnh");
    private JButton btnSendFile = new JButton("File");
    private JButton btnSendVoice = new JButton("Voice");
    private JComboBox<String> cboTarget = new JComboBox<>();
    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JList<String> lstUsers = new JList<>(userListModel);
    private User user;

    public ClientChatView(User user) {
        this.user = user;
        init();
    }

    private void init() {
        setTitle("Chat - " + user.getUsername());
        setSize(780, 440);
        setLayout(null);

        JLabel lblTarget = new JLabel("Gửi đến:");
        lblTarget.setBounds(10, 10, 60, 25);
        cboTarget.setBounds(80, 10, 170, 25);

        JScrollPane spChat = new JScrollPane(txtChat);
        spChat.setBounds(10, 45, 500, 300);
        txtChat.setEditable(false);

        txtInput.setBounds(10, 360, 300, 30);
        btnSend.setBounds(320, 360, 60, 30);
        btnSendImage.setBounds(390, 360, 60, 30);
        btnSendFile.setBounds(460, 360, 60, 30);
        btnSendVoice.setBounds(530, 360, 80, 30);

        JLabel lblUser = new JLabel("User Online:");
        lblUser.setBounds(600, 10, 120, 25);
        JScrollPane spUsers = new JScrollPane(lstUsers);
        spUsers.setBounds(600, 40, 160, 320);

        add(lblTarget); add(cboTarget);
        add(spChat); add(txtInput);
        add(btnSend); add(btnSendImage); add(btnSendFile); add(btnSendVoice);
        add(lblUser); add(spUsers);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Update danh sách user cho ComboBox gửi đến
    public void updateCboTarget(List<String> users) {
        cboTarget.removeAllItems();
        cboTarget.addItem("All");
        for (String u : users) {
            if (!u.equalsIgnoreCase(user.getUsername())) {
                cboTarget.addItem(u);
            }
        }
    }

    public void updateUserOnlineList(List<String> users) {
        userListModel.clear();
        for (String u : users) {
            userListModel.addElement(u);
        }
    }

    // SỬA chỗ này: dùng StyledDocument thay vì append
    public void appendText(String text) {
        try {
            StyledDocument doc = txtChat.getStyledDocument();
            doc.insertString(doc.getLength(), text, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Getter cho controller
    public JTextPane getTxtChat() { return txtChat; }
    public JTextField getTxtInput() { return txtInput; }
    public JButton getBtnSend() { return btnSend; }
    public JButton getBtnSendFile() { return btnSendFile; }
    public JButton getBtnSendVoice() { return btnSendVoice; }
    public JButton getBtnSendImage() { return btnSendImage; }
    public JComboBox<String> getCboTarget() { return cboTarget; }
    public DefaultListModel<String> getUserListModel() { return userListModel; }
    public JList<String> getLstUsers() { return lstUsers; }
    public User getUser() { return user; }
}
