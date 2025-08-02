package nl.davefemi.prik2go.client;

import nl.davefemi.prik2go.dto.KlantenDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import nl.davefemi.prik2go.exceptions.VestigingException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ApiClientInterface {

    public ResponseEntity<List> getBranches() throws ApplicatieException;

    public ResponseEntity<KlantenDTO> getCustomers(String location);

    public ResponseEntity<Boolean> getBranchStatus(String location) throws ApplicatieException;

    public void changeBranchStatus(String location) throws ApplicatieException, VestigingException;
}
