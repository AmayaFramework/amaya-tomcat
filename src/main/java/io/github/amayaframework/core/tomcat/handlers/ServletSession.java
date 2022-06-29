package io.github.amayaframework.core.tomcat.handlers;

import com.github.romanqed.util.Action;
import com.github.romanqed.util.IOUtil;
import io.github.amayaframework.core.config.AmayaConfig;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.controllers.Controller;
import io.github.amayaframework.core.handlers.Session;
import io.github.amayaframework.core.methods.HttpMethod;
import io.github.amayaframework.core.pipeline.RouteData;
import io.github.amayaframework.core.routers.MethodRouter;
import io.github.amayaframework.core.routes.MethodRoute;
import io.github.amayaframework.core.tomcat.actions.ServletRequestData;
import io.github.amayaframework.core.tomcat.actions.ServletResponseData;
import io.github.amayaframework.core.util.ParseUtil;
import io.github.amayaframework.http.HttpCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletSession implements Session {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final HttpMethod method;
    private AmayaConfig config;
    private MethodRouter router;
    private int length;
    private boolean isComplete;

    public ServletSession(HttpMethod method, HttpServletRequest request, HttpServletResponse response) {
        this.method = method;
        this.request = request;
        this.response = response;
    }

    public void setController(Controller controller) {
        this.router = controller.getRouter();
        this.length = controller.getRoute().length();
    }

    public void setConfig(AmayaConfig config) {
        this.config = config;
    }

    @Override
    public HttpResponse handleInput(Action<Object, Object> handler) throws Throwable {
        String path = ParseUtil.normalizeRoute(request.getRequestURI().substring(length));
        MethodRoute route = router.follow(method, path);
        if (route == null) {
            HttpCode code = HttpCode.NOT_FOUND;
            reject(code, code.getMessage());
            complete();
            return null;
        }
        RouteData data = new RouteData(method, path, route);
        ServletRequestData requestData = new ServletRequestData(request, data, config.getCharset());
        return (HttpResponse) handler.execute(requestData);
    }

    @Override
    public void handleOutput(Action<Object, Object> handler, HttpResponse response) throws Throwable {
        ServletResponseData responseData = new ServletResponseData(this.response, response);
        handler.execute(responseData);
    }

    @Override
    public void reject(Throwable e) throws IOException {
        HttpCode code = HttpCode.INTERNAL_SERVER_ERROR;
        String message;
        if (config.isDebug()) {
            message = IOUtil.getStackTrace(e);
        } else {
            message = e.getMessage();
        }
        reject(code, message);
    }

    @Override
    public void reject(HttpCode code, String message) throws IOException {
        response.reset();
        response.sendError(code.getCode(), message);
    }

    @Override
    public void complete() {
        isComplete = true;
    }

    @Override
    public boolean isCompleted() {
        return isComplete;
    }
}
