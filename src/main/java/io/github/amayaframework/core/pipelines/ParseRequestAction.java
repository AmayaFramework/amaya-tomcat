package io.github.amayaframework.core.pipelines;

import com.github.romanqed.jutils.http.HttpCode;
import com.github.romanqed.jutils.util.Checks;
import io.github.amayaframework.core.contexts.ServletHttpRequest;
import io.github.amayaframework.core.util.ParseUtil;

import javax.servlet.http.HttpServletRequest;
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
    public ServletRequestData execute(ServletRequestData requestData) {
        HttpServletRequest servletRequest = requestData.servletRequest;
        Map<String, List<String>> query = Checks.requireNonException(
                () -> ParseUtil.parseQueryString(servletRequest.getQueryString(), requestData.getCharset()),
                HashMap::new
        );
        Map<String, Object> params = null;
        try {
            params = ParseUtil.extractRouteParameters(requestData.getRoute(), requestData.getPath());
        } catch (Exception e) {
            reject(HttpCode.BAD_REQUEST);
        }
        ServletHttpRequest request = new ServletHttpRequest(servletRequest);
        request.setMethod(requestData.getMethod());
        request.setQuery(query);
        request.setPathParameters(params);
        requestData.setRequest(request);
        return requestData;
    }
}
