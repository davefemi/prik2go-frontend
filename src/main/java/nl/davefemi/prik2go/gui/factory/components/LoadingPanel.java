package nl.davefemi.prik2go.gui.factory.components;

import javax.swing.*;
import java.awt.*;

public class LoadingPanel extends JDialog {

    public LoadingPanel(JDialog owner, boolean modal){
        super(owner, modal);
        setSize(200,70);
        setLocationRelativeTo(owner);
        JPanel loading = new JPanel();
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        loading.setLayout(new GridBagLayout());
        loading.add(bar);
        add(loading);
    }
}
