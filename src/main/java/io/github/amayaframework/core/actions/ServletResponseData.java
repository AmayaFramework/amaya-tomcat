package io.github.amayaframework.core.actions;

import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.pipeline.ResponseData;

import javax.servlet.http.HttpServletResponse;

/**
 * A simple container created to transfer data between output pipeline actions.
 */
public class ServletResponseData implements ResponseData {
    protected final HttpServletResponse servletResponse;
    protected final HttpResponse response;

    public ServletResponseData(HttpServletResponse servletResponse, HttpResponse response) {
        this.servletResponse = servletResponse;
        this.response = response;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    @Override
    public HttpResponse getResponse() {
        return response;
    }
}
