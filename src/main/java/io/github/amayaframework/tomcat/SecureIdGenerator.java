package io.github.amayaframework.tomcat;

import java.security.SecureRandom;

/**
 *
 */
public final class SecureIdGenerator extends AbstractIdGenerator {
    private final SecureRandom random;

    public SecureIdGenerator(SecureRandom random) {
        this.random = random;
    }

    @Override
    protected void nextBytes(byte[] buf) {
        random.nextBytes(buf);
    }
}
