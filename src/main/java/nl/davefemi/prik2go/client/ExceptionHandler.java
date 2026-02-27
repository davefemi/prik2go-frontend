package nl.davefemi.prik2go.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.davefemi.prik2go.exceptions.ApplicationException;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.client.HttpClientErrorException;

public class ExceptionHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    protected static void handleHttpClientErrorException(HttpClientErrorException e) throws ApplicationException {
        if (MediaType.APPLICATION_PROBLEM_JSON.equals(e.getResponseHeaders().getContentType())) {
            ProblemDetail pd;
            try {
                pd = objectMapper.readValue(e.getResponseBodyAsString(), ProblemDetail.class);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            throw new ApplicationException(pd.getDetail());
        }
        throw new ApplicationException(e.getResponseBodyAsString());
    }
}
