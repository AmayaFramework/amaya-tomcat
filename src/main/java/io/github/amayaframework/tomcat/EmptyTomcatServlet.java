package io.github.amayaframework.tomcat;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class EmptyTomcatServlet extends AbstractTomcatServlet {
    static final Servlet EMPTY_SERVLET = new EmptyTomcatServlet();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) {
        // Do nothing
    }
}
