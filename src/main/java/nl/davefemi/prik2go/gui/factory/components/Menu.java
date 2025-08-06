package nl.davefemi.prik2go.gui.factory.components;

import nl.davefemi.prik2go.Prik2GoApp;
import nl.davefemi.prik2go.authentication.Authenticator;
import nl.davefemi.prik2go.client.ApiClient;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.BerichtDialoog;

import javax.crypto.NullCipher;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import java.util.concurrent.*;

public class Menu extends JMenuBar {
    private JMenu account = null;

    public Menu(){
        super();
        init();
    }

    private void init(){
        this.add(getAccount());
    }

    private JMenu getAccount(){
        if (account == null){
            account = new JMenu("Account");
            account.add(getLogin());
            account.add(new JMenuItem("Logout"));
            account.add(new JMenuItem("Change Password"));
        }
        return account;
    }

    private JMenuItem getLogin() {
        JMenuItem login = new JMenuItem("Login");
        login.addActionListener(e -> {
            try {
                Authenticator.ensureValidSession();
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        });
        return login;
    }

}
