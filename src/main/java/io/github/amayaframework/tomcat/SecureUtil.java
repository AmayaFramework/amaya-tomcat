package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Exceptions;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

final class SecureUtil {
    private static final String DRBG_ALGORITHM = "DRBG";
    private static final String DEFAULT_ALGORITHM = decideDefault();

    private static String decideDefault() {
        if (Security.getAlgorithms("SecureRandom").contains(DRBG_ALGORITHM)) {
            return DRBG_ALGORITHM;
        }
        return "";
    }

    static SecureRandom getSecureRandom() {
        try {
            return SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            if (DEFAULT_ALGORITHM.isBlank()) {
                return new SecureRandom();
            }
            return Exceptions.silent(() -> SecureRandom.getInstance(DEFAULT_ALGORITHM));
        }
    }
}
