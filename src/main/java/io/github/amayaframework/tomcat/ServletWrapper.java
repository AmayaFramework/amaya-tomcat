package io.github.amayaframework.tomcat;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import org.apache.catalina.core.StandardWrapper;

final class ServletWrapper extends StandardWrapper {
    private final Servlet servlet;

    ServletWrapper(Servlet servlet) {
        this.servlet = servlet;
        this.asyncSupported = true;
        this.instanceInitialized = false;
    }

    @Override
    public synchronized Servlet loadServlet() throws ServletException {
        if (!instanceInitialized) {
            servlet.init(facade);
            instanceInitialized = true;
        }
        return servlet;
    }

    @Override
    public long getAvailable() {
        return 0;
    }

    @Override
    public boolean isUnavailable() {
        return false;
    }

    @Override
    public Servlet getServlet() {
        return servlet;
    }

    @Override
    public String getServletClass() {
        return servlet.getClass().getName();
    }
}
