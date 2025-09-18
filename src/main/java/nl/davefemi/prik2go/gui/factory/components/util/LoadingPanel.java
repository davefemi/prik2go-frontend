package nl.davefemi.prik2go.gui.factory.components.util;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;


public class LoadingPanel extends JDialog {

    public LoadingPanel(JDialog owner){
        super(owner, APPLICATION_MODAL);
        this.setUndecorated(true);
        setSize(180,35);
        setLocationRelativeTo(owner);
        JPanel loading = new JPanel();
        loading.setOpaque(false);
        loading.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        JProgressBar bar = new JProgressBar();
        loading.setLayout(new GridBagLayout());
        loading.add(bar);
        add(loading);
        bar.setIndeterminate(true);
    }

    public LoadingPanel(Window owner){
        super(owner, APPLICATION_MODAL);
        this.setUndecorated(true);
        setSize(180,35);
        setLocationRelativeTo(owner);
        JPanel loading = new JPanel();
        loading.setOpaque(false);
        loading.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2, true));
        JProgressBar bar = new JProgressBar();
        loading.setLayout(new GridBagLayout());
        loading.add(bar);
        add(loading);
        bar.setIndeterminate(true);
    }
}
