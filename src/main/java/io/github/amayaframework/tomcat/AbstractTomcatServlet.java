package io.github.amayaframework.tomcat;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

abstract class AbstractTomcatServlet implements Servlet {
    private ServletConfig config;

    @Override
    public void init(ServletConfig config) {
        this.config = config;
    }

    @Override
    public ServletConfig getServletConfig() {
        return config;
    }

    @Override
    public String getServletInfo() {
        return "Amaya Tomcat integration for Tomcat 10 (module: amaya-tomcat)";
    }

    @Override
    public void destroy() {
        // Do nothing
    }

    protected abstract void service(HttpServletRequest req, HttpServletResponse res) throws Throwable;

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        try {
            service((HttpServletRequest) req, (HttpServletResponse) res);
        } catch (Error | RuntimeException | IOException | ServletException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServletException("Amaya Tomcat integration servlet failed", e);
        }
    }
}
