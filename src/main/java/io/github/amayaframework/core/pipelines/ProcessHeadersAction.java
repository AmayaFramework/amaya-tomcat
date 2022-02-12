package io.github.amayaframework.core.pipelines;

import io.github.amayaframework.core.contexts.ContentType;
import io.github.amayaframework.core.contexts.HttpResponse;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>The output action during which the response headers is sent.</p>
 * <p>Receives: {@link ServletResponseData}</p>
 * <p>Returns: {@link ServletResponseData}</p>
 */
public class ProcessHeadersAction extends PipelineAction<ServletResponseData, ServletResponseData> {
    @Override
    public ServletResponseData execute(ServletResponseData responseData) {
        HttpServletResponse servletResponse = responseData.servletResponse;
        HttpResponse response = responseData.response;
        servletResponse.setStatus(response.getCode().getCode());
        response.getHeaderMap().forEach((key, value) -> value.forEach(e -> servletResponse.addHeader(key, e)));
        ContentType type = response.getContentType();
        if (response.getBody() != null || (type != null && !type.isString())) {
            servletResponse.setContentType(type.getHeader());
        }
        response.getCookies().forEach(servletResponse::addCookie);
        return responseData;
    }
}
