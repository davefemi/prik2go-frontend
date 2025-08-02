package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;

import java.util.List;
import java.util.Map;

public interface DataServiceInterface {

    List<String> getVestigingen() throws ApplicatieException;

    KlantenDTO getKlantenDTO(String locatie);

    boolean getVestigingStatus(String locatie) throws ApplicatieException;

    void veranderVestigingStatus(String locatie) throws VestigingException, ApplicatieException;
}
