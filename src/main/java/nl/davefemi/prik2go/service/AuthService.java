package nl.davefemi.prik2go.service;

import nl.davefemi.prik2go.client.AuthClient;
import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.dto.UserDTO;
import nl.davefemi.prik2go.exceptions.ApplicationException;

public class AuthService {

    public SessionDTO loginUser(UserDTO user) throws ApplicationException {
        if (user!=null) {
            return AuthClient.loginUser(user).getBody();
        }
        return null;
    }

    public void changePassword(UserDTO user) throws ApplicationException {
        AuthClient.changePassword(user);
    }

    public boolean linkOAuth2User(String provider) throws ApplicationException {
        return AuthClient.linkOAuth2User(provider);
    }

    public boolean unlinkOAuth2User() throws ApplicationException {
        return AuthClient.unlinkOAuth2User();
    }

    public SessionDTO loginOAuth2User(String provider) throws ApplicationException {
        return AuthClient.loginOAuth2User(provider);
    }
}
