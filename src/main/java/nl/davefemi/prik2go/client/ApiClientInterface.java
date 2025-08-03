package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApiClientInterface {

    public ResponseEntity<List> getBranches() throws ApplicatieException, IllegalAccessException;

    public ResponseEntity<KlantenDTO> getCustomers(String location) throws IllegalAccessException;

    public ResponseEntity<Boolean> getBranchStatus(String location) throws ApplicatieException, IllegalAccessException;

    public void changeBranchStatus(String location) throws ApplicatieException, VestigingException, IllegalAccessException;
}
