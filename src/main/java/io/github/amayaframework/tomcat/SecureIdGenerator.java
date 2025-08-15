package io.github.amayaframework.tomcat;

import java.security.SecureRandom;

/**
 * A {@link org.apache.catalina.SessionIdGenerator} implementation that uses a provided
 * {@link SecureRandom} instance as the source of randomness.
 *
 * <p>This allows the caller to control the random number generator's
 * algorithm, provider, and seeding strategy.
 */
public final class SecureIdGenerator extends AbstractIdGenerator {
    private final SecureRandom random;

    /**
     * Creates a new {@code SecureIdGenerator} that will generate session IDs
     * using the given {@link SecureRandom} instance.
     *
     * @param random the secure random number generator to use
     */
    public SecureIdGenerator(SecureRandom random) {
        this.random = random;
    }

    @Override
    protected void nextBytes(byte[] buf) {
        random.nextBytes(buf);
    }
}
