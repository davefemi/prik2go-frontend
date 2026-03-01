package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.dto.BranchDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import nl.davefemi.prik2go.exceptions.BranchException;

import java.util.List;

public interface DataServiceInterface {

    List<String> getBranches() throws ApplicationException, IllegalAccessException;

    BranchDTO getCustomerDTO(String locatie) throws IllegalAccessException, ApplicationException;

    boolean getBranchStatus(String locatie) throws ApplicationException, IllegalAccessException;

    void changeBranchStatus(String locatie) throws BranchException, ApplicationException, IllegalAccessException;
}
