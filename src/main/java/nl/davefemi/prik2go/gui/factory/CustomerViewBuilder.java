package nl.davefemi.prik2go.gui.factory;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import nl.davefemi.prik2go.gui.factory.components.ActionButton;
import nl.davefemi.prik2go.gui.factory.components.Menu;
import nl.davefemi.prik2go.gui.factory.components.BranchButton;
import nl.davefemi.prik2go.gui.factory.components.util.LoadingBar;

/**
 * Deze klasse is verantwoordelijk voor het bouwen en beheren van de elementen
 * van de VestigingView.
 */
public class CustomerViewBuilder {
        private JMenuBar menuBar = null;
        private JPanel branchPanel = null;
        private ActionButton statusChangeButton = null;
        private JPanel customerPanel = null;
        private JLabel customerHeaderLabel = null;
        private JTextArea customerIdArea = null;
        private JPanel actionPanel = null;
        private JLabel totalCustomerLabel = null;
        private boolean branchError = false;
        private JPanel loading = null;
        private final ActionListener branchSelectorListener;
        private final ActionListener actionSelectorListener;
        private final ActionListener visualizerSelectorListener;
        private static final String NO_DATA_AVAILABLE = "No data available";
        private static final String CHOOSE_A_BRANCH = "Choose a branch...";
        private static final String CUSTOMER_HEADER_TEXT = "Branch: ";
        private static final String TOTAL_NUMBER_OF_CUSTOMERS = "Total customers: ";
        
        /**
         * Constructor voor deze builder klasse
         * @param branchSelectorListener
         * @param actionSelectorListener
         * @param visualizerSelectorListener
         */
        public CustomerViewBuilder(ActionListener branchSelectorListener,
                                   ActionListener actionSelectorListener,
                                   ActionListener visualizerSelectorListener) {
                this.branchSelectorListener = branchSelectorListener;
                this.actionSelectorListener = actionSelectorListener;
                this.visualizerSelectorListener = visualizerSelectorListener;
        }

        public JPanel getLoading(){
                if (loading == null) {
                        loading = LoadingBar.getLoadingPanel(false);
                }
                return loading;
        }

        public JMenuBar getMenu(){
                if (menuBar == null ){
                        menuBar = new Menu();
                        System.setProperty("apple.laf.useScreenMenuBar", "true");
                }
                return menuBar;
        }

