package nl.davefemi.prik2go.gui.factory.components.util;

import javax.swing.*;
import java.awt.*;

public class ActiveWindow {
    private static Component activeComponent;

    public static void setActiveComponent(Component component){
        activeComponent = component;
    }

    public static void bringToFront() {
        if (activeComponent != null) {
            if (activeComponent instanceof JFrame) {
                SwingBringToFront.bringWindowToFront((JFrame) activeComponent);
            }
            else if (activeComponent instanceof JPanel)
                SwingBringToFront.bringPanelToFront((JPanel) activeComponent);
        }
        activeComponent = null;
    }

    public static Component getActiveComponent(){
        return activeComponent;
    }
}
