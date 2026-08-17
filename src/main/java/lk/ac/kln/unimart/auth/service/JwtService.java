package lk.ac.kln.unimart.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long accessMinutes;

    public JwtService(JwtEncoder jwtEncoder,
                      @Value("${app.security.access-minutes:15}") long accessMinutes) {
        this.jwtEncoder = jwtEncoder;
        this.accessMinutes = accessMinutes;
    }

    public String generateAccessToken(String subjectEmail) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(accessMinutes * 60);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("unimart-backend")
                .subject(subjectEmail)
                .issuedAt(now)
                .expiresAt(expiry)
                .build();

        JwsHeader header = JwsHeader.with(() -> "HS256").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    public long getAccessMinutes() {
        return accessMinutes;
    }
}