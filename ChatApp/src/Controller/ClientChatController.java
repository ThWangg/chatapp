package Controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledDocument;

import DAO.MessageDAO;
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
    private MessageDAO msgDao = new MessageDAO();

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

        // nút gửi
        chatView.getBtnSend().addActionListener(e -> sendTextMessage());
        chatView.getBtnSendFile().addActionListener(e -> FileSend.sendFile(chatView, oos, user.getUsername(), getSelectedReceiver()));
        chatView.getBtnSendVoice().addActionListener(e -> VoiceSend.sendVoice(chatView, oos, user.getUsername(), getSelectedReceiver()));
        chatView.getBtnSendImage().addActionListener(e -> ImageSend.sendImage(chatView, oos, user.getUsername(), getSelectedReceiver()));

        // load lại chat (all hay priv)
        chatView.getCboTarget().addActionListener(e -> loadChatHistory());

        // thr nhận dữ liệu từ server
        new Thread(this::listenServer).start();

        // load lịch sử khi mới vô (all)
        loadChatHistory();
    }

    public String getSelectedReceiver() {
        Object selected = chatView.getCboTarget().getSelectedItem();
        System.out.println("selected: " + selected);
        if (selected != null) {
            return selected.toString();
        } else {
            return "All";
        }
    }

    public void sendTextMessage() {
        String content = chatView.getTxtInput().getText().trim();
        if (content.isEmpty())
            return;
        String receiver = getSelectedReceiver();
        String type;
        if (receiver.equalsIgnoreCase("All")) {
            type = "text";
        } else {
            type = "private";
        }
        try {
            Message msg = new Message(user.getUsername(), receiver, content, type);
            oos.writeObject(msg);
            oos.flush();
            chatView.getTxtInput().setText("");
            // lưu msg vô db
            messageController.sendTextMessage(user.getUsername(), receiver, content);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(chatView, "Gửi tin nhắn thất bại");
            ex.printStackTrace();
        }
    }

    public void listenServer() {
        try {
            while (true) {
                Message msg = (Message) ois.readObject();
                String type = msg.getType();

                String selectedReceiver = getSelectedReceiver();

                if ("text".equals(type)) {
                    if ("All".equalsIgnoreCase(selectedReceiver)) {
                        appendText(msg.getSenderId() + ": " + msg.getContent() + "\n");
                    }
                } else if ("private".equals(type)) {
                    String sender = msg.getSenderId();
                    String receiver = msg.getReceiverId();
                    String currentUser = user.getUsername();

                    // hiển thị nếu chọn đúng cửa sổ trò chuyện
                    if ((sender.equals(currentUser) && selectedReceiver.equals(receiver)) || (receiver.equals(currentUser) && selectedReceiver.equals(sender))) {
                        appendText("[Private] " + sender + ": " + msg.getContent() + "\n");
                    }
                } else if ("file".equals(type)) {
                    String sender = msg.getSenderId();
                    String receiver = msg.getReceiverId();
                    String currentUser = user.getUsername();

                    if (!sender.equals(currentUser) && ((receiver.equals(currentUser) && selectedReceiver.equals(sender)) || ("All".equalsIgnoreCase(receiver) && "All".equalsIgnoreCase(selectedReceiver)))) {
                        saveFile(msg);
                    }
                }else if ("voice".equals(type)) {
                    String sender = msg.getSenderId();
                    String receiver = msg.getReceiverId();
                    String currentUser = user.getUsername();

                    // hiển thị nếu chọn đúng cửa sổ trò chuyện
                    if ((sender.equals(currentUser) && selectedReceiver.equals(receiver)) || (receiver.equals(currentUser) && selectedReceiver.equals(sender))) {

                        appendText("[Voice] " + sender + " đã gửi một đoạn ghi âm.(Nhấn vào để nghe)\n");

                        // lưu file tạm
                        java.io.File voiceFile = new java.io.File(System.getProperty("java.io.tmpdir"), msg.getFileName());
                        java.nio.file.Files.write(voiceFile.toPath(), msg.getFileData());

                        chatView.getTxtChat().addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseClicked(java.awt.event.MouseEvent e) {
                                int pos = chatView.getTxtChat().viewToModel2D(e.getPoint());
                                javax.swing.text.Element elem = chatView.getTxtChat().getStyledDocument().getCharacterElement(pos);
                                String line = "";
                                try {
                                    line = chatView.getTxtChat().getText(elem.getStartOffset(), elem.getEndOffset() - elem.getStartOffset());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                                if (line.contains("[Voice]") && line.contains(sender)) {
                                    VoiceSend.playWav(voiceFile);
                                }
                            }
                        });
                    }
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
            JOptionPane.showMessageDialog(chatView, "mất kết nối tới server");
            System.exit(0);
        }
    }

    public void appendText(String text) {
        try {
            StyledDocument doc = chatView.getTxtChat().getStyledDocument();
            doc.insertString(doc.getLength(), text, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveFile(Message msg) {
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

    // load lại lịch sử chat khi đổi người chat ở comboBox
    public void loadChatHistory() {
        String receiver = getSelectedReceiver();
        chatView.getTxtChat().setText("");
        List<Message> history;
        if ("All".equalsIgnoreCase(receiver)) {
            history = msgDao.getGroupChatHistory(user.getUsername());
        } else {
            history = msgDao.getPrivateChatHistory(user.getUsername(), receiver);
        }

        for (Message m : history) {
            if ("private".equals(m.getType())) {
                appendText("[Private] " + m.getSenderId() + ": " + m.getContent() + "\n");
            } else {
                appendText(m.getSenderId() + ": " + m.getContent() + "\n");
            }
        }
    }
}
