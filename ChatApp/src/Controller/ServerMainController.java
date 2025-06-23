package Controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import DAO.MessageDAO;
import DAO.UserDAO;
import Main.ClientHandler;
import Model.Message;
import Model.User;
import View.ChatHistoryDialog;
import View.ServerMainView;
import View.UserManageDialog;

public class ServerMainController {
    private ServerMainView view;
    private User adminUser;
    private Map<String, ClientHandler> onlineUsers;

    public ServerMainController(ServerMainView view, User adminUser, Map<String, ClientHandler> onlineUsers) {
        this.view = view;
        this.adminUser = adminUser;
        this.onlineUsers = onlineUsers;

        loadAllUsersFromDB();

        // popup click đúp để quản lý user
        view.getLstAllUsers().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String username = view.getLstAllUsers().getSelectedValue();
                    if (username != null) showUserManageDialog(username);
                }
            }
        });

        view.getBtnBroadcast().addActionListener(e -> broadcastMessage());

        // click chụt phải xem ls chat với user
        view.getLstAllUsers().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
                    String username = view.getLstAllUsers().getSelectedValue();
                    if (username != null) showChatHistoryDialog(username);
                }
            }
        });
    }

    // loai toàn bộ user
    public void loadAllUsersFromDB() {
        DefaultListModel<String> allModel = view.getAllUserListModel();
        allModel.clear();
        for (String username : UserDAO.getAllUsernames()) {
            allModel.addElement(username);
        }
    }

    // upd user onl (server trả về)
    public void updateUserOnlineList(List<String> onlineUsersList) {
        DefaultListModel<String> model = view.getUserListModel();
        model.clear();
        for (String username : onlineUsersList) {
            model.addElement(username);
        }
    }


    public void broadcastMessage() {
        String msg = view.getTxtBroadcast().getText().trim();
        if (!msg.isEmpty()) {
            view.getTxtLog().append("Broadcast: " + msg + "\n");
            view.getTxtBroadcast().setText("");

            Message message = new Message("admin", "all", msg, "text");
            for (ClientHandler handler : onlineUsers.values()) {
                handler.sendMessage(message);
            }
        }
    }

    // quanr lý user (chặn, xóa, sửa)
    public void showUserManageDialog(String username) {
        User user = UserDAO.getUserByUsername(username);
        if (user == null) return;
        UserManageDialog dialog = new UserManageDialog(view, user);
        dialog.setVisible(true);

        dialog.getBtnBlock().addActionListener(e -> {
            UserDAO.blockUser(username);
            JOptionPane.showMessageDialog(view, "Đã chặn " + username);
        });

        dialog.getBtnDelete().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view, "Xác nhận xóa user?", "Xóa user", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserDAO.deleteUser(username);
                loadAllUsersFromDB();
                JOptionPane.showMessageDialog(view, "Đã xóa user " + username);
                dialog.dispose();
            }
        });

        dialog.getBtnUpdate().addActionListener(e -> {
            String newUsername = dialog.getTxtUsername().getText().trim();
            String newPassword = dialog.getTxtPassword().getText().trim();
            UserDAO.updateUser(username, newUsername, newPassword);
            loadAllUsersFromDB();
            JOptionPane.showMessageDialog(view, "Đã cập nhật user");
            dialog.dispose();
        });
    }

    // ls chat với user (popup)
    public void showChatHistoryDialog(String targetUsername) {
        List<Message> history = MessageDAO.getGroupChatHistory(adminUser.getUsername());
        ChatHistoryDialog dialog = new ChatHistoryDialog(view, adminUser.getUsername(), targetUsername, history);
        dialog.setVisible(true);

        dialog.getBtnDeleteHistory().addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(view, "Xóa toàn bộ lịch sử chat với " + targetUsername + "?", "Xóa lịch sử", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                MessageDAO.deleteChatHistory(adminUser.getUsername(), targetUsername);
                dialog.dispose();
                JOptionPane.showMessageDialog(view, "Đã xóa lịch sử chat");
            }
        });
    }
}
