package io.github.amayaframework.tomcat;

import com.github.romanqed.jct.CancelToken;
import com.github.romanqed.juni.UniRunnable1;
import io.github.amayaframework.context.HttpContext;
import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.server.HttpServer;
import io.github.amayaframework.server.HttpServerConfig;
import io.github.amayaframework.service.AbstractService;
import io.github.amayaframework.service.ServiceCallback;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.net.InetSocketAddress;

final class TomcatHttpServer extends AbstractService implements HttpServer {
    private static final String SERVLET_NAME = "AmayaServlet";

    private final Tomcat tomcat;
    private final AddressSet addresses;
    private final TomcatHttpConfig config;
    private final HttpMethodBuffer methodBuffer;
    private final HttpCodeBuffer codeBuffer;
    private final boolean preferAsync;
    private final Context context;
    private UniRunnable1<HttpContext> runnable;

    TomcatHttpServer(Tomcat tomcat,
                    AddressSet addresses,
                    TomcatHttpConfig config,
                    Context context,
                    HttpMethodBuffer methodBuffer,
                    HttpCodeBuffer codeBuffer,
                    boolean preferAsync) {
        this.tomcat = tomcat;
        this.addresses = addresses;
        this.config = config;
        this.context = context;
        this.methodBuffer = methodBuffer;
        this.codeBuffer = codeBuffer;
        this.preferAsync = preferAsync;
    }

    @Override
    public void bind(InetSocketAddress address) {
        addresses.add(address);
    }

    @Override
    public void bind(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Illegal port: " + port);
        }
        addresses.add(new InetSocketAddress(port));
    }

    @Override
    public ServletContext servletContext() {
        return context.getServletContext();
    }

    @Override
    public void bind(InetSocketAddress address, HttpVersion version) {
        addresses.add(address, version);
    }

    @Override
    public void bind(int port, HttpVersion version) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Illegal port: " + port);
        }
        addresses.add(new InetSocketAddress(port), version);
    }

    @Override
    public HttpServerConfig config() {
        return config;
    }

    @Override
    public UniRunnable1<HttpContext> handler() {
        return runnable;
    }

    @Override
    public void handler(UniRunnable1<HttpContext> handler) {
        this.runnable = handler;
    }

    private Servlet createSyncServlet() {
        return new SyncTomcatServlet(
                methodBuffer,
                codeBuffer,
                config.version,
                config.tokenizer,
                config.parser,
                config.formatter,
                runnable
        );
    }

    private Servlet createAsyncServlet() {
        return new AsyncTomcatServlet(
                methodBuffer,
                codeBuffer,
                config.version,
                config.tokenizer,
                config.parser,
                config.formatter,
                runnable
        );
    }

    private Servlet createServlet() {
        if (runnable == null) {
            return EmptyTomcatServlet.EMPTY_SERVLET;
        }
        // 1. isSync() => run()
        // 2. isAsync() => runAsync()
        // 3. isUni() / unknown => preferAsync ? runAsync() : run()
        if (runnable.isSync()) {
            return createSyncServlet();
        }
        if (runnable.isAsync()) {
            return createAsyncServlet();
        }
        return preferAsync ? createAsyncServlet() : createSyncServlet();
    }

    @Override
    protected void doStart(CancelToken token, ServiceCallback callback) throws Throwable {
        if (token.canceled()) {
            return;
        }
        var servlet = createServlet();
        // Register servlet to / path for generic path catch
        tomcat.addServlet("", SERVLET_NAME, servlet);
        context.addServletMappingDecoded("/", SERVLET_NAME);
        tomcat.start();
    }

    @Override
    protected void doStop(CancelToken token) throws Throwable {
        if (token.canceled()) {
            return;
        }
        tomcat.stop();
    }

    @Override
    protected void doDispose() {
        try {
            tomcat.stop();
            tomcat.destroy();
        } catch (Exception ignored) {
            // No exceptions while disposing
        }
    }
}
