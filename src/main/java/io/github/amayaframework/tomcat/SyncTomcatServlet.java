package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Runnable0;
import com.github.romanqed.jfunc.Runnable1;
import io.github.amayaframework.context.HttpContext;
import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.server.MimeFormatter;
import io.github.amayaframework.server.MimeParser;
import io.github.amayaframework.server.PathTokenizer;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class SyncTomcatServlet extends AbstractTomcatConfiguredServlet {
    private final Runnable1<HttpContext> handler;

    SyncTomcatServlet(Runnable1<ServletConfig> onInit,
                      Runnable0 onDestroy,
                      HttpMethodBuffer methodBuffer,
                      HttpCodeBuffer codeBuffer,
                      HttpVersion version,
                      PathTokenizer tokenizer,
                      MimeParser parser,
                      MimeFormatter formatter,
                      Runnable1<HttpContext> handler) {
        super(onInit, onDestroy, methodBuffer, codeBuffer, version, tokenizer, parser, formatter);
        this.handler = handler;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws Throwable {
        var context = buildContext(req, res);
        if (context != null) {
            handler.run(context);
        }
    }
}
