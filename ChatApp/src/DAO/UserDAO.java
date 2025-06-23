package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Model.DBConnection;
import Model.User;

public class UserDAO {
	//đăng nhập
	public static User login(String username, String password) {
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(
	            "SELECT * FROM users WHERE username = ? AND password = ?")) {
	        ps.setString(1, username);
	        ps.setString(2, password);
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	            int id = rs.getInt("id");
	            String role = rs.getString("role");
	            // lấy thêm các trường khác nếu cần
	            return new User(id, username, password, role);
	        }
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	    return null;
	}


    public static List<String> getAllUsernames() {
        List<String> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT username FROM users")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(rs.getString("username"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return users;
    }

    // user chi tiết
    public static User getUserByUsername(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=?")) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // block user
    public static boolean blockUser(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE users SET status=0 WHERE username=?")) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    // xoá user
    public static boolean deleteUser(String username) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE username=?")) {
            ps.setString(1, username);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }

    // sửa thông tin user
    public static boolean updateUser(String oldUsername, String newUsername, String newPassword) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET username=?, password=? WHERE username=?")) {
            ps.setString(1, newUsername);
            ps.setString(2, newPassword);
            ps.setString(3, oldUsername);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) { ex.printStackTrace(); }
        return false;
    }
}
