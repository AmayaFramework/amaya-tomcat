package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Runnable0;
import com.github.romanqed.jfunc.Runnable1;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

abstract class AbstractTomcatServlet implements Servlet {
    private final Runnable1<ServletConfig> onInit;
    private final Runnable0 onDestroy;
    private ServletConfig config;

    protected AbstractTomcatServlet(Runnable1<ServletConfig> onInit, Runnable0 onDestroy) {
        this.onInit = onInit;
        this.onDestroy = onDestroy;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        try {
            if (onInit != null) {
                onInit.run(config);
            }
        } catch (Error | RuntimeException | ServletException e) {
            throw e;
        } catch (Throwable e) {
            throw new ServletException("Failed to initialize amaya servlet", e);
        }
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
        try {
            if (onDestroy != null) {
                onDestroy.run();
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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
