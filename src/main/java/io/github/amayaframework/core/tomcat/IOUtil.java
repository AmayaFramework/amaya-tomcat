package io.github.amayaframework.core.tomcat;

import java.io.IOException;

import static com.github.romanqed.util.IOUtil.readResourceFile;

final class IOUtil {
    private static final String ART = "art.txt";
    private static final String LOGO = "logo.txt";

    static String readLogo() throws IOException {
        return readResourceFile(LOGO);
    }

    static String readArt() throws IOException {
        return readResourceFile(ART);
    }
}
