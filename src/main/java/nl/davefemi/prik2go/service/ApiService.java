package nl.davefemi.prik2go.service;

import lombok.RequiredArgsConstructor;
import nl.davefemi.prik2go.client.ApiClientInterface;
import nl.davefemi.prik2go.dto.CustomerDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.exceptions.BranchException;
import java.util.*;

@RequiredArgsConstructor
public class ApiService implements DataServiceInterface {
    private final ApiClientInterface client;

    @Override
    public List<String> getBranches() throws ApplicationException, IllegalAccessException {
        return (List<String>) client.getBranches().getBody();
    }

    @Override
    public CustomerDTO getCustomerDTO(String locatie) throws IllegalAccessException, ApplicationException {
        return client.getCustomers(locatie).getBody();
    }

    @Override
    public boolean getBranchStatus(String locatie) throws ApplicationException, IllegalAccessException {
        return client.getBranchStatus(locatie).getBody();
    }

    @Override
    public void changeBranchStatus(String locatie) throws BranchException, ApplicationException, IllegalAccessException {
        client.changeBranchStatus(locatie);
    }
}
