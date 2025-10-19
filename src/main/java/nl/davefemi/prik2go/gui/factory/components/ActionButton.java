package nl.davefemi.prik2go.gui.factory.components;

import java.awt.Color;

import javax.swing.JButton;

/**
 * Klasse die een actieknop respresenteert die hoort bij de functies 'open vestiging',
 * 'sluit vestiging' en 'visualizer'
 */
public class ActionButton extends JButton {
        private static final long serialVersionUID = 1L;
        private static final String CHANGE_STATUS = "changeStatus";
        private static final String CLOSE_BRANCH = "Close branch";
        private static final String OPEN_BRANCH = "Open branch" ;
        private static final String VISUALIZER = "Visualizer";
        
        /**
         * Constructor voor deze ActieKnop
         * @param text
         */
        private ActionButton(String actie) {
                super(actie);
                this.setBackground(new Color(211,211,211));
                this.setFocusable(false); 
        }
        
        /**
         * Methode die een JButton met de tekst voor de wisselfunctie terugkeert.
         * @return wisselknop
         */
        public static ActionButton getStatusChangeSelector() {
                ActionButton wisselKnop = new ActionButton(CLOSE_BRANCH);
                wisselKnop.putClientProperty("action", CHANGE_STATUS);
                wisselKnop.setBackground(new Color(211,211,211));
                wisselKnop.setFocusable(false);
                wisselKnop.setVisible(false);
                return wisselKnop;
        }
        
        /**
         * Methode die een JButton met de tekst voor de visualizerfunctie terugkeert.
         * @return visualizerknop
         */
        public static ActionButton getVisualizerSelector() {
                ActionButton visualizerKnop = new ActionButton(VISUALIZER);
                visualizerKnop.setBackground(new Color(211,211,211));
                visualizerKnop.setFocusable(false);
                return visualizerKnop;
        }
        
        /**
         * Methode die de wisselfunctie aanpast aan de vestigingstatus
         * @param open
         */
        public void switchAction(boolean open) {
                if (this.getClientProperty("action").equals(CHANGE_STATUS)) {
                        setText(open ? CLOSE_BRANCH : OPEN_BRANCH);
                }
        }
}
