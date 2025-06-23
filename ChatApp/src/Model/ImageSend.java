package Model;

import java.io.File;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

public class ImageSend {
    public static void sendImage(JFrame parent, ObjectOutputStream oos, String sender, String receiver) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn ảnh để gửi");
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                byte[] imgBytes = java.nio.file.Files.readAllBytes(file.toPath());
                Message msg = new Message(sender, receiver, "[Đã gửi ảnh: " + file.getName() + "]", "image");
                msg.setFileName(file.getName());
                msg.setFileData(imgBytes);
                oos.writeObject(msg);
                oos.flush();
                JOptionPane.showMessageDialog(parent, "Đã gửi ảnh: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Gửi ảnh thất bại: " + ex.getMessage());
            }
        }
    }
    public static void showImageInPane(JTextPane pane, Message msg) {
        try {
            javax.swing.text.StyledDocument doc = pane.getStyledDocument();
            javax.swing.text.Style style = pane.addStyle("imgStyle", null);
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(msg.getFileData());
            javax.swing.text.StyleConstants.setIcon(style, icon);
            doc.insertString(doc.getLength(), "ignored text", style);
            doc.insertString(doc.getLength(), "\n", null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}