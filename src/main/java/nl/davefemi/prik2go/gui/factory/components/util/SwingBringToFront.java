package nl.davefemi.prik2go.gui.factory.components.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;

public final class SwingBringToFront {

    public static void bringPanelToFront(JComponent panel) {
        Window w = SwingUtilities.getWindowAncestor(panel);
        if (w != null) bringWindowToFront(w);

        JTabbedPane tabs = (JTabbedPane) SwingUtilities.getAncestorOfClass(JTabbedPane.class, panel);
        if (tabs != null) {
            if (tabs.getSelectedComponent() != panel) {
                tabs.setSelectedComponent(panel);
            }
        }

        Container parent = panel.getParent();
        if (parent != null && parent.getLayout() instanceof CardLayout) {
            Object cardName = panel.getClientProperty("cardName");
            if (cardName instanceof String) {
                ((CardLayout) parent.getLayout()).show(parent, (String) cardName);
            }
        }

        JScrollPane sp = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, panel);
        if (sp != null) {
            Rectangle r = panel.getBounds();
            panel.scrollRectToVisible(new Rectangle(0, 0, r.width, r.height));
        }

        Container p = panel.getParent();
        if (p != null && !(p.getLayout() instanceof java.awt.LayoutManager2)) {
            // only helps when components actually overlap
            p.setComponentZOrder(panel, 0); // 0 == topmost in Swing
            p.revalidate();
            p.repaint();
        }

        // 3) Try to move keyboard focus into the panel
        panel.requestFocusInWindow();
    }

    /** Keep AOT only until the window gains focus, then clear it (with a safety timeout). */
    public static void bringWindowToFront (Window w) {
        if (w instanceof Frame f) {
            int state = f.getExtendedState();
            if ((state & Frame.ICONIFIED) != 0) f.setExtendedState(state & ~Frame.ICONIFIED);
        }
        try { w.setAutoRequestFocus(false); } catch (Throwable ignore) {}
        w.setAlwaysOnTop(true);
        w.toFront();

        final WindowAdapter clear = new WindowAdapter() {
            private void clearNow() {
                w.setAlwaysOnTop(false);
                w.removeWindowFocusListener(this);
                w.removeWindowListener(this);
            }
            @Override public void windowActivated(java.awt.event.WindowEvent e) { clearNow(); }
            @Override public void windowGainedFocus(java.awt.event.WindowEvent e) { clearNow(); }
        };
        w.addWindowFocusListener(clear);
        w.addWindowListener(clear);

        new javax.swing.Timer(4000, e -> {
            w.setAlwaysOnTop(false);
            w.removeWindowFocusListener(clear);
            w.removeWindowListener(clear);
        }) {{ setRepeats(false); start(); }};
    }
}
