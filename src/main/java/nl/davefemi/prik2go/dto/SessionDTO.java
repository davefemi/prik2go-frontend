package nl.davefemi.prik2go.dto;

import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
public class SessionDTO {
    private UUID user;
    private String token;
    private UUID tokenId;
    private Instant expiresAt;
}
