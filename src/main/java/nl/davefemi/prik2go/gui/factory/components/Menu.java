package nl.davefemi.prik2go.gui.factory.components;

import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.gui.factory.components.authentication.ChangeForm;
import nl.davefemi.prik2go.gui.factory.components.util.BerichtDialoog;
import nl.davefemi.prik2go.gui.factory.components.util.SpringUtilities;
import nl.davefemi.prik2go.gui.factory.components.util.SwingBringToFront;

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
                if (AuthController.validateSession())
                {
                    SwingBringToFront.bringPanelToFront(this);
                }
            } catch (Exception ex) {
                if (!(ex instanceof CancellationException)) {
                    SwingBringToFront.bringPanelToFront(this);
                    BerichtDialoog.getErrorDialoog(getParent(), ex.getMessage());
                }
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
                if (AuthController.changePassword()) {
                    BerichtDialoog.getInfoDialoog(null, "Password successfully changed");
                }
            } catch (Exception ex) {
                if (!(ex instanceof CancellationException)) {
                    BerichtDialoog.getErrorDialoog(null, ex.getMessage());
                }
            }
        });
        return changePassword;
    }

    private JMenuItem getLinkAccount(){
        JMenuItem linkAccount = new JMenuItem("Link Google-Account");
        linkAccount.addActionListener(e -> {
            try {
                if (AuthController.linkGoogleAccount()){
                    SwingBringToFront.bringPanelToFront(this);
                }
            } catch (Exception ex) {
                SwingBringToFront.bringPanelToFront(this);
                BerichtDialoog.getErrorDialoog(getParent(), ex.getMessage());
            }
        });
        return linkAccount;
    }

}
