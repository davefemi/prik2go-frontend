package nl.davefemi.prik2go.gui.factory.components.util;

import javax.swing.*;
import java.awt.*;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;

public class LoadingBar {

    public static JDialog getLoadingDialog(JDialog owner){
        JDialog dialog = new JDialog(owner, APPLICATION_MODAL);
        buildDialog(dialog, owner);
        return dialog;
    }

    public static JDialog getLoadingDialog(Window owner){
        JDialog dialog = new JDialog (owner, APPLICATION_MODAL);
        buildDialog(dialog, owner);
        return dialog;
    }

    public static JPanel getLoadingPanel(boolean setBorder){
        JPanel loading = new JPanel();
        loading.setOpaque(false);
        if (setBorder)
            loading.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        loading.setLayout(new GridBagLayout());
        loading.add(bar);
        return loading;
    }

    private static void buildDialog(JDialog dialog, Component owner){
        dialog.setUndecorated(true);
        dialog.setSize(180,35);
        dialog.setLocationRelativeTo(owner);
        dialog.add(getLoadingPanel(true));
    }

}
