package nl.davefemi.prik2go.gui.factory.components.authentication;

import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.gui.factory.components.util.BerichtDialoog;
import nl.davefemi.prik2go.gui.factory.components.util.SwingBringToFront;
import nl.davefemi.prik2go.gui.factory.components.util.SpringUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.logging.Logger;

import static java.awt.event.KeyEvent.VK_ENTER;

public class LoginForm extends JPanel {
    private static Logger log = Logger.getLogger(LoginForm.class.getName());
    private static final JLabel EMAIL = new JLabel("E-mail", JLabel.TRAILING);
    private final JLabel PASSWORD = new JLabel("Password", JLabel.TRAILING);
    private JTextField emailField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    private JLabel googleField = new JLabel("Sign in with Google");
    private final LoginForm panel = this;
    private JOptionPane pane;
    private final String[] options = {"OK", "Cancel"};
    private JDialog dialog;

    public LoginForm() {
        super();
        buildPanel();
    }

    private void buildPanel() {
        panel.setLayout(new GridLayout(2, 0));
        panel.putClientProperty("googleAuth", false);
        getFields();
    }

    private void getFields(){
        JPanel panel = new JPanel();
        panel.setLayout(new SpringLayout());
        panel.add(EMAIL);
        EMAIL.setLabelFor(emailField);
        setAction(emailField);
        panel.add(emailField);
        panel.add(PASSWORD);
        PASSWORD.setLabelFor(passwordField);
        setAction(passwordField);
        panel.add(passwordField);
        SpringUtilities.makeCompactGrid(panel,
                1, 4,
                5, 5,
                5, 5);
        this.panel.add(panel, BorderLayout.CENTER);
        addGoogleField();
    }

    private void addGoogleField(){
        googleField.setHorizontalAlignment(SwingConstants.CENTER);
        googleField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    if (AuthController.loginWithGoogle()) {
                        panel.firePropertyChange("googleAuth", false, true);
                        log.info("Auth gelukt");
                    }
                } catch (ApplicatieException ex) {
                    SwingBringToFront.bringPanelToFront(panel);
                    BerichtDialoog.getErrorDialoog(panel, ex.getMessage());
                }
            }
        });
        this.panel.add(googleField);
    }

    public UserDTO getUserLogin(UUID user) {
        getForm();
        if (!(pane.getValue() instanceof String))
            throw new CancellationException("User closed window");
        String result = (String) pane.getValue();
        if (result != null && result.equals("OK")) {
            if (!(emailField.getText().isBlank() || new String(passwordField.getPassword()).isBlank())) {
                UserDTO dto = new UserDTO();
                dto.setEmail(emailField.getText());
                dto.setPassword(new String(passwordField.getPassword()));
                emailField.setText("");
                passwordField.setText("");
                return dto;
            } else {
                throw new IllegalArgumentException("Fields cannot be blank");
            }
        }
        throw new CancellationException("You must enter a password");
    }


    private void getForm() {
        pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, options);
        dialog = pane.createDialog("Login");
        dialog.setSize(600, 165);
        dialog.setVisible(true);
    }

    private void setAction(JTextField field){
        field.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(VK_ENTER, 0),
                        "action");
        field.getActionMap().put("action", getAction());
    }

    private AbstractAction getAction(){
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pane.setValue(options[0]);
            }
        };
    }

    public void closeDialog(){
        dialog.dispose();
    }

}

