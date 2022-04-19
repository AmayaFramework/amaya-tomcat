package io.github.amayaframework.core.tomcat.actions;

import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.pipeline.AbstractResponseData;

import javax.servlet.http.HttpServletResponse;

/**
 * A simple container created to transfer data between output pipeline actions.
 */
public class ServletResponseData extends AbstractResponseData {
    protected final HttpServletResponse servletResponse;

    public ServletResponseData(HttpServletResponse servletResponse, HttpResponse response) {
        super(response);
        this.servletResponse = servletResponse;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }
}
