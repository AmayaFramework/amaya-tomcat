package io.github.amayaframework.core.actions;

import com.github.romanqed.jutils.util.Handler;
import io.github.amayaframework.core.config.ConfigProvider;
import io.github.amayaframework.core.contexts.ContentType;
import io.github.amayaframework.core.contexts.FixedOutputStream;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.pipeline.PipelineAction;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * <p>The output action during which the response body is sent.</p>
 * <p>Receives: {@link ServletResponseData}</p>
 * <p>Returns: {@link Void}</p>
 */
public class ProcessBodyAction extends PipelineAction<ServletResponseData, Void> {
    private final Charset charset = ConfigProvider.getConfig().getCharset();

    @Override
    public Void execute(ServletResponseData responseData) throws Exception {
        HttpServletResponse servletResponse = responseData.servletResponse;
        HttpResponse response = responseData.response;
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
            OutputStreamWriter streamWriter = new OutputStreamWriter(servletResponse.getOutputStream(), charset);
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
