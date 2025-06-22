package Controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import Model.FileSend;
import Model.ImageSend;
import Model.Message;
import Model.User;
import Model.VoiceSend;
import View.ClientChatView;

public class ClientChatController {
    private ClientChatView chatView;
    private User user;
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private MessageController messageController = new MessageController();

    public ClientChatController(ClientChatView chatView, User user) {
        this.chatView = chatView;
        this.user = user;

        try {
            socket = new Socket("127.0.0.1", 8000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(user.getUsername());
            oos.flush();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(chatView, "Không kết nối được server!");
            System.exit(1);
        }
        // Gửi tin nhắn
        chatView.getBtnSend().addActionListener(e -> sendTextMessage());
        chatView.getBtnSendFile().addActionListener(e -> FileSend.sendFile(chatView, oos, user.getUsername(), getSelectedReceiver()));
        chatView.getBtnSendVoice().addActionListener(e -> VoiceSend.sendVoice(chatView, oos, user.getUsername(), getSelectedReceiver()));
        chatView.getBtnSendImage().addActionListener(e -> ImageSend.sendImage(chatView, oos, user.getUsername(), getSelectedReceiver()));

        // Khi chọn đối tượng chat thì load lại lịch sử
        chatView.getCboTarget().addActionListener(e -> loadChatHistory());

        // Thread nhận dữ liệu từ server
        new Thread(this::listenServer).start();

        // Load lịch sử mặc định với "all"
        loadChatHistory();
    }

    private String getSelectedReceiver() {
        Object selected = chatView.getCboTarget().getSelectedItem();
        return (selected != null) ? selected.toString() : "All";
    }

    private void sendTextMessage() {
        String content = chatView.getTxtInput().getText().trim();
        if (content.isEmpty()) return;
        String receiver = getSelectedReceiver();
        String type = receiver.equalsIgnoreCase("All") ? "text" : "private";
        try {
            Message msg = new Message(user.getUsername(), receiver, content, type);
            oos.writeObject(msg);
            oos.flush();
            chatView.getTxtInput().setText("");
            // Lưu vào DB qua MessageController
            messageController.sendTextMessage(user.getUsername(), receiver, content);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(chatView, "Gửi tin nhắn thất bại!");
        }
    }

    private void listenServer() {
        try {
            while (true) {
                Message msg = (Message) ois.readObject();
                String type = msg.getType();
                if ("text".equals(type)) {
                    appendText(msg.getSenderId() + ": " + msg.getContent() + "\n");
                } else if ("private".equals(type)) {
                    appendText("[Private] " + msg.getSenderId() + ": " + msg.getContent() + "\n");
                } else if ("file".equals(type)) {
                    saveFile(msg);
                } else if ("voice".equals(type)) {
                    saveVoice(msg);
                } else if ("userlist".equals(type)) {
                    SwingUtilities.invokeLater(() -> {
                        chatView.getUserListModel().clear();
                        java.util.List<String> users = msg.getUserList();
                        for (String u : users) {
                            chatView.getUserListModel().addElement(u);
                        }
                        chatView.updateCboTarget(users);
                    });
                } else if ("image".equals(type)) {
                    SwingUtilities.invokeLater(() -> {
                        ImageSend.showImageInPane(chatView.getTxtChat(), msg);
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(chatView, "Mất kết nối tới server!");
            System.exit(0);
        }
    }

    private void appendText(String text) {
        try {
            StyledDocument doc = chatView.getTxtChat().getStyledDocument();
            doc.insertString(doc.getLength(), text, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void saveFile(Message msg) {
        try {
            javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
            fc.setSelectedFile(new java.io.File(msg.getFileName()));
            int result = fc.showSaveDialog(chatView);
            if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File outFile = fc.getSelectedFile();
                java.nio.file.Files.write(outFile.toPath(), msg.getFileData());
                appendText("[Đã nhận file từ " + msg.getSenderId() + ": " + outFile.getName() + "]\n");
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(chatView, "Lưu file thất bại: " + ex.getMessage());
        }
    }
    private void saveVoice(Message msg) {
        // (Tương tự file, nếu có muốn phát lại thì code thêm)
    }

    // Load lại lịch sử chat mỗi khi đổi đối tượng chat ở comboBox
    private void loadChatHistory() {
        String receiver = getSelectedReceiver();
        chatView.getTxtChat().setText("");
        java.util.List<Message> history = messageController.getChatHistory(user.getUsername(), receiver);
        for (Message m : history) {
            appendText(m.getSenderId() + ": " + m.getContent() + "\n");
        }
    }
}
