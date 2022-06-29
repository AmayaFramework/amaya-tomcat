package io.github.amayaframework.core.tomcat.actions;

import com.github.romanqed.util.Handler;
import io.github.amayaframework.core.actions.WithConfig;
import io.github.amayaframework.core.config.AmayaConfig;
import io.github.amayaframework.core.contexts.FixedOutputStream;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.pipeline.PipelineAction;
import io.github.amayaframework.http.ContentType;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * <p>The output action during which the response body is sent.</p>
 * <p>Receives: {@link ServletResponseData}</p>
 * <p>Returns: {@link Void}</p>
 */
@WithConfig
public class ProcessBodyAction extends PipelineAction<ServletResponseData, Void> {
    private final Charset charset;

    public ProcessBodyAction(AmayaConfig config) {
        this.charset = config.getCharset();
    }

    @Override
    public Void execute(ServletResponseData data) throws Throwable {
        HttpServletResponse servletResponse = data.servletResponse;
        HttpResponse response = data.getResponse();
        ContentType type = response.getContentType();
        Handler<FixedOutputStream> handler = response.getOutputStreamHandler();
        if (handler != null) {
            FixedOutputStream outputStream = new FixedOutputStream(servletResponse.getOutputStream()) {
                @Override
                public void specifyLength(long length) {
                    servletResponse.setContentLengthLong(length);
                }
            };
            handler.handle(outputStream);
            outputStream.flush();
            return null;
        }
        if (type != null && type.isString()) {
            Charset charset = response.getCharset();
            OutputStreamWriter streamWriter = new OutputStreamWriter(
                    servletResponse.getOutputStream(),
                    charset == null ? this.charset : charset);
            BufferedWriter writer = new BufferedWriter(streamWriter);
            Object body = response.getBody();
            if (body != null) {
                writer.write(body.toString());
                writer.flush();
            }
        }
        return null;
    }
}
