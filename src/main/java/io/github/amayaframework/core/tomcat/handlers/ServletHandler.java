package io.github.amayaframework.core.tomcat.handlers;

import io.github.amayaframework.core.ConfigProvider;
import io.github.amayaframework.core.config.AmayaConfig;
import io.github.amayaframework.core.controllers.Controller;
import io.github.amayaframework.core.handlers.PipelineHandler;
import io.github.amayaframework.core.methods.HttpMethod;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>A class representing the servlet handler used inside the server. Built on pipelines.</p>
 * <p>The http connection processing scheme is quite simple: first, the input pipeline is triggered,
 * resulting in an HttpResponse. Then the output pipeline is triggered, the purpose of which is to
 * process and verify the received HttpResponse. After that, the server receives a response.</p>
 */
public class ServletHandler extends HttpServlet {
    private final PipelineHandler handler;
    private final Controller controller;
    private final AmayaConfig config;

    public ServletHandler(Controller controller) {
        handler = new PipelineHandler();
        this.controller = controller;
        config = ConfigProvider.getConfig();
    }

    public PipelineHandler getHandler() {
        return handler;
    }

    protected void doMethod(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletSession session = new ServletSession(method, req, resp);
        session.setConfig(config);
        session.setController(controller);
        handler.handle(session);
        req.getInputStream().close();
        resp.getOutputStream().close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doMethod(HttpMethod.GET, req, resp);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doMethod(HttpMethod.HEAD, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doMethod(HttpMethod.POST, req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doMethod(HttpMethod.PUT, req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doMethod(HttpMethod.DELETE, req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doMethod(HttpMethod.OPTIONS, req, resp);
    }
}
