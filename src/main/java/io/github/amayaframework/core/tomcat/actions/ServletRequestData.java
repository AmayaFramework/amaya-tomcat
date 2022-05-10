package io.github.amayaframework.core.tomcat.actions;

import io.github.amayaframework.core.pipeline.AbstractRequestData;
import io.github.amayaframework.core.pipeline.RouteData;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A simple container created to transfer data between input pipeline actions.
 */
public class ServletRequestData extends AbstractRequestData {
    protected final HttpServletRequest servletRequest;
    private final InputStream inputStream;
    private final Charset charset;

    public ServletRequestData(HttpServletRequest request, RouteData data, Charset charset) throws IOException {
        super(data);
        this.servletRequest = request;
        this.inputStream = servletRequest.getInputStream();
        this.charset = charset;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public String getContentType() {
        String contentHeader = servletRequest.getContentType();
        if (contentHeader == null) {
            return null;
        }
        int position = contentHeader.indexOf(';');
        if (position < 0) {
            return contentHeader;
        }
        return contentHeader.substring(0, position);
    }

    @Override
    public Charset getCharset() {
        try {
            return Charset.forName(servletRequest.getCharacterEncoding());
        } catch (Exception e) {
            return charset;
        }
    }
}
