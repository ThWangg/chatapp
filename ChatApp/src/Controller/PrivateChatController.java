package Controller;

import java.io.ObjectOutputStream;

import javax.swing.SwingUtilities;

import Model.Message;
import Model.User;
import View.PrivateChatDialog;

public class PrivateChatController {
    private PrivateChatDialog dialog;
    private User user;
    private String targetUser;
    private ObjectOutputStream oos;
    private MessageController messageController = new MessageController();

    public PrivateChatController(PrivateChatDialog dialog, User user, String targetUser, ObjectOutputStream oos) {
        this.dialog = dialog;
        this.user = user;
        this.targetUser = targetUser;
        this.oos = oos;

        dialog.getBtnSend().addActionListener(e -> sendPrivateMessage());

        // Load lịch sử chat khi mở
        loadPrivateHistory();
    }

    public void sendPrivateMessage() {
        String content = dialog.getTxtInput().getText().trim();
        if (!content.isEmpty()) {
            try {
                Message msg = new Message(user.getUsername(), targetUser, content, "private");
                oos.writeObject(msg);
                oos.flush();
                dialog.getTxtChat().append("[Private] " + content + "\n");
                dialog.getTxtInput().setText("");
                // Lưu DB
                messageController.sendTextMessage(user.getUsername(), targetUser, content);
            } catch (Exception ex) {
                dialog.getTxtChat().append("Lỗi gửi tin nhắn riêng!\n");
            }
        }
    }

    public void loadPrivateHistory() {
        dialog.getTxtChat().setText("");
        java.util.List<Message> history = messageController.getPrivChatHistory(user.getUsername(), targetUser);
        for (Message m : history) {
            dialog.getTxtChat().append(m.getSenderId() + ": " + m.getContent() + "\n");
        }
    }

}
