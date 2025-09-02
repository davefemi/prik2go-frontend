package nl.davefemi.prik2go.gui.factory.components;

import nl.davefemi.prik2go.authentication.Authenticator;
import nl.davefemi.prik2go.authentication.ChangeForm;
import nl.davefemi.prik2go.client.AuthClient;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.BerichtDialoog;

import javax.naming.LimitExceededException;
import javax.swing.*;
import java.util.concurrent.CancellationException;

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
            account.add(getLogout());
            account.add(getChangePassword());
            account.add(getLinkAccount());
        }
        return account;
    }

    private JMenuItem getLogin() {
        JMenuItem login = new JMenuItem("Login");
        login.addActionListener(e -> {
            try {
                Authenticator.validateSession();
            } catch (Exception ex) {
                if (!(ex instanceof CancellationException))
                BerichtDialoog.getErrorDialoog(null, ex.getMessage());
            }
        });
        return login;
    }

    private JMenuItem getLogout(){
        JMenuItem logout = new JMenuItem("Log out");
        logout.addActionListener(e ->{
        });
        return logout;
    }

    private JMenuItem getChangePassword(){
        JMenuItem changePassword = new JMenuItem("Change password");
        changePassword.addActionListener(e ->{
            ChangeForm form = new ChangeForm();
            try {
                if (Authenticator.changePassword())
                    BerichtDialoog.getInfoDialoog(null, "Password successfully changed");
            } catch (Exception ex) {
                if (!(ex instanceof CancellationException))
                BerichtDialoog.getErrorDialoog(null, ex.getMessage());
            }
        });
        return changePassword;
    }

    private JMenuItem getLinkAccount(){
        JMenuItem linkAccount = new JMenuItem("Link Google-Account");
        linkAccount.addActionListener(e -> {
            try {
                Authenticator.linkGoogleAccount();
            } catch (IllegalAccessException ex) {
                BerichtDialoog.getErrorDialoog(null, ex.getMessage());
            }
        });
        return linkAccount;
    }

}
