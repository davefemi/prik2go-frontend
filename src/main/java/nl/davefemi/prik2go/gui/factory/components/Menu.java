package nl.davefemi.prik2go.gui.factory.components;

import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.gui.factory.components.authentication.ChangeForm;
import nl.davefemi.prik2go.gui.factory.components.util.BerichtDialoog;
import nl.davefemi.prik2go.gui.factory.components.util.LoadingBar;
import nl.davefemi.prik2go.gui.factory.components.util.SwingBringToFront;

import javax.swing.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class Menu extends JMenuBar {
    private JMenu account = null;
    private Menu menu = this;

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
            JDialog loading = LoadingBar.getLoadingDialog(SwingUtilities.getWindowAncestor(menu));
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        if (AuthController.loginWithGoogle()) {
                            return true;
                        }
                    } catch (ApplicatieException ex) {
                        SwingBringToFront.bringPanelToFront(menu);
                        loading.setVisible(false);
                        BerichtDialoog.getErrorDialoog(SwingUtilities.getWindowAncestor(menu), ex.getMessage());
                    }
                    return false;
                }

                @Override
                public void done(){
                    try {
                        if (get()){
                            SwingBringToFront.bringPanelToFront(menu);
                            loading.setVisible(false);
                        }
                    } catch (InterruptedException ex) {
                        loading.setVisible(false);
                        SwingBringToFront.bringPanelToFront(menu);
                        throw new RuntimeException(ex);
                    } catch (ExecutionException ex) {
                        loading.setVisible(false);
                        SwingBringToFront.bringPanelToFront(menu);
                        BerichtDialoog.getErrorDialoog(SwingUtilities.getWindowAncestor(menu), ex.getMessage());
                    }
                }
            };
            worker.execute();
            loading.setVisible(true);
        });
        return linkAccount;
    }

}
