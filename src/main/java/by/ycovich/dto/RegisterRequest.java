package by.ycovich.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private Integer id;
    private String username;
    private String email;
    private String password;
}