        /**
         * Vestiging paneel wordt opgebouwd en knoppen worden toegevoegd met labels die
         * horen bij vestigingen.
         * @return vestigingpaneel
         */
        public JPanel getBranchPanel(Map<String, Boolean> locations) {
                if (branchPanel == null) {
                        branchPanel = new JPanel();
                        branchPanel.setLayout(new GridLayout(0, 1));
                }
                addBranchSelectors(locations);
                return branchPanel;
        
        }
        /**
         * Klantenpaneel wordt opgebouwd
         * @return klantenpaneel
         */
        public JPanel getCustomerPanel() {
                if (customerPanel == null) {
                        customerPanel = new JPanel();
                        customerPanel.setLayout(new BorderLayout(0,20));
                        customerPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,0));
                        customerPanel.add(getCustomerIdPanel(), BorderLayout.CENTER);
                        if (!branchError) {
                                customerPanel.add(getActionPanel(), BorderLayout.SOUTH);
                        }
                }
                return customerPanel;
        }
        
        /**
         * Vlag om aan te geven dat vestigingen niet konden worden opgehaald
         * @param status
         */
        public void setBranchError(boolean status) {
                branchError = status;
        }
        
        /**
         * Display wordt geupdate en voorzien van nieuwe data, vestigingspanelen worden gerefresht
         * @param locations
         * @param selectedLocation
         * @param customerIds
         * @param amntCustomers
         */
        public void updateDisplay(Map<String, Boolean> locations, String selectedLocation, List<Integer> customerIds,
                        int amntCustomers, boolean action) {
                boolean status = locations.get(selectedLocation);
                clearFields();
                if(customerIds.size() > 0) {
                        int nummer = 1;
                        for (int n : customerIds) {
                                customerIdArea.append("Customer " + nummer + ": " + String.valueOf(n) + '\n');
                                nummer++;
                        }   
                }
                else if(!status) {
                        customerIdArea.setText("This branch is closed");
                }
                else {
                        customerIdArea.setText("No customers found");
                }
                customerIdArea.setVisible(true);
                customerHeaderLabel.setText(CUSTOMER_HEADER_TEXT + selectedLocation);
                totalCustomerLabel.setText(TOTAL_NUMBER_OF_CUSTOMERS + amntCustomers);
                statusChangeButton.switchAction(status);
                statusChangeButton.setVisible(true);
                if (action) updateBranchSelectors(locations);
        }
        
        /**
         * Display wordt geupdate, vestigingspanelen worden gerefresht
         * @param locaties
         */
        public void updateDisplay(Map<String, Boolean> locaties) {
                updateBranchSelectors(locaties);
        }
        
        /**
         * Toewijzing van een error tekst aan het klantenveld bij een locatie.
         * @param location
         * @param text
         */
        public void displayError(String location, String text) {
                if (customerIdArea == null) {
                        customerIdArea = getCustomerIdArea();
                        }
                customerIdArea.setText(text);
                customerIdArea.setVisible(true);
                customerHeaderLabel.setText(CUSTOMER_HEADER_TEXT + location);
        }
        
        /**
         * Methode om het vestigingspaneel te voorzien van de locatienamen en hun status.
         * @param locations
         */
        private void addBranchSelectors(Map<String, Boolean> locations) {
                branchPanel.removeAll();
                for (Map.Entry<String, Boolean> locatie : locations.entrySet()) {
                        JButton knop = new BranchButton(locatie.getKey(), locatie.getValue());
                        knop.addActionListener(branchSelectorListener);
                        branchPanel.add(knop);
                        }
                branchPanel.revalidate();
                branchPanel.repaint();
        }

        private void updateBranchSelectors(Map<String, Boolean> locations ){
                Component[] knoppen = branchPanel.getComponents();
                for (Component c : knoppen){
                        BranchButton knop = (BranchButton) c;
                        boolean oldStatus = (boolean) knop.getClientProperty("status");
                        boolean newStatus = locations.get(knop.getClientProperty("branch"));
                        if (newStatus != (Boolean) oldStatus){
                                knop.setStatus(newStatus);
                        }
                }
        }
         
        /**
         * Creatie van tekstgebied waarin klantnummers kunnen worden getoond
         * @return
         */
        private JTextArea getCustomerIdArea() {
                if (customerIdArea == null) {
                        customerIdArea = new JTextArea();
                        customerIdArea.setOpaque(true);
                        customerIdArea.setTabSize(7);
                        customerIdArea.setBackground(Color.WHITE);
                        customerIdArea.setEditable(false);
                        customerIdArea.setVisible(false);
                }
                if (branchError) {
                        customerIdArea.setText(NO_DATA_AVAILABLE);
                        customerIdArea.setVisible(true);
                }
                return customerIdArea;
        }

        /**
         * Panel waarop klantnummer zichtbaar worden, wordt opgebouwd. 
         * @return klantnummerpaneel
         */
        private JPanel getCustomerIdPanel() {
                JPanel customerIdPanel = new JPanel();
                customerIdPanel.setLayout(new BorderLayout(0,20));
                ScrollPane schuifpaneel = new ScrollPane();
                schuifpaneel.add(getCustomerIdArea());
                customerIdPanel.add(getCustomerHeaderLabel(), BorderLayout.NORTH);
                customerIdPanel.add(schuifpaneel, BorderLayout.CENTER);
                customerIdPanel.add(getTotalCustomerLabel(), BorderLayout.SOUTH);
                return customerIdPanel;
        }
        
        /**
         * Velden worden leeggemaakt ten behoeve van het weergeven van nieuwe informatie.
         */
        private void clearFields() {
                customerIdArea.setText("");
                totalCustomerLabel.setText("");
        }
        
        /**
         * Koptekstlabel wordt gecreeerd
         * @return klantkoptekstlabel
         */
        private JLabel getCustomerHeaderLabel() {
                if (customerHeaderLabel == null) {
                        customerHeaderLabel = new JLabel();
                }
                String text = branchError ? "" : CHOOSE_A_BRANCH;
                customerHeaderLabel.setText(text);
                customerHeaderLabel.setFont(new Font("Plain", Font.BOLD, 18));
                return customerHeaderLabel;
        }
        

        /**
         * Creatie van JLabel voor het weergeven van klantenaantallen
         * @return totaalklantenlabel
         */
        private JLabel getTotalCustomerLabel() {
                if (totalCustomerLabel == null) {
                        totalCustomerLabel = new JLabel();
                        totalCustomerLabel.setFont(new Font("Plain", Font.BOLD, 14));
                }
                return totalCustomerLabel;
        }
        
        /**
         * Creatie van paneel waarop de actieknoppen zichtbaar zijn
         * @return actiepaneel
         */
        private JPanel getActionPanel() {
                if (actionPanel == null) {
                        actionPanel = new JPanel();
                        actionPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,15));
                        actionPanel.setLayout(new GridLayout (0,2));
                        addActionSelectors();
                }
                return actionPanel;
        }
        
        /**
         * Methode om actieknoppen aan te maken. Een actieknop om de status van de actieve vestiging
         * aan te passen en een visualizerknop om de visualizer van de vestigingen te openen.
         */
        private void addActionSelectors() {
                statusChangeButton = ActionButton.getStatusChangeSelector();
                statusChangeButton.addActionListener(actionSelectorListener);
                actionPanel.add(statusChangeButton);
                ActionButton visualizerButton = ActionButton.getVisualizerSelector();
                visualizerButton.addActionListener(visualizerSelectorListener);
                actionPanel.add(visualizerButton);
                }
}
