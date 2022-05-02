package io.github.amayaframework.core.tomcat.handlers;

import com.github.romanqed.util.Action;
import com.github.romanqed.util.IOUtil;
import io.github.amayaframework.core.config.AmayaConfig;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.contexts.Responses;
import io.github.amayaframework.core.controllers.Controller;
import io.github.amayaframework.core.handlers.Session;
import io.github.amayaframework.core.methods.HttpMethod;
import io.github.amayaframework.core.pipeline.RouteData;
import io.github.amayaframework.core.routers.MethodRouter;
import io.github.amayaframework.core.routes.MethodRoute;
import io.github.amayaframework.core.tomcat.actions.ServletRequestData;
import io.github.amayaframework.core.tomcat.actions.ServletResponseData;
import io.github.amayaframework.core.util.ParseUtil;
import io.github.amayaframework.http.ContentType;
import io.github.amayaframework.http.HttpCode;
import io.github.amayaframework.http.HttpUtil;

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
        String path = request.getRequestURI().substring(length);
        path = ParseUtil.normalizeRoute(path);
        MethodRoute route = router.follow(method, path);
        if (route == null) {
            HttpCode code = HttpCode.NOT_FOUND;
            return Responses.responseWithCode(code, code.getMessage());
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
        String message = code.getMessage() + "\n";
        if (e != null && config.isDebug()) {
            message += IOUtil.getStackTrace(e) + "\n";
            Throwable caused = e.getCause();
            if (caused != null) {
                message += "Caused by: \n" + IOUtil.getStackTrace(caused);
            }
        }
        reject(code, message);
    }

    @Override
    public void reject(HttpCode code, String message) throws IOException {
        String header = HttpUtil.generateContentHeader(ContentType.PLAIN, config.getCharset());
        response.setContentType(header);
        String toSend;
        if (message != null) {
            toSend = message;
        } else {
            toSend = code.getMessage();
        }
        response.sendError(code.getCode(), toSend);
    }
}
