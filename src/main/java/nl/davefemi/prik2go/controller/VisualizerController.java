package nl.davefemi.prik2go.controller;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.service.DataServiceInterface;

import javax.swing.*;

/**
 * Klasse verantwoordelijk voor de controllerfunctie van de VisualisatieView.
 */
public class VisualizerController implements VisualizerControllerInterface {
        private static final Logger logger = Logger.getLogger(VisualizerController.class.getName());
        private final DataServiceInterface service;
        private List<String> vestigingen;

        public VisualizerController(DataServiceInterface service) throws ApplicationException, IllegalAccessException {
                 this.service = service;
                 initVestigingen();
        }

        /**
        * Verzorgt de initialisatie van de Vestiging-arraylist.
        */
        private void initVestigingen() throws ApplicationException, IllegalAccessException {
                vestigingen = service.getBranches();
        }
        /**
         * Sluit of opent een vestiging als er op een bar is geklikt in de view.
         */
        @Override
        public void barClicked(String locatie, Consumer<Exception> exceptionHandler) {
                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                                service.changeBranchStatus(locatie);
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
                SwingWorker<List<Future<Integer>>, Void> worker = new SwingWorker<>() {
                        @Override
                        protected List<Future<Integer>> doInBackground() {
                                ExecutorService pool = Executors.newFixedThreadPool(Math.min(12, vestigingen.size()));
                                List<Future<Integer>> futures = new ArrayList<>();
                                for (String v : vestigingen) {
                                        Future<Integer> future = pool.submit(() ->
                                                service.getCustomerDTO(v).getNumberOfCustomers());
                                        futures.add(future);
                                }
                                pool.shutdown();
                                return futures;
                        }

                        @Override
                        protected void done() {
                                try {
                                        List<Future<Integer>> futures = get();
                                        Map<String, Integer> nieuweMap = new TreeMap<>();
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
