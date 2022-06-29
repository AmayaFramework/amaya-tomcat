package io.github.amayaframework.core.tomcat.actions;

import io.github.amayaframework.core.actions.WithConfig;
import io.github.amayaframework.core.config.AmayaConfig;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.pipeline.PipelineAction;
import io.github.amayaframework.http.ContentType;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.Locale;

/**
 * <p>The output action during which the response headers is sent.</p>
 * <p>Receives: {@link ServletResponseData}</p>
 * <p>Returns: {@link ServletResponseData}</p>
 */
@WithConfig
public class ProcessHeadersAction extends PipelineAction<ServletResponseData, ServletResponseData> {
    private final Charset charset;

    public ProcessHeadersAction(AmayaConfig config) {
        this.charset = config.getCharset();
    }

    @Override
    public ServletResponseData execute(ServletResponseData data) {
        HttpServletResponse servletResponse = data.servletResponse;
        HttpResponse response = data.getResponse();
        servletResponse.setStatus(response.getCode().getCode());
        response.getHeaderMap().forEach((key, value) -> value.forEach(e -> servletResponse.addHeader(key, e)));
        ContentType type = response.getContentType();
        if (type != null && (response.getOutputStreamHandler() != null || response.getBody() != null)) {
            servletResponse.setContentType(type.getHeader());
            if (type.isString()) {
                Charset charset = response.getCharset();
                String charsetName = (charset == null ? this.charset : charset).name().toLowerCase(Locale.ROOT);
                servletResponse.setCharacterEncoding(charsetName);
            }
        }
        response.getCookies().forEach(servletResponse::addCookie);
        data.complete();
        return data;
    }
}
