package nl.davefemi.prik2go.gui.factory.components;

import nl.davefemi.prik2go.controller.AuthController;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.gui.factory.components.authentication.ChangeForm;
import nl.davefemi.prik2go.gui.factory.components.util.MessageDialog;
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
            account.add(getLinkAccount("Google"));
            account.add(getLinkAccount("Outlook"));
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
                    MessageDialog.getErrorDialog(getParent(), ex.getMessage());
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
                    MessageDialog.getInfoDialog(null, "Password successfully changed");
                }
            } catch (Exception ex) {
                if (!(ex instanceof CancellationException)) {
                    MessageDialog.getErrorDialog(null, ex.getMessage());
                }
            }
        });
        return changePassword;
    }

    private JMenuItem getLinkAccount(String provider){
        JMenuItem linkAccount = new JMenuItem("Link " + provider +"-Account");
        linkAccount.addActionListener(e -> {
            JDialog loading = LoadingBar.getLoadingDialog(SwingUtilities.getWindowAncestor(menu));
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        if (AuthController.linkOAuth2User(provider)) {
                            return true;
                        }
                    } catch (ApplicationException ex) {
                        SwingBringToFront.bringPanelToFront(menu);
                        loading.setVisible(false);
                        MessageDialog.getErrorDialog(SwingUtilities.getWindowAncestor(menu), ex.getMessage());
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
                        MessageDialog.getErrorDialog(SwingUtilities.getWindowAncestor(menu), ex.getMessage());
                    }
                }
            };
            worker.execute();
            loading.setVisible(true);
        });
        return linkAccount;
    }

}
