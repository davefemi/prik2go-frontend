package nl.davefemi.prik2go.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class OAuthRequestDTO {
    private UUID requestCode;
    private String secret;
    private Long pollingInterval;
    private Instant expiresAt;
    private String url;
}
