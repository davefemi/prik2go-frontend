package nl.davefemi.prik2go.authentication;

import nl.davefemi.prik2go.dto.SessionDTO;
import nl.davefemi.prik2go.service.AuthService;

import javax.swing.*;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;

public class Authenticator {
    private static final AuthService authService = new AuthService();
    
    public static void login(UUID user, Consumer<SessionDTO> callback, Consumer<Exception> exceptionConsumer) {
        LoginForm loginForm = new LoginForm();
        SwingWorker worker = new SwingWorker<SessionDTO, Void>() {

            @Override
            protected SessionDTO doInBackground() throws Exception {
                return authService.loginUser(loginForm.getUserLogin(user != null ? user : null));
            }

            @Override
            protected void done() {
                try {
                    SessionDTO result = (SessionDTO) get();
                    callback.accept(result);
                } catch (IllegalArgumentException e) {
                    exceptionConsumer.accept((Exception) e.getCause());
                } catch (CancellationException e) {

                } catch (Exception e) {
                    exceptionConsumer.accept((Exception) e.getCause());
                }
            }
        };
        worker.execute();
    }
}
