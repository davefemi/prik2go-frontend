package nl.davefemi.prik2go.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID user;
    private String email;
    private String password;
    private String newPassword;
}
