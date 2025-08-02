package nl.davefemi.prik2go.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Skin;
import nl.davefemi.prik2go.controller.VisualizerControllerInterface;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.BerichtDialoog;
import nl.davefemi.prik2go.exceptions.VestigingException;
import nl.davefemi.prik2go.gui.factory.components.Bar;
import nl.davefemi.prik2go.observer.ApiObserver;
import nl.davefemi.prik2go.observer.ApiSubject;

/**
 * Klasse verantwoordelijk voor de visualisatie van input op de verschillende vestigingen
 */
public class VisualizerView extends JFrame implements ApiObserver {
        private static final long serialVersionUID = 1L;
        private final VisualizerControllerInterface controller;
        private static final int WIDTH_FRAME = 1200;
        private static final int HEIGHT_FRAME = 520;
        private static final int MARGIN = 10;
        private static final String TITLE = "Visualizer";
        private final int WIDTH_PANE = WIDTH_FRAME - 2 * MARGIN;
        private final int HEIGHT_PANE = HEIGHT_FRAME - 3 * MARGIN;
        private static final int HGAP = 10;
        private Container pane = null;


        /**
         * Constructor voor Visualizer View waarbij de controller wordt geinitialiseerd en de view wordt
         * aangemeld als een observer van de vestingsklassen. De initiële visualisatie wordt opgestart.
         * @param controller
         * @throws ApplicatieException
         */
        public VisualizerView(VisualizerControllerInterface controller) throws ApplicatieException {
                super();
                this.controller = controller;
                initialize();
                JLabel loading = new JLabel("Loading...");
                this.setLayout(new BorderLayout());
                loading.setHorizontalAlignment(SwingConstants.CENTER);
                loading.setVerticalAlignment(SwingConstants.CENTER);
                this.add(loading, BorderLayout.CENTER);
                getMap(stringIntegerMap -> {
                    try {
                            this.remove(loading);
                            this.setLayout(null);
                            drawBars(stringIntegerMap);
                    } catch (ApplicatieException e) {
                            throw new RuntimeException(e);
                    }
                });
        }

        /**
         * Initialisatie waarbij de JFrame wordt opgebouwd.
         */
        private void initialize() {
                setSize(900, 600);
                pane = this.getContentPane();
                pane.setBounds(MARGIN, MARGIN, WIDTH_PANE, HEIGHT_PANE);
                pane.setBackground(new Color(203,203,203));
                setTitle(TITLE);
                this.setSize(WIDTH_FRAME, HEIGHT_FRAME);
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                this.setLocation((int) Math.round((dim.width - WIDTH_FRAME) / 1.1)
                                        ,(int) Math.round((dim.height - HEIGHT_FRAME) / 1.1)
                                        );
                
        }
        
        /**
         * Map wordt gevuld met een value pair van locatie en aantal klanten
         * @return map<locatie, aantal klanten>
         */
        private void getMap(Consumer<Map<String, Integer>> callback) {
                controller.getBarInfo(stringIntegerMap ->{
                        callback.accept(stringIntegerMap);
                }, exception ->{

                });
        }

        /**
         * Tekent de bars op basis van de map met (key, value) paren.
         * Bars met hoogte 0 worden getoond als bars met maximale hoogte in de
         * achtergrondkleur.
         * @throws ApplicatieException 
         * 
         */
        private void drawBars(Map<String, Integer> map) throws ApplicatieException {
                pane.removeAll();
                try {
                        int size = map.size();
                        int total_hgap = (size + 1) * HGAP;
                        int width_bar = (WIDTH_PANE - total_hgap) / size;
                        int maxValue = maxValue(map);
                        double verticalScaleFactor = (double) HEIGHT_PANE / (double) maxValue;
                        Set<String> keys = map.keySet();
                        int x_pos = 0;
                        for (String key : keys) {
                                x_pos = x_pos + HGAP;
                                int value = map.get(key);
                                createBar(key, value, x_pos, verticalScaleFactor, width_bar);
                                x_pos = x_pos + width_bar;
                        }
                }
                catch (Exception e) {
                        throw new ApplicatieException("Fout met rendering visualiser");
                }
                
                this.repaint();
                this.revalidate();
        }
        
        /**
         * Aanmaak van een nieuw Bar object aan de hand van de meegegeven variabelen
         * @param key naam van de locatie
         * @param value aantal klanten
         * @param x_pos positie van de vestiging
         * @param verticalScaleFactor verticale hoogte
         * @param width_bar breedte bar
         */
        private void createBar(String key, int value, int x_pos, double verticalScaleFactor, int width_bar) {
                Bar bar;
                if (value == 0) {
                        // bar in achtergrondkleur met maximale hoogte om label bovenaan te krijgen.
                        int y_pos = 0;
                        bar = new Bar(key, 0, x_pos, y_pos, width_bar, HEIGHT_PANE, new Color(203,203,203));
                } else {
                        // // bar in kleur geel met hoogte: schaalfactor * aantal klanten.
                        int height_bar = (int) (verticalScaleFactor * value);
                        int y_pos = HEIGHT_PANE - height_bar;
                        bar = new Bar(key, value, x_pos, y_pos, width_bar, height_bar, new Color(0,80,80));
                }
                bar.addMouseListener(new BarLuisteraar());
                pane.add(bar);
        }

        /**
         * Geeft de grootste value in de map
         * 
         * @return
         */
        private int maxValue(Map<String, Integer> map) {
                int max = 0;
                Set<String> keys = map.keySet();
                for (String key : keys) {
                        if (map.get(key) > max) {
                                max = map.get(key);
                        }
                }
                return max;
        }

        private void setProgress(){
                this.removeAll();
                JFXPanel fxPanel = new JFXPanel();
                SwingUtilities.invokeLater(() -> this.add(fxPanel));
                Platform.runLater(() -> {
                        ProgressIndicator indicator = new ProgressIndicator();
                        fxPanel.setScene(new Scene(indicator));
                });
        }
        
        /**
         * Bij notificatie van VestigingSubject object worden nieuwe gegevens opgehaald en getoond op
         * het scherm. In het geval van een exceptie wordt een error dialoog getoond.
         */
        @Override
        public void update(ApiSubject s, Object arg) {
//                setProgress();
                controller.getBarInfo(stringIntegerMap -> {
                    try {
                        drawBars(stringIntegerMap);
                    } catch (ApplicatieException e) {
                        throw new RuntimeException(e);
                    }
                }, exception ->{
                        BerichtDialoog.getErrorDialoog(this, exception.getMessage());
                });
        }

        /**
         * Luisteraar implementatie bij het Bar-object
         */
        private class BarLuisteraar extends MouseAdapter {
                
                /**
                 * Actie bij een event gegenereerd door deze Bar. Een nieuwe Map met value pairs wordt
                 * opgevraagd van de controller.
                 */
                public void mousePressed(MouseEvent e) {
                        Bar bar = (Bar) e.getSource();
                        controller.barClicked(bar.getName(), ex ->{
                                if (ex instanceof VestigingException){
                                        BerichtDialoog.getInfoDialoog(getContentPane(), ex.getMessage());
                                }
                                else {
                                        BerichtDialoog.getErrorDialoog(getContentPane(), ex.getMessage());
                                }
                        });
                }
        }
}
