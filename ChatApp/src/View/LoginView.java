package View;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginView extends JFrame {
    private JTextField txtUsername = new JTextField();
    private JPasswordField txtPassword = new JPasswordField();
    private JButton btnLogin = new JButton("Đăng nhập");

    public LoginView() {
        setTitle("Đăng nhập hệ thống");
        setSize(350, 200);
        setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(30, 30, 80, 30);
        txtUsername.setBounds(120, 30, 180, 30);
        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(30, 70, 80, 30);
        txtPassword.setBounds(120, 70, 180, 30);

        btnLogin.setBounds(120, 120, 100, 30);

        add(lblUser); add(txtUsername);
        add(lblPass); add(txtPassword);
        add(btnLogin);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Getter cho controller
    public JButton getBtnLogin() { return btnLogin; }
    public String getUsername() { return txtUsername.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
}
