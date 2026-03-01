package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.dto.BranchDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.exceptions.BranchException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApiClientInterface {

    ResponseEntity<List> getBranches() throws ApplicationException, IllegalAccessException;

    ResponseEntity<BranchDTO> getCustomers(String location) throws IllegalAccessException, ApplicationException;

    ResponseEntity<Boolean> getBranchStatus(String location) throws ApplicationException, IllegalAccessException;

    void changeBranchStatus(String location) throws ApplicationException, BranchException, IllegalAccessException;
}
