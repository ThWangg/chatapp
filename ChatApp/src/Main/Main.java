package Main;

import Controller.LoginController;
import View.LoginView;

public class Main {
    public static void main(String[] args) {
        // Khởi tạo và show giao diện đăng nhập
        javax.swing.SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
    }
}
