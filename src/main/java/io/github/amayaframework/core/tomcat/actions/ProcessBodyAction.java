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
import java.io.OutputStream;
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

    private BufferedWriter wrapStream(OutputStream stream, Charset charset) {
        OutputStreamWriter writer = new OutputStreamWriter(stream, charset == null ? this.charset : charset);
        return new BufferedWriter(writer);
    }

    @Override
    public Void execute(ServletResponseData data) throws Throwable {
        HttpServletResponse servletResponse = data.servletResponse;
        HttpResponse response = data.getResponse();
        ContentType type = response.getContentType();
        Handler<FixedOutputStream> handler = response.getOutputStreamHandler();
        if (handler != null) {
            FixedOutputStream stream = new ServletOutputStream(servletResponse);
            handler.handle(stream);
            long remaining = stream.getRemainingLength();
            if (remaining != 0) {
                throw new IllegalStateException("Not all data has been sent, " + remaining + " bytes are left");
            }
            stream.flush();
            return null;
        }
        if (type != null && type.isString()) {
            Charset charset = response.getCharset();
            BufferedWriter writer = wrapStream(servletResponse.getOutputStream(), charset);
            Object body = response.getBody();
            if (body != null) {
                writer.write(body.toString());
                writer.flush();
            }
        }
        return null;
    }
}
