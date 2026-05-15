package com.task.harbor.infrastructure.security.totp;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TotpService {

    private final SecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;
    private final String issuer;

    public TotpService(@Value("${app.security.totp-issuer}") String issuer) {
        this.secretGenerator = new DefaultSecretGenerator();
        TimeProvider timeProvider = new SystemTimeProvider();
        DefaultCodeGenerator codeGenerator = new DefaultCodeGenerator();
        this.codeVerifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        this.issuer = issuer;
    }

    public String generateSecret() {
        return secretGenerator.generate();
    }

    public String generateQrCodeUrl(String secret, String email) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, email, secret, issuer);
    }

    public boolean verifyCode(String secret, String code) {
        return codeVerifier.isValidCode(secret, code);
    }
}