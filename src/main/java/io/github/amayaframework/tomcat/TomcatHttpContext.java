package io.github.amayaframework.tomcat;

import io.github.amayaframework.context.AbstractContext;
import io.github.amayaframework.context.HttpContext;
import io.github.amayaframework.context.HttpRequest;
import io.github.amayaframework.context.HttpResponse;
import io.github.amayaframework.server.ServerHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class TomcatHttpContext extends AbstractContext<HttpServletRequest, HttpServletResponse> implements HttpContext {
    // Amaya context
    private final TomcatRequest request;
    private final ServerHttpResponse response;

    TomcatHttpContext(HttpServletRequest servletRequest,
                      HttpServletResponse servletResponse,
                      TomcatRequest request,
                      ServerHttpResponse response) {
        super(servletRequest, servletResponse);
        // Amaya context
        this.request = request;
        this.response = response;
    }

    @Override
    public HttpRequest request() {
        return request;
    }

    @Override
    public HttpResponse response() {
        return response;
    }
}
