package View;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class FileChooserDialog extends JFileChooser {
    public FileChooserDialog() {
        super();
        setDialogTitle("Chọn file để gửi");
        setFileSelectionMode(FILES_ONLY);
    }

    public File chooseFile(JFrame parent) {
        int result = showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            return getSelectedFile();
        }
        return null;
    }
}
