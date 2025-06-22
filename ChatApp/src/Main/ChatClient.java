package Main;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient extends JFrame {
    private JTextArea txtChat = new JTextArea();
    private JTextField txtInput = new JTextField();
    private JButton btnSend = new JButton("Gửi");
    private JButton btnFile = new JButton("File");
    private JButton btnVoice = new JButton("Voice");
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> lstUsers = new JList<>(listModel);

    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private String username;

    public ChatClient() {
        setTitle("Chat Client");
        setSize(700, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JScrollPane spChat = new JScrollPane(txtChat);
        spChat.setBounds(10, 10, 450, 300);
        txtChat.setEditable(false);
        txtInput.setBounds(10, 320, 300, 30);
        btnSend.setBounds(320, 320, 60, 30);
        btnFile.setBounds(390, 320, 60, 30);
        btnVoice.setBounds(460, 320, 80, 30);

        JScrollPane spUsers = new JScrollPane(lstUsers);
        spUsers.setBounds(480, 10, 200, 340);

        add(spChat); add(txtInput); add(btnSend); add(btnFile); add(btnVoice); add(spUsers);
        // --- Kết nối server ---
        String serverIP = JOptionPane.showInputDialog(this, "Nhập IP server:", "127.0.0.1");
        username = JOptionPane.showInputDialog(this, "Nhập tên đăng nhập:");
        try {
            Socket socket = new Socket(serverIP, 5000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(username); oos.flush();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không kết nối được server!");
            System.exit(0);
        }
        btnSend.addActionListener(e -> sendText());
        txtInput.addActionListener(e -> sendText());

        btnFile.addActionListener(e -> sendFile());
        btnVoice.addActionListener(e -> sendVoice());

        lstUsers.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String toUser = lstUsers.getSelectedValue();
                    if (toUser != null && !toUser.equals(username)) {
                        String content = JOptionPane.showInputDialog("Gửi đến " + toUser + ":");
                        if (content != null && !content.trim().isEmpty())
                            sendPrivate(toUser, content);
                    }
                }
            }
        });

        // --- Thread nhận ---
        new Thread(() -> listenServer()).start();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void sendText() {
        String content = txtInput.getText().trim();
        if (content.isEmpty()) return;
        try {
            Message msg = new Message(username, "all", content, "text");
            oos.writeObject(msg); oos.flush();
            txtInput.setText("");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gửi thất bại!"); }
    }

    private void sendPrivate(String toUser, String content) {
        try {
            Message msg = new Message(username, toUser, content, "private");
            oos.writeObject(msg); oos.flush();
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gửi riêng thất bại!"); }
    }

    private void sendFile() {
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                byte[] data = java.nio.file.Files.readAllBytes(file.toPath());
                Message msg = new Message(username, "all", "[Đã gửi file: " + file.getName() + "]", "file");
                msg.fileName = file.getName();
                msg.fileData = data;
                oos.writeObject(msg); oos.flush();
                txtChat.append("Bạn đã gửi file: " + file.getName() + "\n");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gửi file thất bại!"); }
        }
    }

    private void sendVoice() {
        File wav = recordVoice();
        if (wav != null) {
            try {
                byte[] data = java.nio.file.Files.readAllBytes(wav.toPath());
                Message msg = new Message(username, "all", "[Đã gửi voice]", "voice");
                msg.fileName = wav.getName();
                msg.fileData = data;
                oos.writeObject(msg); oos.flush();
                txtChat.append("Bạn đã gửi voice.\n");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Gửi voice thất bại!"); }
        }
    }

    private File recordVoice() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn nơi lưu file voice (sẽ bị ghi đè)");
        fc.setSelectedFile(new File("voice.wav"));
        int r = fc.showSaveDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return null;
        File file = fc.getSelectedFile();
        try {
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format); line.start();
            JOptionPane.showMessageDialog(this, "Nhấn OK để bắt đầu ghi.");
            Thread t = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(line)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
                } catch (Exception ex) { ex.printStackTrace(); }
            });
            t.start();
            JOptionPane.showMessageDialog(this, "Nhấn OK để kết thúc ghi âm!");
            line.stop(); line.close();
            Thread.sleep(500); // chờ chắc ghi xong
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi ghi âm: " + ex.getMessage()); return null; }
        return file;
    }

    private void listenServer() {
        try {
            while (true) {
                Message msg = (Message) ois.readObject();
                if ("text".equals(msg.type)) {
                    txtChat.append(msg.senderId + ": " + msg.content + "\n");
                } else if ("private".equals(msg.type)) {
                    txtChat.append("[Riêng] " + msg.senderId + ": " + msg.content + "\n");
                } else if ("file".equals(msg.type)) {
                    int r = JOptionPane.showConfirmDialog(this, "Nhận file " + msg.fileName + " từ " + msg.senderId + ". Lưu?");
                    if (r == JOptionPane.YES_OPTION) {
                        JFileChooser fc = new JFileChooser();
                        fc.setSelectedFile(new File(msg.fileName));
                        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                            File out = fc.getSelectedFile();
                            java.nio.file.Files.write(out.toPath(), msg.fileData);
                            txtChat.append("[Đã lưu file " + out.getName() + "]\n");
                        }
                    }
                } else if ("voice".equals(msg.type)) {
                    int r = JOptionPane.showConfirmDialog(this, "Nhận voice " + msg.fileName + " từ " + msg.senderId + ". Lưu và phát?");
                    if (r == JOptionPane.YES_OPTION) {
                        JFileChooser fc = new JFileChooser();
                        fc.setSelectedFile(new File(msg.fileName));
                        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                            File out = fc.getSelectedFile();
                            java.nio.file.Files.write(out.toPath(), msg.fileData);
                            txtChat.append("[Đã lưu voice " + out.getName() + "]\n");
                            playWav(out);
                        }
                    }
                } else if ("userlist".equals(msg.type)) {
                    listModel.clear();
                    for (String u : msg.userList) listModel.addElement(u);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Mất kết nối tới server!");
            System.exit(0);
        }
    }

    private void playWav(File file) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            JOptionPane.showMessageDialog(this, "Đang phát file voice, đóng để dừng.");
            clip.stop(); clip.close(); ais.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không phát được file: " + ex.getMessage());
        }
    }
    
    static class Message implements Serializable {
        String senderId, receiverId, content, type, fileName;
        byte[] fileData;
        List<String> userList;
        Message(String senderId, String receiverId, String content, String type) {
            this.senderId = senderId; this.receiverId = receiverId;
            this.content = content; this.type = type;
        }
    }

    public static void main(String[] args) {
        new ChatClient();
    }
}
