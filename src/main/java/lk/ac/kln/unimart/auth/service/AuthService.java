package lk.ac.kln.unimart.auth.service;

import lk.ac.kln.unimart.auth.dto.AuthResponse;
import lk.ac.kln.unimart.auth.dto.LoginRequest;
import lk.ac.kln.unimart.auth.dto.RegisterRequest;
import lk.ac.kln.unimart.auth.dto.UserDto;
import lk.ac.kln.unimart.auth.entity.User;
import lk.ac.kln.unimart.auth.entity.UserRole;
import lk.ac.kln.unimart.auth.repository.UserRepository;
import lk.ac.kln.unimart.common.exception.ConflictException;
import lk.ac.kln.unimart.common.exception.ForbiddenException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository users, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (users.existsByUniversityEmail(request.universityEmail())) {
            throw new ConflictException("An account with this email already exists");
        }

        User user = new User();
        user.setUniversityEmail(request.universityEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName().trim());
        user.setRole(UserRole.STUDENT);
        user.setActive(true);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        users.save(user);

        String token = jwtService.generateAccessToken(user.getUniversityEmail());
        UserDto userDto = new UserDto(user.getId(), user.getUniversityEmail(), user.getFullName(), user.getRole().name());
        return new AuthResponse(token, "Bearer", jwtService.getAccessMinutes() * 60, userDto);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = users.findByUniversityEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ForbiddenException("Invalid email or password"));

        if (!user.isActive()) {
            throw new ForbiddenException("Account is not active");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ForbiddenException("Invalid email or password");
        }

        String token = jwtService.generateAccessToken(user.getUniversityEmail());
        UserDto userDto = new UserDto(user.getId(), user.getUniversityEmail(), user.getFullName(), user.getRole().name());
        return new AuthResponse(token, "Bearer", jwtService.getAccessMinutes() * 60, userDto);
    }
}