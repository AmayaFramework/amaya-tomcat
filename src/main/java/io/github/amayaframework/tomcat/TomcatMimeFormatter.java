package io.github.amayaframework.tomcat;

import io.github.amayaframework.http.MimeData;
import io.github.amayaframework.server.MimeFormatter;

final class TomcatMimeFormatter implements MimeFormatter {

    @Override
    public String format(MimeData mimeData) {
        return mimeData.toString();
    }
}
