package View;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class VoiceRecordDialog extends JDialog {
    private JButton btnRecord = new JButton("Ghi âm");
    private JButton btnStop = new JButton("Dừng");
    private JButton btnSend = new JButton("Gửi");
    private File recordedFile = null;
    private TargetDataLine line;

    public VoiceRecordDialog(JFrame parent) {
        super(parent, "Ghi âm", true);
        setSize(350, 150);
        setLayout(null);

        btnRecord.setBounds(30, 30, 80, 40);
        btnStop.setBounds(120, 30, 80, 40);
        btnSend.setBounds(210, 30, 80, 40);

        add(btnRecord); add(btnStop); add(btnSend);

        btnStop.setEnabled(false);
        btnSend.setEnabled(false);

        btnRecord.addActionListener(e -> startRecording());
        btnStop.addActionListener(e -> stopRecording());
        btnSend.addActionListener(e -> setVisible(false));
    }

    private void startRecording() {
        try {
            recordedFile = File.createTempFile("voice_", ".mp3");
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Thread t = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(line)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, recordedFile);
                } catch (Exception ex) { ex.printStackTrace(); }
            });
            t.start();
            btnRecord.setEnabled(false);
            btnStop.setEnabled(true);
            btnSend.setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi ghi âm: " + ex.getMessage());
        }
    }

    private void stopRecording() {
        if (line != null) {
            line.stop();
            line.close();
            btnRecord.setEnabled(true);
            btnStop.setEnabled(false);
            btnSend.setEnabled(true);
        }
    }

    public File getRecordedFile() { return recordedFile; }
}
