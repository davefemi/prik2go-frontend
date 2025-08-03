package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;

import java.util.List;
import java.util.Map;

public interface DataServiceInterface {

    List<String> getVestigingen() throws ApplicatieException, IllegalAccessException;

    KlantenDTO getKlantenDTO(String locatie) throws IllegalAccessException;

    boolean getVestigingStatus(String locatie) throws ApplicatieException, IllegalAccessException;

    void veranderVestigingStatus(String locatie) throws VestigingException, ApplicatieException, IllegalAccessException;
}
