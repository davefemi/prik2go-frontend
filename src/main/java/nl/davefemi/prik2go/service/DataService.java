package nl.davefemi.prik2go.service;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.client.ApiClientInterface;
import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import java.util.*;

@RequiredArgsConstructor
public class DataService implements DataServiceInterface {
    private final ApiClientInterface client;

    @Override
    public List<String> getVestigingen() throws ApplicatieException, IllegalAccessException {
        return (List<String>) client.getBranches().getBody();
    }

    @Override
    public KlantenDTO getKlantenDTO(String locatie) throws IllegalAccessException, ApplicatieException {
        return client.getCustomers(locatie).getBody();
    }

    @Override
    public boolean getVestigingStatus(String locatie) throws ApplicatieException, IllegalAccessException {
        return client.getBranchStatus(locatie).getBody();
    }

    @Override
    public void veranderVestigingStatus(String locatie) throws VestigingException, ApplicatieException, IllegalAccessException {
        client.changeBranchStatus(locatie);
    }
}
