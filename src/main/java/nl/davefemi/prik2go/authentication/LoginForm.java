package nl.davefemi.prik2go.authentication;

import nl.davefemi.prik2go.dto.UserDTO;
import javax.swing.*;
import java.util.concurrent.CancellationException;

public class LoginForm extends JPanel {
    private static final JLabel EMAIL = new JLabel("E-mail");
    private final JLabel PASSWORD = new JLabel("Password");
    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private LoginForm panel = this;

    public LoginForm(){
        super();
        buildPanel();
    }

    private void buildPanel(){
        panel.setSize(400,400);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(EMAIL);
        panel.add(Box.createHorizontalStrut(5));
        emailField.setFocusable(true);
        panel.add(emailField);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(PASSWORD);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(passwordField);
    }

    public UserDTO getUserLogin(){
        int result = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION){
            if (!(emailField.getText().isBlank() || new String(passwordField.getPassword()).isBlank())) {
                UserDTO dto = new UserDTO();
                dto.setEmail(emailField.getText());
                dto.setPassword(new String(passwordField.getPassword()));
                emailField.setText("");
                passwordField.setText("");
                return dto;
            }
            else{
                throw new IllegalArgumentException("Fields cannot be blank");
            }
        }
        throw new CancellationException("User cancelled");
    }
}
