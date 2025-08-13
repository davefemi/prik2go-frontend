package nl.davefemi.prik2go.authentication;

import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.gui.factory.components.SpringUtilities;
import javax.naming.LimitExceededException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.CancellationException;
import static java.awt.event.KeyEvent.VK_ENTER;

public class ChangeForm extends JPanel {
    private JOptionPane pane;
    private final String[] options = {"Save", "Cancel"};
    private static final String CURRENT_PASSWORD = "Current password";
    private static final String REPEAT_NEW_PASSWORD = "Repeat new password";
    private static final String NEW_PASSWORD = "New password";
    private static final String[] LABELS = { CURRENT_PASSWORD, NEW_PASSWORD, REPEAT_NEW_PASSWORD};
    private static final JPasswordField passwordField = new JPasswordField(20);
    private static final JPasswordField newPasswordField = new JPasswordField(20);
    private static final JPasswordField repeatNewPasswordField = new JPasswordField(20);
    private static final JPasswordField[] PASSWORD_FIELDS = {passwordField, newPasswordField, repeatNewPasswordField};
    private final ChangeForm panel = this;
    private JLabel error;

    public ChangeForm(){
        super();
        buildPanel();
    }

    private void buildPanel(){
        panel.setSize(400,400);
        panel.setLayout(new GridLayout(0,1));
        panel.addPasswordFields();
    }

    private void addPasswordFields(){
        JPanel panel = new JPanel();
        panel.setLayout(new SpringLayout());
        for (int i = 0; i< LABELS.length; i++){
            JLabel l = new JLabel(LABELS[i], JLabel.TRAILING);
            panel.add(l);
            PASSWORD_FIELDS[i].setText("");
            l.setLabelFor(PASSWORD_FIELDS[i]);
            setAction(PASSWORD_FIELDS[i]);
            panel.add(PASSWORD_FIELDS[i]);
        }
        JLabel l = new JLabel("", JLabel.TRAILING);
        error = new JLabel();
        error.setForeground(Color.RED);
        l.setLabelFor(error);
        panel.add(l);
        panel.add(error);
        SpringUtilities.makeCompactGrid(panel,
                4, 2,
                10,10,
                10,15);
        this.panel.add(panel, BorderLayout.CENTER);
    }

    public UserDTO getUserInput() throws LimitExceededException {
        int retries = 5;
        while(retries --> 0) {
            try {
                pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
                        JOptionPane.DEFAULT_OPTION, null, options);
                JDialog dialog = pane.createDialog("Change password");
                dialog.setSize(500, 250);
                dialog.setVisible(true);
                return processInput();
            } catch (IllegalArgumentException e) {
                error.setText(e.getMessage());
                for (JPasswordField f : PASSWORD_FIELDS){
                    f.setText("");
                }
            }
        }
        throw new LimitExceededException("Too many attempts");
    }

    private UserDTO processInput() throws IllegalArgumentException{
        error.setText("");
        if (!(pane.getValue() instanceof String))
            throw new CancellationException("User closed window");
        String result = (String) pane.getValue();
        if (result != null && result.equals("Save")){
            if ((new String(passwordField.getPassword()).isBlank()
                    || new String(newPasswordField.getPassword()).isBlank()
                    || new String(repeatNewPasswordField.getPassword()).isBlank())) {
                throw new IllegalArgumentException("Fields cannot be blank");
            }
            if (!new String(newPasswordField.getPassword())
                    .equals(new String(repeatNewPasswordField.getPassword()))){
                throw new IllegalArgumentException("New password values must be the same");
            }
            UserDTO dto = new UserDTO();
            dto.setPassword(new String(passwordField.getPassword()));
            dto.setNewPassword(new String(newPasswordField.getPassword()));
            repeatNewPasswordField.setText("");
            passwordField.setText("");
            return dto;
        }
        throw new CancellationException("User cancelled");
    }

    private void setAction(JPasswordField field){
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
}
