package lk.ac.kln.unimart.auth.dto;

public record UserDto(
        Long id,
        String email,
        String fullName,
        String role
) {}
