package io.github.amayaframework.core.tomcat.actions;

import io.github.amayaframework.core.ConfigProvider;
import io.github.amayaframework.core.contexts.ContentType;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.pipeline.PipelineAction;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * <p>The output action during which the response headers is sent.</p>
 * <p>Receives: {@link ServletResponseData}</p>
 * <p>Returns: {@link ServletResponseData}</p>
 */
public class ProcessHeadersAction extends PipelineAction<ServletResponseData, ServletResponseData> {
    private final Charset charset = ConfigProvider.getConfig().getCharset();

    @Override
    public ServletResponseData execute(ServletResponseData responseData) {
        HttpServletResponse servletResponse = responseData.servletResponse;
        HttpResponse response = responseData.getResponse();
        servletResponse.setStatus(response.getCode().getCode());
        response.getHeaderMap().forEach((key, value) -> value.forEach(e -> servletResponse.addHeader(key, e)));
        ContentType type = response.getContentType();
        if (response.getBody() != null || (type != null && !type.isString())) {
            servletResponse.setContentType(type.getHeader());
            servletResponse.setCharacterEncoding(charset.name().toLowerCase(Locale.ROOT));
        }
        response.getCookies().forEach(servletResponse::addCookie);
        return responseData;
    }
}
