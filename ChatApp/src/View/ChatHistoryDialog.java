package View;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import Model.Message;

public class ChatHistoryDialog extends JDialog {
    private JTextArea txtHistory = new JTextArea();
    private JButton btnDeleteHistory = new JButton("Xóa lịch sử");

    public ChatHistoryDialog(JFrame parent, String admin, String user, List<Message> history) {
        super(parent, "Lịch sử chat: " + admin + " ↔ " + user, true);
        setSize(400, 350);
        setLayout(null);

        JScrollPane sp = new JScrollPane(txtHistory);
        sp.setBounds(10, 10, 370, 250);
        txtHistory.setEditable(false);

        btnDeleteHistory.setBounds(130, 270, 120, 30);

        StringBuilder sb = new StringBuilder();
        for (Message msg : history) {
            sb.append(msg.getSenderId()).append(": ").append(msg.getContent()).append("\n");
        }
        txtHistory.setText(sb.toString());

        add(sp); add(btnDeleteHistory);
        setLocationRelativeTo(parent);
    }

    public JButton getBtnDeleteHistory() { return btnDeleteHistory; }
}
