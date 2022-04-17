package io.github.amayaframework.core.tomcat.contexts;

import io.github.amayaframework.core.contexts.AbstractHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServletHttpRequest extends AbstractHttpRequest {
    protected final HttpServletRequest servletRequest;

    public ServletHttpRequest(HttpServletRequest servletRequest) {
        this.servletRequest = Objects.requireNonNull(servletRequest);
    }

    @Override
    public List<String> getHeaders(String key) {
        return Collections.list(servletRequest.getHeaders(key));
    }
}
