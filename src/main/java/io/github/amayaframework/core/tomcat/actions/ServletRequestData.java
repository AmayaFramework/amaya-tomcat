package io.github.amayaframework.core.tomcat.actions;

import io.github.amayaframework.core.ConfigProvider;
import io.github.amayaframework.core.methods.HttpMethod;
import io.github.amayaframework.core.pipeline.AbstractRequestData;
import io.github.amayaframework.core.routes.MethodRoute;

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
    private final Charset charset = ConfigProvider.getConfig().getCharset();

    public ServletRequestData(HttpServletRequest request, HttpMethod method, String path, MethodRoute route)
            throws IOException {
        super(method, path, route);
        this.servletRequest = request;
        this.inputStream = servletRequest.getInputStream();
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
        return servletRequest.getContentType();
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
