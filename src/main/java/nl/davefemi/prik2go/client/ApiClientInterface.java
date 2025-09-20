package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.dto.CustomerDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.exceptions.BranchException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApiClientInterface {

    public ResponseEntity<List> getBranches() throws ApplicationException, IllegalAccessException;

    public ResponseEntity<CustomerDTO> getCustomers(String location) throws IllegalAccessException, ApplicationException;

    public ResponseEntity<Boolean> getBranchStatus(String location) throws ApplicationException, IllegalAccessException;

    public void changeBranchStatus(String location) throws ApplicationException, BranchException, IllegalAccessException;
}
