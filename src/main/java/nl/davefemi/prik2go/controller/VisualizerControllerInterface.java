package nl.davefemi.prik2go.controller;

import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 
 * Intermediair tussen de userinterface (klasse Visualizer met daarop bars (staafjes)) 
 * en de domeinklassen 
 * @author Mederwerker OU
 *
 */
public interface VisualizerControllerInterface {

  void barClicked(String locatie, Consumer<Exception> exceptionHandler);

  void getBarInfo(Consumer<Map<String, Integer>> callback, Consumer<Exception> exceptionHandling);
}

