package io.github.amayaframework.core.tomcat.actions;

import io.github.amayaframework.core.contexts.FixedOutputStream;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

final class ServletOutputStream extends FixedOutputStream {
    private final HttpServletResponse response;

    public ServletOutputStream(HttpServletResponse response) throws IOException {
        super(response.getOutputStream());
        this.response = response;
    }

    @Override
    public void specifyLength(long length) throws IOException {
        super.specifyLength(length);
        response.setContentLengthLong(length);
    }
}
