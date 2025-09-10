package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Runnable0;
import com.github.romanqed.jfunc.Runnable1;
import com.github.romanqed.jsync.AsyncRunnable1;
import io.github.amayaframework.context.HttpContext;
import io.github.amayaframework.http.HttpCode;
import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.server.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.CancellationException;

final class AsyncTomcatServlet extends AbstractTomcatConfiguredServlet {
    private static final Log LOG = LogFactory.getLog(AsyncTomcatServlet.class);
    private final AsyncRunnable1<HttpContext> handler;

    AsyncTomcatServlet(Runnable1<ServletConfig> onInit,
                       Runnable0 onDestroy,
                       HttpMethodBuffer methodBuffer,
                       HttpCodeBuffer codeBuffer,
                       HttpVersion version,
                       HttpErrorHandler errorHandler,
                       PathTokenizer tokenizer,
                       MimeParser parser,
                       MimeFormatter formatter,
                       AsyncRunnable1<HttpContext> handler) {
        super(onInit, onDestroy, methodBuffer, codeBuffer, version, errorHandler, tokenizer, parser, formatter);
        this.handler = handler;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) {
        var asyncCtx = req.startAsync();
        var rs = (HttpServletResponse) asyncCtx.getResponse();
        try {
            var context = buildContext((HttpServletRequest) asyncCtx.getRequest(), rs);
            if (context == null) {
                asyncCtx.complete();
                return;
            }
            handler.runAsync(context).whenComplete((v, t) -> {
                try {
                    if (t != null) {
                        if (t instanceof CancellationException) {
                            LOG.debug("Async request cancelled");
                            rs.setStatus(204); // no content
                        } else {
                            LOG.error("Unhandled async exception", t);
                            errorHandler.handle(rs, HttpCode.INTERNAL_SERVER_ERROR, t.getMessage());
                        }
                    }
                } catch (IOException ioe) {
                    LOG.error("Failed to send error response", ioe);
                } finally {
                    asyncCtx.complete();
                }
            });
        } catch (Throwable e) {
            try {
                LOG.error("Synchronous error in async handler", e);
                errorHandler.handle(rs, HttpCode.INTERNAL_SERVER_ERROR, e.getMessage());
            } catch (IOException ioe) {
                LOG.error("Failed to send error response", ioe);
            } finally {
                asyncCtx.complete();
            }
        }
    }
}
