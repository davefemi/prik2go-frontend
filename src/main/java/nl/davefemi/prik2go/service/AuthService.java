package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.client.AuthClient;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import java.util.logging.Logger;

public class AuthService {
    private static final Logger logger = Logger.getLogger(AuthService.class.getName());

    public SessionDTO loginUser(UserDTO user) throws ApplicationException {
        if (user!=null) {
            return AuthClient.loginUser(user).getBody();
        }
        return null;
    }

    public SessionDTO changePassword(UserDTO user) throws ApplicationException {
        return AuthClient.changePassword(user).getBody();
    }

    public boolean linkOAuth2User(String provider) throws ApplicationException {
        return AuthClient.linkOAuth2User(provider);
    }

    public SessionDTO loginOAuth2User(String provider) throws ApplicationException {
        return AuthClient.loginOAuth2User(provider);
    }
}
