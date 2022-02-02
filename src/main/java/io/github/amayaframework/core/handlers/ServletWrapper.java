package io.github.amayaframework.core.handlers;

import com.github.romanqed.jutils.http.HttpCode;
import io.github.amayaframework.core.contexts.ContentType;
import io.github.amayaframework.core.contexts.HttpResponse;
import io.github.amayaframework.core.contexts.StreamHandler;
import io.github.amayaframework.core.util.ParseUtil;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;

public class ServletWrapper extends AbstractSourceWrapper<HttpServletResponse> {
    public ServletWrapper(Logger logger, Charset charset) {
        super(logger, charset);
    }

    private void setResponseData(HttpServletResponse servletResponse, HttpResponse response) {
        response.getHeaderMap().forEach((key, value) -> value.forEach(e -> servletResponse.addHeader(key, e)));
        servletResponse.setStatus(response.getCode().getCode());
    }

    @Override
    public void sendStringResponse(HttpServletResponse source, HttpResponse response) throws IOException {
        setResponseData(source, response);
        BufferedWriter writer = wrapOutputStream(source.getOutputStream());
        Object body = response.getBody();
        if (body != null) {
            writer.write(body.toString());
        }
        writer.close();
    }

    @Override
    public void sendStreamResponse(HttpServletResponse source, HttpResponse response) throws IOException {
        StreamHandler handler = response.getOutputStreamHandler();
        if (handler == null) {
            setResponseData(source, response);
            return;
        }
        handler.accept(source.getOutputStream());
        if (!handler.isSuccessful()) {
            reject(source, handler.getException());
            return;
        }
        setResponseData(source, response);
        source.setContentLength(handler.getContentLength());
        handler.flush();
    }

    @Override
    public void reject(HttpServletResponse source, HttpCode code, String message) throws IOException {
        String header = ParseUtil.generateContentHeader(ContentType.PLAIN, charset);
        source.setContentType(header);
        String toSend;
        if (message != null) {
            toSend = message;
        } else {
            toSend = code.getMessage();
        }
        source.sendError(code.getCode(), toSend);
    }
}
