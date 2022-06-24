package io.github.amayaframework.core.tomcat.actions;

import com.github.romanqed.util.Checks;
import io.github.amayaframework.core.pipeline.InputAction;
import io.github.amayaframework.core.tomcat.contexts.ServletHttpRequest;
import io.github.amayaframework.core.util.ParseUtil;
import io.github.amayaframework.http.HttpCode;
import io.github.amayaframework.http.HttpUtil;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>An input action during which the basic components of the request will be checked and parsed:
 * query parameters, path parameters, headers and the request body.</p>
 * <p>Receives: {@link ServletRequestData}</p>
 * <p>Returns: {@link ServletRequestData}</p>
 */
public class ParseRequestAction extends InputAction<ServletRequestData, ServletRequestData> {

    @Override
    public ServletRequestData execute(ServletRequestData data) {
        HttpServletRequest servletRequest = data.servletRequest;
        Charset charset = data.getCharset();
        Map<String, List<String>> query = Checks.safetyCall(
                () -> HttpUtil.parseQueryString(servletRequest.getQueryString(), charset),
                () -> new HashMap<>()
        );
        Map<String, Object> params = null;
        try {
            params = ParseUtil.extractRouteParameters(data.getRoute(), data.getPath());
        } catch (Exception e) {
            reject(HttpCode.BAD_REQUEST);
        }
        ServletHttpRequest request = new ServletHttpRequest(servletRequest);
        request.setCharset(charset);
        request.setQuery(query);
        request.setPathParameters(params);
        data.setRequest(request);
        return data;
    }
}
