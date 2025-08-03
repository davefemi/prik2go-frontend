package nl.davefemi.prik2go.controller;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import nl.davefemi.prik2go.service.DataServiceInterface;

import javax.swing.*;

/**
 * Klasse verantwoordelijk voor de controllerfunctie van de VisualisatieView.
 */
public class VisualizerController implements VisualizerControllerInterface {
        private static final Logger logger = Logger.getLogger(VisualizerController.class.getName());
        private final DataServiceInterface service;
        private List<String> vestigingen;

        public VisualizerController(DataServiceInterface service) throws ApplicatieException, IllegalAccessException {
                 this.service = service;
                 initVestigingen();
        }

        /**
        * Verzorgt de initialisatie van de Vestiging-arraylist.
        */
        private void initVestigingen() throws ApplicatieException, IllegalAccessException {
                vestigingen = service.getVestigingen();
        }
        /**
         * Sluit of opent een vestiging als er op een bar is geklikt in de view.
         * @throws VestigingException 
         */
        @Override
        public void barClicked(String locatie, Consumer<Exception> exceptionHandler) {
                SwingWorker worker = new SwingWorker<Void, Void>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                                service.veranderVestigingStatus(locatie);
                                return null;
                        }

                        @Override
                        protected void done(){
                                try{
                                        get();
                                } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                } catch (ExecutionException e) {
                                        exceptionHandler.accept((Exception) e.getCause());
                                }
                        }
                };
                worker.execute();
        }
                
        /**
         * Keert een map terug met vestiginglocaties en aantal klanten
         */
        @Override
        public void getBarInfo(Consumer<Map<String, Integer>> callback, Consumer<Exception> exceptionHandling) {
                SwingWorker worker = new SwingWorker<List<Future<Integer>>, Void>() {
                        @Override
                        protected List<Future<Integer>> doInBackground() throws Exception {
                                ExecutorService pool = Executors.newFixedThreadPool(Math.min(12, vestigingen.size()));
                                List<Future<Integer>> futures = new ArrayList<Future<Integer>>();
                                for (String v : vestigingen) {
                                        Future future = pool.submit(() ->
                                                service.getKlantenDTO(v).getAantalKlanten());
                                        futures.add(future);
                                }
                                pool.shutdown();
                                return futures;
                        }

                        @Override
                        protected void done() {
                                try {
                                        List<Future<Integer>> futures = (List<Future<Integer>>) get();
                                        Map<String, Integer> nieuweMap = new TreeMap<String, Integer>();
                                        for (int i = 0; i<futures.size(); i++) {
                                                nieuweMap.put(vestigingen.get(i), futures.get(i).get());
                                        }
                                        callback.accept(nieuweMap);
                                } catch (Exception e) {
                                        exceptionHandling.accept(e);
                                        logger.warning("Fout opgetreden: " + e.getMessage());
                                }
                        }
                };
                worker.execute();
        }
}
