package io.github.amayaframework.core.handlers;

import com.github.romanqed.jutils.http.HttpCode;
import com.github.romanqed.jutils.util.Action;
import io.github.amayaframework.core.config.AmayaConfig;
import io.github.amayaframework.core.contexts.ContentType;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.contexts.Responses;
import io.github.amayaframework.core.controllers.Controller;
import io.github.amayaframework.core.methods.HttpMethod;
import io.github.amayaframework.core.pipelines.ServletRequestData;
import io.github.amayaframework.core.pipelines.ServletResponseData;
import io.github.amayaframework.core.routers.MethodRouter;
import io.github.amayaframework.core.routes.MethodRoute;
import io.github.amayaframework.core.util.ParseUtil;

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
        this.length = controller.getPath().length();
    }

    public void setConfig(AmayaConfig config) {
        this.config = config;
    }

    @Override
    public HttpResponse handleInput(Action<Object, Object> handler) throws Exception {
        String path = request.getRequestURI().substring(length);
        path = ParseUtil.normalizePath(path);
        MethodRoute route = router.follow(method, path);
        if (route == null) {
            HttpCode code = HttpCode.NOT_FOUND;
            return Responses.responseWithCode(code, code.getMessage());
        }
        ServletRequestData requestData = new ServletRequestData(request, method, path, route);
        return (HttpResponse) handler.execute(requestData);
    }

    @Override
    public void handleOutput(Action<Object, Object> handler, HttpResponse response) throws Exception {
        ServletResponseData responseData = new ServletResponseData(this.response, response);
        handler.execute(responseData);
    }

    @Override
    public void reject(Exception e) throws IOException {
        HttpCode code = HttpCode.INTERNAL_SERVER_ERROR;
        String message = code.getMessage() + "\n";
        if (e != null && config.isDebug()) {
            message += ParseUtil.throwableToString(e) + "\n";
            Throwable caused = e.getCause();
            if (caused != null) {
                message += "Caused by: \n" + ParseUtil.throwableToString(caused);
            }
        }
        reject(code, message);
    }

    @Override
    public void reject(HttpCode code, String message) throws IOException {
        String header = ParseUtil.generateContentHeader(ContentType.PLAIN, config.getCharset());
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
