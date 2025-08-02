package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.client.ApiClient;
import nl.davefemi.prik2go.client.AuthClient;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicatieException;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public String loginUser(UserDTO user) throws ApplicatieException {
        if (user!=null) {
            return AuthClient.loginUser(user).getBody();
        }
        return null;
    }
}
