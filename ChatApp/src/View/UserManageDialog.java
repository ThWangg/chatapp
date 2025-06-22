package View;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import Model.User;

public class UserManageDialog extends JDialog {
    private JTextField txtUsername = new JTextField();
    private JTextField txtPassword = new JTextField();
    private JButton btnBlock = new JButton("Chặn");
    private JButton btnDelete = new JButton("Xóa");
    private JButton btnUpdate = new JButton("Sửa");

    public UserManageDialog(JFrame parent, User user) {
        super(parent, "Quản lý user: " + user.getUsername(), true);
        setLayout(null);
        setSize(350, 200);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(20, 20, 80, 25);
        txtUsername.setBounds(110, 20, 200, 25);
        txtUsername.setText(user.getUsername());

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(20, 60, 80, 25);
        txtPassword.setBounds(110, 60, 200, 25);
        txtPassword.setText(user.getPassword());

        btnBlock.setBounds(20, 110, 80, 30);
        btnDelete.setBounds(120, 110, 80, 30);
        btnUpdate.setBounds(220, 110, 80, 30);

        add(lblUsername); add(txtUsername);
        add(lblPassword); add(txtPassword);
        add(btnBlock); add(btnDelete); add(btnUpdate);

        setLocationRelativeTo(parent);
    }

    public JTextField getTxtUsername() { return txtUsername; }
    public JTextField getTxtPassword() { return txtPassword; }
    public JButton getBtnBlock() { return btnBlock; }
    public JButton getBtnDelete() { return btnDelete; }
    public JButton getBtnUpdate() { return btnUpdate; }
}
