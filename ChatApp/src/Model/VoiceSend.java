package Model;

import java.io.File;
import java.io.ObjectOutputStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class VoiceSend {
    public static void sendVoice(JFrame parent, ObjectOutputStream oos, String sender, String receiver) {
        try {
            File voiceFile = recordVoiceDialog(parent);
            if (voiceFile == null || !voiceFile.exists()) return;
            byte[] voiceBytes = java.nio.file.Files.readAllBytes(voiceFile.toPath());
            Message msg = new Message(sender, receiver, "[Đã gửi voice]", "voice");
            msg.setFileName(voiceFile.getName());
            msg.setFileData(voiceBytes);
            oos.writeObject(msg);
            oos.flush();
            JOptionPane.showMessageDialog(parent, "Đã gửi voice!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Gửi voice thất bại: " + ex.getMessage());
        }
    }

    public static File recordVoiceDialog(JFrame parent) {
        try {
            File tempFile = File.createTempFile("voice_", ".wav");
            AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            JOptionPane.showMessageDialog(parent, "Nhấn OK để bắt đầu ghi âm.");
            Thread t = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(line)) {
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, tempFile);
                } catch (Exception ex) { ex.printStackTrace(); }
            });
            t.start();
            JOptionPane.showMessageDialog(parent, "Nhấn OK để kết thúc ghi âm.");
            line.stop();
            line.close();
            Thread.sleep(400);
            return tempFile;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Lỗi ghi âm: " + ex.getMessage());
            return null;
        }
    }

    public static void playWav(File file) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            JOptionPane.showMessageDialog(null, "Đang phát voice. Nhấn OK để dừng.");
            clip.stop();
            clip.close();
            ais.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Không phát được voice: " + ex.getMessage());
        }
    }

}