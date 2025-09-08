package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Runnable0;
import com.github.romanqed.jfunc.Runnable1;
import com.github.romanqed.jsync.AsyncRunnable1;
import io.github.amayaframework.context.HttpContext;
import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.server.*;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class AsyncTomcatServlet extends AbstractTomcatConfiguredServlet {
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
    protected void service(HttpServletRequest req, HttpServletResponse res) throws Throwable {
        var asyncCtx = req.startAsync();
        try {
            var context = buildContext(
                    (HttpServletRequest) asyncCtx.getRequest(),
                    (HttpServletResponse) asyncCtx.getResponse()
            );
            if (context == null) {
                asyncCtx.complete();
                return;
            }
            handler.runAsync(context).whenComplete((v, t) -> asyncCtx.complete());
        } catch (Throwable e) {
            asyncCtx.complete();
            throw e;
        }
    }
}
