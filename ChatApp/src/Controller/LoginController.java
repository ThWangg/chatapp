package Controller;

import javax.swing.JOptionPane;

import DAO.UserDAO;
import Main.ChatServer;
import Model.User;
import View.ClientChatView;
import View.LoginView;
import View.ServerMainView;

public class LoginController {
    private LoginView loginView;

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        loginView.getBtnLogin().addActionListener(e -> authenticate());
    }

    private void authenticate() {
        String username = loginView.getUsername();
        String password = loginView.getPassword();

        User user = UserDAO.login(username, password);
        if (user != null) {
            loginView.dispose();
            if ("admin".equalsIgnoreCase(user.getRole())) {
                ServerMainView view = new ServerMainView(user);
                new ServerMainController(view, user, ChatServer.onlineUsers);
                view.setVisible(true);
            } else {
                ClientChatView view = new ClientChatView(user);
                new ClientChatController(view, user);
                view.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(loginView, "Sai tài khoản hoặc mật khẩu!");
        }
    }
}
