package Model;

import java.io.File;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FileSend {
    public static void sendFile(JFrame parent, ObjectOutputStream oos, String sender, String receiver) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file để gửi");
        int result = chooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                Message msg = new Message(sender, receiver, "[Đã gửi file: " + file.getName() + "]", "file");
                msg.setFileName(file.getName());
                msg.setFileData(fileBytes);
                oos.writeObject(msg);
                oos.flush();
                JOptionPane.showMessageDialog(parent, "Đã gửi file: " + file.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Gửi file thất bại: " + ex.getMessage());
            }
        }
    }
}
