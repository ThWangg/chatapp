package View;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PrivateChatDialog extends JDialog {
    private JTextArea txtChat = new JTextArea();
    private JTextField txtInput = new JTextField();
    private JButton btnSend = new JButton("Gửi");

    public PrivateChatDialog(JFrame parent, String targetUser) {
        super(parent, " đang chat với: " + targetUser, false);
        setSize(420, 340);
        setLayout(null);

        JScrollPane scrollChat = new JScrollPane(txtChat);
        scrollChat.setBounds(10, 10, 380, 200);
        txtChat.setEditable(false);

        txtInput.setBounds(10, 220, 260, 30);
        btnSend.setBounds(280, 220, 80, 30);

        add(scrollChat);
        add(txtInput);
        add(btnSend);

        setLocationRelativeTo(parent);
    }

    public JTextArea getTxtChat() { return txtChat; }
    public JTextField getTxtInput() { return txtInput; }
    public JButton getBtnSend() { return btnSend; }
}
