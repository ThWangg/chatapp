package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.DBConnection;
import Model.Message;

public class MessageDAO {
    // Lấy lịch sử chat giữa 2 user (bao gồm cả group "all")
    public static List<Message> getChatHistory(String user1, String user2) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE " +
                "((sender=? AND receiver=?) OR (sender=? AND receiver=?)) " +
                "OR (receiver='all' AND (sender=? OR sender=?)) " +
                "ORDER BY timestamp ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            ps.setString(5, user1);
            ps.setString(6, user2);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message msg = new Message(
                    rs.getString("sender"),
                    rs.getString("receiver"),
                    rs.getString("content"),
                    rs.getString("type")
                );
                msg.setFileName(rs.getString("filename"));
                msg.setFileData(rs.getBytes("filedata"));
                msg.setTimestamp(rs.getTimestamp("timestamp"));
                list.add(msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Lưu tin nhắn (bất kỳ loại nào) vào DB
    public static boolean saveMessage(Message msg) {
        String sql = "INSERT INTO messages(sender, receiver, type, content, timestamp) VALUES (?,?,?,?,NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, msg.getSenderId());
            ps.setString(2, msg.getReceiverId());
            ps.setString(3, msg.getType());
            ps.setString(4, msg.getContent());
//            ps.setString(5, msg.getFileName());
//            ps.setBytes(6, msg.getFileData());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Xóa toàn bộ lịch sử chat giữa 2 user
    public static boolean deleteHistory(String user1, String user2) {
        String sql = "DELETE FROM messages WHERE " +
                "((sender=? AND receiver=?) OR (sender=? AND receiver=?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm tin nhắn theo từ khóa, phân biệt theo 2 user
    public static List<Message> searchMessages(String user1, String user2, String keyword) {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE " +
                "((sender=? AND receiver=?) OR (sender=? AND receiver=?)) AND content LIKE ? " +
                "ORDER BY timestamp ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            ps.setString(5, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message msg = new Message(
                    rs.getString("sender"),
                    rs.getString("receiver"),
                    rs.getString("content"),
                    rs.getString("type")
                );
                msg.setFileName(rs.getString("filename"));
                msg.setFileData(rs.getBytes("filedata"));
                msg.setTimestamp(rs.getTimestamp("timestamp"));
                list.add(msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    public static boolean deleteChatHistory(String user1, String user2) {
        String sql = "DELETE FROM messages WHERE " +
                     "((sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
