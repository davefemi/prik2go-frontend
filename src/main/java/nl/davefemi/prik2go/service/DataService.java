package nl.davefemi.prik2go.service;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.client.ApiClient;
import nl.davefemi.prik2go.client.ApiClientInterface;
import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import java.util.*;

@RequiredArgsConstructor
public class DataService implements DataServiceInterface {
    private final ApiClientInterface client;

    @Override
    public List<String> getVestigingen() throws ApplicatieException {
        return (List<String>) client.getBranches().getBody();
    }

    @Override
    public KlantenDTO getKlantenDTO(String locatie) {
        return client.getCustomers(locatie).getBody();
    }

    @Override
    public boolean getVestigingStatus(String locatie) throws ApplicatieException {
        return client.getBranchStatus(locatie).getBody();
    }

    @Override
    public void veranderVestigingStatus(String locatie) throws VestigingException, ApplicatieException {
        client.changeBranchStatus(locatie);
    }
}
