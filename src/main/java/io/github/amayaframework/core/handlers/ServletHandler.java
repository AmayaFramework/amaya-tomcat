package io.github.amayaframework.core.handlers;

import com.github.romanqed.jutils.pipeline.PipelineResult;
import io.github.amayaframework.core.configurators.BaseServletConfigurator;
import io.github.amayaframework.core.controllers.Controller;
import io.github.amayaframework.core.methods.HttpMethod;
import io.github.amayaframework.core.pipelines.ServletRequestData;
import io.github.amayaframework.core.util.AmayaConfig;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * <p>A class representing the servlet handler used inside the server. Built on pipelines.</p>
 * <p>The http connection processing scheme is quite simple: first, the input pipeline is triggered,
 * resulting in an HttpResponse. Then the output pipeline is triggered, the purpose of which is to
 * process and verify the received HttpResponse. After that, the server receives a response.</p>
 */
public class ServletHandler extends HttpServlet {
    private final IOHandler handler;
    private final ServletWrapper wrapper;

    public ServletHandler(Controller controller) {
        handler = new BaseIOHandler(controller, Collections.singletonList(new BaseServletConfigurator()));
        wrapper = new ServletWrapper(LoggerFactory.getLogger(getClass()), AmayaConfig.INSTANCE.getCharset());
    }

    public IOHandler getHandler() {
        return handler;
    }

    protected void doMethod(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletRequestData requestData = new ServletRequestData(req, method);
        PipelineResult processResult = handler.process(requestData);
        wrapper.process(resp, processResult);
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
