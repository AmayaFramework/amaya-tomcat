package io.github.amayaframework.tomcat;

import org.apache.catalina.SessionIdGenerator;

/**
 * An abstract base class for {@link SessionIdGenerator} implementations
 * that provides common handling of JVM route and session ID length,
 * as well as the default algorithm for encoding random bytes into
 * hexadecimal session identifiers.
 *
 * <p>Subclasses must implement {@link #nextBytes(byte[])} to supply
 * cryptographically strong or otherwise suitable random bytes.
 */
public abstract class AbstractIdGenerator implements SessionIdGenerator {
    protected String jvmRoute;
    protected int sessionIdLength = 16;

    @Override
    public String getJvmRoute() {
        return jvmRoute;
    }

    @Override
    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }

    @Override
    public int getSessionIdLength() {
        return sessionIdLength;
    }

    @Override
    public void setSessionIdLength(int sessionIdLength) {
        this.sessionIdLength = sessionIdLength;
    }

    @Override
    public String generateSessionId() {
        return generateSessionId(jvmRoute);
    }

    /**
     * Fills the provided byte array with random data.
     *
     * @param buf the byte array to fill with random bytes
     */
    protected abstract void nextBytes(byte[] buf);

    @Override
    public String generateSessionId(String route) {
        // Code from org.apache.catalina.util.StandardSessionIdGenerator
        /*
         * Licensed to the Apache Software Foundation (ASF) under one or more
         * contributor license agreements.  See the NOTICE file distributed with
         * this work for additional information regarding copyright ownership.
         * The ASF licenses this file to You under the Apache License, Version 2.0
         * (the "License"); you may not use this file except in compliance with
         * the License.  You may obtain a copy of the License at
         *
         *      http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         */
        var randomBuf = new byte[16];

        // Render the result as a String of hexadecimal digits
        // Start with enough space for sessionIdLength and medium route size
        var buffer = new StringBuilder(2 * sessionIdLength + 20);

        int resultLenBytes = 0;

        while (resultLenBytes < sessionIdLength) {
            nextBytes(randomBuf);
            for (int j = 0; j < randomBuf.length && resultLenBytes < sessionIdLength; j++) {
                byte b1 = (byte) ((randomBuf[j] & 0xf0) >> 4);
                byte b2 = (byte) (randomBuf[j] & 0x0f);
                if (b1 < 10) {
                    buffer.append((char) ('0' + b1));
                } else {
                    buffer.append((char) ('A' + (b1 - 10)));
                }
                if (b2 < 10) {
                    buffer.append((char) ('0' + b2));
                } else {
                    buffer.append((char) ('A' + (b2 - 10)));
                }
                resultLenBytes++;
            }
        }

        if (route != null && !route.isEmpty()) {
            buffer.append('.').append(route);
        } else if (jvmRoute != null && !jvmRoute.isEmpty()) {
            buffer.append('.').append(jvmRoute);
        }

        return buffer.toString();
    }
}
