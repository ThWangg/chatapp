package Controller;

import DAO.UserDAO;
import Model.User;
import View.UserManageDialog;

public class UserManageController {
    private UserManageDialog dialog;
    private User user;

    public UserManageController(UserManageDialog dialog, User user) {
        this.dialog = dialog;
        this.user = user;

        dialog.getBtnBlock().addActionListener(e -> {
            UserDAO.blockUser(user.getUsername());
            dialog.dispose();
        });

        dialog.getBtnDelete().addActionListener(e -> {
            UserDAO.deleteUser(user.getUsername());
            dialog.dispose();
        });

        dialog.getBtnUpdate().addActionListener(e -> {
            String newUsername = dialog.getTxtUsername().getText().trim();
            String newPassword = dialog.getTxtPassword().getText().trim();
            UserDAO.updateUser(user.getUsername(), newUsername, newPassword);
            dialog.dispose();
        });
    }
}
