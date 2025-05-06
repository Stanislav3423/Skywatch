package org.meteoinit.skywatch.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String role;
    private boolean enabled;

    public UserDto(Long id, String username, String role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
    }
}
