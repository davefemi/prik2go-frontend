package nl.davefemi.prik2go.gui.factory.components;

import java.awt.*;

import javax.swing.JButton;

/**
 * Subklasse van JButton voor de aanmaak van een vestigingknop.
 */
public class BranchButton extends JButton {
        private static final long serialVersionUID = 1L;
        private static final Color OPENCOLOR = new Color (187, 214, 236);
        private static final Color CLOSEDCOLOR = new Color(236, 231, 231);
        private static final Color LETTERCOLOR = new Color(1,1,1);
        
        /**
         * Constructor voor de klasse VestigingKnop
         * @param location van de vestiging
         * @param open: status van de vestiging
         */
        public BranchButton(String location, boolean open) {
                super(location);
                this.setForeground(LETTERCOLOR);
                this.setFocusable(false);
                this.putClientProperty("branch", location);
                this.putClientProperty("status", open);
                this.setBackground(open ? OPENCOLOR : CLOSEDCOLOR);
        }

        public void setStatus(boolean status){
                this.putClientProperty("status", status);
                this.setBackground(status ? OPENCOLOR : CLOSEDCOLOR);
        }

}
